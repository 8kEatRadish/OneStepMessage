package com.shawn.oneStepMessage

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelStore


/**
 * 文件: EventLiveData.kt
 * 描述: 消息事件
 * 作者: SuiHongWei 2021/2/22
 */
open class EventLiveData<T> constructor(private val postEventReview: PostEventReview<T>) : LiveData<Event<T>>() {

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

    //kotlin中(T) -> Unit 写法在java中使用不方便，特意为java 提供callback版本
    @MainThread
    fun observeEvent(owner: LifecycleOwner, callback: OnChanged<T>): Observer<Event<T>> {
        //拦截下发事件，判断时候需要下发
        val wrapperObserver = Observer<Event<T>>() {
            it.getContentIfNotHandled()?.let { data ->
                callback.onChanged(data)
            }
        }
        //注册事件
        super.observe(owner, wrapperObserver)
        return wrapperObserver
    }

    @MainThread
    fun observeEvent(
            owner: LifecycleOwner,
            viewModelStore: ViewModelStore,
            callback: OnChanged<T>
    ): Observer<Event<T>> {
        //拦截下发事件，判断该观察者是否需要下发
        val wrapperObserver = Observer<Event<T>>() {
            it.getContentIfNotHandled(viewModelStore)?.let() { data ->
                callback.onChanged(data)
            }
        }
        //注册事件
        super.observe(owner, wrapperObserver)
        return wrapperObserver
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in Event<T>>) {
        throw Exception("请使用observeEvent对事件进行监听！")
    }

    override fun observeForever(observer: Observer<in Event<T>>) {
        throw Exception("请使用observeEvent对事件进行监听！")
    }


    fun postEventValue(value: T) {

        if (!postEventReview.review(value)) {
            "消息审查失败，拒绝发送消息，请检查审查代码以及消息内容".showLogWithPosition(javaClass.simpleName)
            return
        }

        "post message : ${value.toString()} ; ".showLogWithPosition(javaClass.simpleName)

        super.postValue(Event(value))
    }

    //liveData源码中有线程检测，必须在主线程中用
    fun setEventValue(value: T) {

        if (!postEventReview.review(value)) {
            "消息审查失败，拒绝发送消息，请检查审查代码以及消息内容".showLogWithPosition(javaClass.simpleName)
            return
        }

        "set message : ${value.toString()} ; ".showLogWithPosition(javaClass.simpleName)

        super.setValue(Event(value))
    }

    interface OnChanged<T> {
        fun onChanged(value: T)
    }

    //发送审查代码，控制消息发送源
    interface PostEventReview<T> {
        fun review(value: T): Boolean
    }
}