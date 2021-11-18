package com.shawn.oneStepMessage

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelStore
import java.util.*
import java.util.concurrent.atomic.AtomicInteger


/**
 * 文件: EventLiveData.kt
 * 描述: 消息事件
 * 作者: SuiHongWei 2021/2/22
 */
open class EventLiveData<T> constructor(
    private val postEventReview: PostEventReview<T> = object : PostEventReview<T> {
        override fun review(value: T): Boolean {
            return true
        }
    }
) : LiveData<Event<T>>() {

    private val START_VERSION = -1

    private val mCurrentVersion: AtomicInteger = AtomicInteger(START_VERSION)

    /**
     * 事件只能被一个观察者消费
     */
    @MainThread
    fun observeEvent(owner: LifecycleOwner, onChanged: (T) -> Unit): Observer<Event<T>> {
        //拦截下发事件，判断时候需要下发
        val wrapperObserver = createObserverWrapper(onChanged, mCurrentVersion.get())
        //注册事件
        super.observe(owner, wrapperObserver)
        return wrapperObserver
    }

    @MainThread
    fun observeEventSticky(owner: LifecycleOwner, onChanged: (T) -> Unit): Observer<Event<T>> {
        //拦截下发事件，判断时候需要下发
        val wrapperObserver = createObserverWrapper(onChanged, START_VERSION)
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
        val wrapperObserver =
            createObserverWrapper(onChanged, mCurrentVersion.get(), viewModelStore)
        //注册事件
        super.observe(owner, wrapperObserver)
        return wrapperObserver
    }

    @MainThread
    fun observeEventSticky(
        owner: LifecycleOwner,
        viewModelStore: ViewModelStore,
        onChanged: (T) -> Unit
    ): Observer<Event<T>> {
        //拦截下发事件，判断该观察者是否需要下发
        val wrapperObserver =
            createObserverWrapper(onChanged, START_VERSION, viewModelStore)
        //注册事件
        super.observe(owner, wrapperObserver)
        return wrapperObserver
    }

    //kotlin中(T) -> Unit 写法在java中使用不方便，特意为java 提供callback版本
    @MainThread
    fun observeEvent(owner: LifecycleOwner, callback: OnChanged<T>): Observer<Event<T>> {
        //拦截下发事件，判断时候需要下发
        val wrapperObserver = createObserverWrapper({
            callback.onChanged(it)
        }, mCurrentVersion.get())
        //注册事件
        super.observe(owner, wrapperObserver)
        return wrapperObserver
    }

    @MainThread
    fun observeEventSticky(owner: LifecycleOwner, callback: OnChanged<T>): Observer<Event<T>> {
        //拦截下发事件，判断时候需要下发
        val wrapperObserver = createObserverWrapper({
            callback.onChanged(it)
        }, START_VERSION)
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
        val wrapperObserver = createObserverWrapper({
            callback.onChanged(it)
        }, mCurrentVersion.get(), viewModelStore)
        //注册事件
        super.observe(owner, wrapperObserver)
        return wrapperObserver
    }

    @MainThread
    fun observeEventSticky(
        owner: LifecycleOwner,
        viewModelStore: ViewModelStore,
        callback: OnChanged<T>
    ): Observer<Event<T>> {
        //拦截下发事件，判断该观察者是否需要下发
        val wrapperObserver = createObserverWrapper({
            callback.onChanged(it)
        }, START_VERSION, viewModelStore)
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

        mCurrentVersion.getAndIncrement()

        if (!postEventReview.review(value)) {
            "消息审查失败，拒绝发送消息，请检查审查代码以及消息内容".showLogWithPosition(javaClass.simpleName)
            return
        }

        "post message : ${value.toString()} ; ".showLogWithPosition(javaClass.simpleName)

        super.postValue(Event(value))
    }

    //liveData源码中有线程检测，必须在主线程中用
    fun setEventValue(value: T) {

        mCurrentVersion.getAndIncrement()

        Log.d("suihw test","${mCurrentVersion.get()}")

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

    private fun createObserverWrapper(
        onChanged: (T) -> Unit,
        version: Int,
        viewModelStore: ViewModelStore? = null
    ): ObserverWrapper<T> {
        return ObserverWrapper(onChanged, version, viewModelStore)
    }

    inner class ObserverWrapper<T>(
        private var onChanged: (T) -> Unit,
        private var mVersion: Int,
        private var viewModelStore: ViewModelStore?
    ) : Observer<Event<T>> {
        override fun onChanged(t: Event<T>?) {
            /// mCurrentVersion.get() > mVersion 来去除消息粘性

            Log.d("suihw test ", " mCurrent  =  ${mCurrentVersion.get()} ; mVersion  =  $mVersion")

            if (mCurrentVersion.get() > mVersion) {
                t?.apply {
                    if (viewModelStore == null) {
                        getContentIfNotHandled()?.let { data ->
                            onChanged.invoke(data)
                        }
                    } else {
                        getContentIfNotHandled(viewModelStore!!)?.let { data ->
                            onChanged.invoke(data)
                        }
                    }
                }
            }
        }
    }
}