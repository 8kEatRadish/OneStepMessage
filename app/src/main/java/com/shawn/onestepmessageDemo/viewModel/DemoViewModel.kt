package com.shawn.onestepmessageDemo.viewModel

import androidx.lifecycle.ViewModel
import com.shawn.oneStepMessage.EventLiveData
import com.shawn.onestepmessageDemo.viewModel.bean.Bean

class DemoViewModel : ViewModel() {

    val message1 = EventLiveData<String>()
    val message2 = EventLiveData<Bean>()

}