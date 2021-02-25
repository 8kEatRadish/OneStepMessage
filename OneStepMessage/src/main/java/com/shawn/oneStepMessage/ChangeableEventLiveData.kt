package com.shawn.oneStepMessage


/**
 * 文件: ChangeableEventLiveData.kt
 * 描述: 可写事件
 * 作者: SuiHongWei 2021/2/25
 */
class ChangeableEventLiveData<T> : EventLiveData<T>() {

    fun postEventValue(value: T) {

        "post message : ${value.toString()} ; ".showLog(javaClass.simpleName)

        super.postValue(Event(value))
    }

    //liveData源码中有线程检测，必须在主线程中用
    fun setEventValue(value: T) {

        "set message : ${value.toString()} ; ".showLog(javaClass.simpleName)

        super.setValue(Event(value))
    }
}