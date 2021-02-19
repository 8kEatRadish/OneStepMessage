package com.shawn.oneStepMessage

import android.app.Activity
import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

/**
 * 文件: OSM.kt
 * 描述: 全局viewModelStore
 * 作者: SuiHongWei 2021/2/19
 */
class OSM private constructor() : ViewModelStoreOwner {
    //全局viewModelStore
    private var mViewModelStore = ViewModelStore()

    private lateinit var mFactory: ViewModelProvider.Factory

    private lateinit var mApplication: Application

    companion object {
        val INSTANT by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            OSM()
        }
    }

    /**
     * 获取viewModelProvider
     */
    fun getAppViewModelProvider(): ViewModelProvider {
        return ViewModelProvider(INSTANT, INSTANT.getAppFactory())
    }

    fun getAppViewModelProvider(activity: Activity): ViewModelProvider {
        return ViewModelProvider(
            INSTANT,
            INSTANT.getAppFactory(activity)
        )
    }

    /**
     * 获取factory
     */
    private fun getAppFactory(): ViewModelProvider.Factory {
        if (!this::mFactory.isInitialized) {
            mFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(mApplication)
        }
        return mFactory
    }

    private fun getAppFactory(activity: Activity): ViewModelProvider.Factory {
        val application = checkApplication(activity)
        if (!this::mFactory.isInitialized) {
            mFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        }
        return mFactory
    }

    /**
     * 检查是否关联application
     */
    private fun checkApplication(activity: Activity): Application {
        return activity.application
            ?: throw IllegalStateException(
                "Your activity/fragment is not yet attached to "
                        + "Application. You can't request ViewModel before onCreate call."
            )
    }

    override fun getViewModelStore(): ViewModelStore {
        return mViewModelStore
    }
}