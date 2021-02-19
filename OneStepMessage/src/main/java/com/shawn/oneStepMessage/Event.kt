package com.shawn.oneStepMessage

import androidx.lifecycle.ViewModelStore

/**
 * 封装事件类，可以更好的管理分发状态
 */
class Event<out T>(private val content: T) {
    //分发状态
    private var hasBeenHandled = false
    //记录不同观察者是否分发状态
    private var map = HashMap<ViewModelStore, Boolean>()

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

    fun peekContent(): T = content
}