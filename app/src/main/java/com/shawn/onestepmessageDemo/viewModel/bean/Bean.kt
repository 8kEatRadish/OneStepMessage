package com.shawn.onestepmessageDemo.viewModel.bean

data class Bean(
    var name: String = "",
    var feature1: String = "",
    var feature2: Int = -1,
    var feature3: Boolean = false
){
    override fun toString(): String {
        return "name:$name;feature1:$feature1;feature2:$feature2;feature3:$feature3"
    }
}