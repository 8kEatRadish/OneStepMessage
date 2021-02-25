package com.shawn.oneStepMessage

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelStore


/**
 * 文件: EventLiveData.kt
 * 描述: 只可读事件
 * 作者: SuiHongWei 2021/2/22
 */
open class EventLiveData<T> : LiveData<Event<T>>() {

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

    interface OnChanged<T> {
        fun onChanged(value: T)
    }
}