package com.shawn.onestepmessageDemo.viewModel

import androidx.lifecycle.ViewModel
import com.shawn.oneStepMessage.EventLiveData
import com.shawn.onestepmessageDemo.viewModel.bean.Bean

/**
 * 文件: ChangeableEventLiveData.kt
 * 描述: 每个模块单独写自己的通讯viewModel，要是需要全局通讯则需要把ViewModel写到common模块
 * 作者: SuiHongWei 2021/2/25
 */
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