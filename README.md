

# OneStepMessage

## 简介

基于ViewModel和LiveData的消息总线框架。

## 有什么好处

**OneStepMessage**基于LiveData和ViewModel，所以LiveData的优点它都有(具体源码分析请查看上方LiveData到底是啥)：

- **确保界面符合数据状态**
- **不会发生内存泄漏**
- **不会因 Activity 停止而导致崩溃**
- **不再需要手动处理生命周期** 
- **数据始终保持最新状态**

对比EventBus和LiveDataBus而言，**OneStepMessage**具有的优势有：

- **可以编写模块消息，控制消息作用范围**

  单独模块创建ViewModel可以使消息的范围在一个模块里，在公共模块创建ViewModel可以使消息为全局消息，做到消息可控。

- **业务进行审查逻辑，控制消息发送入口，消息有唯一可信发送源**

  每一个message在创建的时候都要有业务自己传入审查代码，控制消息发送，该message的所有发送都要经过审查，可以确定唯一可信发送源。

  ```java
  class DemoViewModel : ViewModel() {
  
      //添加审查代码，控制唯一可信源，所有消息都可以业务自己控制是否发送
      val message1 = EventLiveData(object : EventLiveData.PostEventReview<String> {
          override fun review(value: String): Boolean {
  
              //TODO 消息发送审查代码，返回true审查通过可以发送，返回false审查不通过不可以发送
  
              return true
          }
      })
      val message2 = EventLiveData(object : EventLiveData.PostEventReview<Bean> {
          override fun review(value: Bean): Boolean {
  
              //TODO 消息发送审查代码，返回true审查通过可以发送，返回false审查不通过不可以发送
  
              return false
          }
      })
  }
  ```

  在EventLiveData.class中：

  ```java
      fun postEventValue(value: T) {
  
          if (!postEventReview.review(value)) {
              "消息审查失败，拒绝发送消息，请检查审查代码以及消息内容".showLogWithPosition(javaClass.simpleName)
              return
          }
  
          "post message : ${value.toString()} ; ".showLogWithPosition(javaClass.simpleName)
  
          super.postValue(Event(value))
      }
  ```

- **基于Jetpack库，无需引入其他三方库**

- **无需注册、注销，减少代码风险**

- **Log定位，每一条消息都可以直接定位到发送代码位置，方便调试**

![log.png](https://i.loli.net/2021/03/02/jKglNVvfeCOhLSo.png)

**下版本改进方向：**

可以参考Okhttp，使用责任链模式来优化审查代码逻辑。

## 使用方法

顾名思义，这个框架叫一步消息，所以使用方式也是一步的：

- **创建一个ViewModel来控制是模块消息还是全局消息。**

  ```java
  class DemoViewModel : ViewModel() {
  
      //添加审查代码，控制唯一可信源，所有消息都可以业务自己控制是否发送
      val message1 = EventLiveData(object : EventLiveData.PostEventReview<String> {
          override fun review(value: String): Boolean {
  
              //TODO 消息发送审查代码，返回true审查通过可以发送，返回false审查不通过不可以发送
  
              return true
          }
      })
      val message2 = EventLiveData(object : EventLiveData.PostEventReview<Bean> {
          override fun review(value: Bean): Boolean {
  
              //TODO 消息发送审查代码，返回true审查通过可以发送，返回false审查不通过不可以发送
  
              return false
          }
      })
  }
  ```

- **一行代码发送消息，一行代码订阅消息。**

  ```kotlin
          //发送一个消息1
          OSM.with(DemoViewModel::class.java).message1.postEventValue("更改message1了 random = ${(0..100).random()}")
         
          //监听消息1
          OSM.with(DemoViewModel::class.java).message1.observeEvent(this, ViewModelStore()){
              Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
          }
         
  ```

- **针对Java，添加了CallBack优化，提升编写体验。**

  ```java
          //发送一个消息1
          OSM.Companion.with(DemoViewModel.class).getMessage1().postEventValue("更改message1了 random = " + r.nextInt());
         
          //监听消息1
          OSM.Companion.with(DemoViewModel.class).getMessage1().observeEvent(this, new ViewModelStore(), new EventLiveData.OnChanged<String>() {
              @Override
              public void onChanged(String value) {
                  showMessage1.setText(value);
              }
          });
  ```

## 注意

- **订阅消息时，不传入ViewModelStore**

  先接收到的会消费消息，导致其他观察者无法收到消息。

- **订阅消息时，传入同一个ViewModelStore**

  先接收到的会消费消息，导致共用同一个ViewModelStore的观察者无法收到消息。

- **订阅消息时，传入不同ViewModelStore**

  单独接收消息，相互不影响。

造成这种情况是因为LiveData有一个问题，就是会发送多个重复消息给观察者，我这边包装处理Observer来防止此类问题：

```java
    /**
     * 事件只能被一个观察者消费
     */
    @MainThread
    fun observeEvent(owner: LifecycleOwner, onChanged: (T) -> Unit): Observer<Event<T>> {
        //拦截下发事件，判断时候需要下发
        val wrapperObserver = Observer<Event<T>>() {
            it.getContentIfNotHandled()?.let { data ->
                onChanged.invoke(data)
            }
        }
        //注册事件
        super.observe(owner, wrapperObserver)
        return wrapperObserver
    }

    /**
     * 事件可以被多个观察者消费，每个观察者只能消费一次
     */
    @MainThread
    fun observeEvent(
            owner: LifecycleOwner,
            viewModelStore: ViewModelStore,
            onChanged: (T) -> Unit
    ): Observer<Event<T>> {
        //拦截下发事件，判断该观察者是否需要下发
        val wrapperObserver = Observer<Event<T>>() {
            it.getContentIfNotHandled(viewModelStore)?.let() { data ->
                onChanged.invoke(data)
            }
        }
        //注册事件
        super.observe(owner, wrapperObserver)
        return wrapperObserver
    }
```

```java
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * 不同观察者分别记录分发状态
     * 如果该观察者没有下发过则下发数据
     * 否则返回null
     */
    fun getContentIfNotHandled(viewModelStore: ViewModelStore): T? {
        return if (map.contains(viewModelStore)) {
            null
        } else {
            map[viewModelStore] = true
            content
        }
    }
```

## License
```
Copyright 2020 The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
