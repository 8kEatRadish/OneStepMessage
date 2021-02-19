package com.shawn.oneStepMessage

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelStore

class EventLiveData<T> : LiveData<Event<T>>() {

    /**
     * 事件只能被一个观察者消费
     */
    fun observeEvent(owner: LifecycleOwner, onChanged: (T) -> Unit): Observer<Event<T>> {
        //拦截下发事件，判断时候需要下发
        val wrapperObserver = Observer<Event<T>>() {
            it.getContentIfNotHandled()?.let { data ->
                onChanged.invoke(data)
            }
        }
        //注册事件
        observe(owner, wrapperObserver)
        return wrapperObserver
    }

    /**
     * 事件可以被多个观察者消费，每个观察者只能消费一次
     */
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
        observe(owner, wrapperObserver)
        return wrapperObserver
    }

    protected fun postEventValue(value: T) {
        super.postValue(Event(value))
    }

    protected fun setEventValue(value: T) {
        super.setValue(Event(value))
    }

    private fun lineNumber(): Int {
        val trace: Array<StackTraceElement> = Thread.currentThread().stackTrace
        return if (trace == null || trace.isEmpty()) {
            -1
        } else {
            trace[0].lineNumber
        }
    }
}