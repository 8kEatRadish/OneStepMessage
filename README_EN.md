

# OneStepMessage

![forks](https://img.shields.io/github/forks/8kEatRadish/OneStepMessage) ![starts](https://img.shields.io/github/stars/8kEatRadish/OneStepMessage) ![issues](https://img.shields.io/github/issues/8kEatRadish/OneStepMessage) ![license](https://img.shields.io/github/license/8kEatRadish/OneStepMessage)

[![掘金](https://img.shields.io/badge/%E6%8E%98%E9%87%91-8kEatRadish-green)](https://juejin.cn/post/6946370050089549861) [![博客](https://img.shields.io/badge/%E5%8D%9A%E5%AE%A2-%E8%B4%B0%E6%8B%BE%E8%82%86%E7%9A%84%E5%AE%A0%E7%89%A9-green)](https://www.keeplovepet.cn/) 

> [![english](https://img.shields.io/badge/%E8%AF%AD%E8%A8%80-%E4%B8%AD%E6%96%87-green)](https://github.com/8kEatRadish/OneStepMessage/blob/master/README.md#onestepmessage)

## Introduction

Message bus framework based on ViewModel and LiveData.

## What are the advantages?

**OneStepMessage** based on ViewModel and LiveData，So it has the advantages of LiveData：

- **Ensures your UI matches your data state**
- **No memory leaks**
- **No crashes due to stopped activities**
- **No more manual lifecycle handling**
- **Always up to date data**

Compared with EventBus and LiveDataBus, **OneStepMessage** has the following advantages:

- **You can write module messages to control the scope of the message**

  Creating a ViewModel in a separate module can make the scope of the message in one module, and creating a ViewModel in a public module can make the message a global message, making the message controllable.

- **Business review logic, control message sending entrance, message has only trusted sending source**

  When each message is created, the business itself must pass in the review code to control the sending of the message. All sending of the message must be reviewed to determine the only trusted source.

  ```java
  class DemoViewModel : ViewModel() {
  
      //Add review code to control the only trusted source, all messages can be sent or not controlled by the business
      val message1 = EventLiveData(object : EventLiveData.PostEventReview<String> {
          override fun review(value: String): Boolean {
  
              //TODO Message to send the review code, return true to review and can be sent, return false to review and cannot be sent
  
              return true
          }
      })
      val message2 = EventLiveData(object : EventLiveData.PostEventReview<Bean> {
          override fun review(value: Bean): Boolean {
  
              //TODO Message to send the review code, return true to review and can be sent, return false to review and cannot be sent
  
              return false
          }
      })
  }
  ```

  In EventLiveData.class:

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

- **Based on the Jetpack library, no need to introduce other third-party libraries**

- **No need to register or cancel, reducing code risk**

- **Log positioning, each message can be directly located to the sending code position, which is convenient for debugging**

![log.png](https://i.loli.net/2021/03/02/jKglNVvfeCOhLSo.png)

## How to import the library?

- **Add it in your root build.gradle at the end of repositories:**

  ```css
  	allprojects {
  		repositories {
  			...
  			maven { url 'https://jitpack.io' }
  		}
  	}
  ```

- **Add the dependency**

  ```css
  	dependencies {
  	        implementation 'com.github.8kEatRadish:OneStepMessage:1.0.0'
  	}
  ```

## How to use the library?

As the name suggests, this framework is called a one-step message, so the usage is also one-step:

- **Create a ViewModel to control whether it is a module message or a global message.**

  ```java
  class DemoViewModel : ViewModel() {
  
      //Add review code to control the only trusted source, all messages can be sent or not controlled by the business
      val message1 = EventLiveData(object : EventLiveData.PostEventReview<String> {
          override fun review(value: String): Boolean {
  
              //TODO Message to send the review code, return true to review and can be sent, return false to review and cannot be sent
              return true
          }
      })
      val message2 = EventLiveData(object : EventLiveData.PostEventReview<Bean> {
          override fun review(value: Bean): Boolean {
  
              //TODO Message to send the review code, return true to review and can be sent, return false to review and cannot be sent
  
              return false
          }
      })
  }
  ```

- **One line of code sends a message, one line of code subscribes to a message.**

  ```kotlin
          //Send a message1
          OSM.with(DemoViewModel::class.java).message1.postEventValue("更改message1了 random = ${(0..100).random()}")
         
          //Listen for messages1
          OSM.with(DemoViewModel::class.java).message1.observeEvent(this, ViewModelStore()){
              Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
          }
         
  ```

- **For Java, CallBack optimization is added to improve the writing experience.**

  ```java
          //Send a message1
          OSM.Companion.with(DemoViewModel.class).getMessage1().postEventValue("更改message1了 random = " + r.nextInt());
         
          //Listen for messages1
          OSM.Companion.with(DemoViewModel.class).getMessage1().observeEvent(this, new ViewModelStore(), new EventLiveData.OnChanged<String>() {
              @Override
              public void onChanged(String value) {
                  showMessage1.setText(value);
              }
          });
  ```

## Note

- **When subscribing to a message, do not pass in ViewModelStore**

  The message received first will consume the message, causing other observers to fail to receive the message.

- **When subscribing to a message, pass in the same ViewModelStore**

  The first received message will be consumed, causing observers who share the same ViewModelStore to fail to receive the message.

- **When subscribing to a message, pass in a different ViewModelStore**

  Receive messages individually and do not affect each other.

This situation is caused because LiveData has a problem, that is, it will send multiple duplicate messages to the observer. I package the Observer here to prevent such problems:

```java
    /**
     * Event can only be consumed by one observer
     */
    @MainThread
    fun observeEvent(owner: LifecycleOwner, onChanged: (T) -> Unit): Observer<Event<T>> {
        //Intercept and issue events, and need to issue when judging
        val wrapperObserver = Observer<Event<T>>() {
            it.getContentIfNotHandled()?.let { data ->
                onChanged.invoke(data)
            }
        }
        //Registration issue
        super.observe(owner, wrapperObserver)
        return wrapperObserver
    }

    /**
     * Events can be consumed by multiple observers, each observer can only consume once
     */
    @MainThread
    fun observeEvent(
            owner: LifecycleOwner,
            viewModelStore: ViewModelStore,
            onChanged: (T) -> Unit
    ): Observer<Event<T>> {
        //Intercept the issued event and determine whether the observer needs to issue
        val wrapperObserver = Observer<Event<T>>() {
            it.getContentIfNotHandled(viewModelStore)?.let() { data ->
                onChanged.invoke(data)
            }
        }
        //Registration issue
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
     * Different observers record the distribution status separately
     * If the observer has not sent the data, send the data
     * Otherwise return null
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
