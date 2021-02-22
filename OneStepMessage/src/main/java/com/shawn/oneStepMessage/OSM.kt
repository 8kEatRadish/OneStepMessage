package com.shawn.oneStepMessage

import android.app.Activity
import android.app.Application
import androidx.lifecycle.ViewModel
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

    companion object {
        private val INSTANT by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            OSM()
        }

        private var mApplication: Application? = null

        /**
         * 初始化OneStepMessage
         * @param application
         * @param showLog 是否显示log，默认为显示
         */
        fun init(application: Application, showLog: Boolean = true) {
            OSM_SHOW_LOG = showLog
            mApplication = application
        }

        //获取viewModel
        fun <T : ViewModel> with(clazz: Class<T>): T {
            return INSTANT.getAppViewModelProvider()[clazz]
        }

        //获取viewModel
        fun <T : ViewModel> with(clazz: Class<T>, activity: Activity): T {
            return INSTANT.getAppViewModelProvider(activity)[clazz]
        }
    }

    /**
     * 获取viewModelProvider
     */
    private fun getAppViewModelProvider(): ViewModelProvider {
        return ViewModelProvider(INSTANT, INSTANT.getAppFactory())
    }

    private fun getAppViewModelProvider(activity: Activity): ViewModelProvider {
        return ViewModelProvider(
            INSTANT,
            INSTANT.getAppFactory(activity)
        )
    }

    /**
     * 获取factory
     */
    private fun getAppFactory(): ViewModelProvider.Factory {
        if (mApplication == null) {
            throw Exception("先执行init()再使用!")
        }
        if (!this::mFactory.isInitialized) {
            mFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(mApplication!!)
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