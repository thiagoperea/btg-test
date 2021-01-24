package com.thiagoperea.btgtest.internal

import android.app.Application
import com.thiagoperea.btgtest.BuildConfig
import com.thiagoperea.btgtest.R
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.*

class BtgTestApplication : Application() {

    companion object {
        const val APP_TAG = "TAG_BTG_TEST_APP"
    }

    override fun onCreate() {
        super.onCreate()


        startKoin {
            androidContext(this@BtgTestApplication)
            modules(
                viewModelModule,
                repositoryModule,
                dataSourceModule,
                databaseModule,
            )
        }
    }
}