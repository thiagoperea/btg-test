package com.thiagoperea.btgtest.internal

import android.content.Context
import android.util.Log
import com.thiagoperea.btgtest.internal.BtgTestApplication.Companion.APP_TAG
import java.util.*

object PropertiesReader {

    private const val FILENAME = "app.properties"
    private const val PROPERTY_API_KEY = "API_KEY"

    /**
     * Lê a propriedade "API_KEY" do arquivo app.properties
     * localizado em app/src/release/assets
     */
    fun getApiKey(appContext: Context): String {
        try {
            appContext.assets
                .open(FILENAME)
                .use {
                    val properties = Properties()
                    properties.load(it)
                    return properties.getProperty(PROPERTY_API_KEY)
                }
        } catch (error: Exception) {
            Log.e(APP_TAG, "Error: ${error.message}")
            return ""
        }
    }
}