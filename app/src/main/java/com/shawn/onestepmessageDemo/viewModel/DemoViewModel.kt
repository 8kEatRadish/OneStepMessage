package com.shawn.onestepmessageDemo.viewModel

import androidx.lifecycle.ViewModel
import com.shawn.oneStepMessage.ChangeableEventLiveData
import com.shawn.oneStepMessage.EventLiveData
import com.shawn.onestepmessageDemo.viewModel.bean.Bean

/**
 * 文件: ChangeableEventLiveData.kt
 * 描述: 每个模块单独写自己的通讯viewModel，要是需要全局通讯则需要把ViewModel写到common模块
 * 作者: SuiHongWei 2021/2/25
 */
class DemoViewModel : ViewModel() {

    // 持有可读写事件，必须要私有，控制唯一可信源
    private val message1 = ChangeableEventLiveData<String>()
    private val message2 = ChangeableEventLiveData<Bean>()


    //消息全部由此处发出，是唯一可信源
    fun changeMessage1(value: String) {

        //TODO 可以添加消息审查代码

        message1.postEventValue(value)
    }

    fun changeMessage2(value: Bean) {

        //TODO 可以添加消息审查代码

        message2.postEventValue(value)
    }


    //向外提供只有读权限的消息
    fun getMessage1(): EventLiveData<String> {
        return message1
    }

    fun getMessage2(): EventLiveData<Bean> {
        return message2
    }

}