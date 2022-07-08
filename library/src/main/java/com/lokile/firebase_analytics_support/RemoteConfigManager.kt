package com.lokile.applibraries.managers

import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.lokile.firebase_analytics_support.handleException

class RemoteConfigValue(val key: String, var value: Any)

object DefaultRemoteConfigValues {
    val USER_SEGMENT_NAME =
        RemoteConfigValue("user_segment_name", "")
}

open class RemoteConfigManager {
    companion object {
        fun reloadConfig(
            firebaseRemoteConfig: FirebaseRemoteConfig,
            configList: List<RemoteConfigValue>,
            result: ((loadFromPreviousVersion: Boolean, configUpdated: Boolean, fetchSuccess: Boolean) -> Unit)? = null,
        ) {
            //init
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 3600L
            }
            firebaseRemoteConfig.setConfigSettingsAsync(configSettings)

            //reload config
            fetchConfigs(firebaseRemoteConfig, configList, result, true)
        }

        private fun fetchConfigs(
            firebaseRemoteConfig: FirebaseRemoteConfig,
            configList: List<RemoteConfigValue>,
            result: ((loadFromPreviousVersion: Boolean, configUpdated: Boolean, fetchSuccess: Boolean) -> Unit)? = null,
            isShouldRetry: Boolean
        ) {
            if (firebaseRemoteConfig.info.lastFetchStatus == FirebaseRemoteConfig.LAST_FETCH_STATUS_SUCCESS) {
                loadConfig(configList, firebaseRemoteConfig, false) //init default value
                result?.invoke(true, false, false)
            }

            firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener {
                when {
                    it.isSuccessful -> {
                        Log.d("AppLibraries", "Config params updated: ${it.result}")
                        (it.isSuccessful && it.result).apply {
                            loadConfig(configList, firebaseRemoteConfig, this)
                            result?.invoke(false, it.result, it.isSuccessful)
                        }
                    }
                    isShouldRetry -> {
                        Log.d("AppLibraries", "Fetch failed, retry!")
                        fetchConfigs(firebaseRemoteConfig, configList, result, false)
                    }
                    else -> {
                        Log.d("AppLibraries", "Fetch failed, Stop!")
                        result?.invoke(
                            firebaseRemoteConfig.info.lastFetchStatus == FirebaseRemoteConfig.LAST_FETCH_STATUS_SUCCESS,
                            false,
                            false
                        )
                    }
                }
            }
        }

        private fun loadConfig(
            configList: List<RemoteConfigValue>,
            firebaseRemoteConfig: FirebaseRemoteConfig,
            forceLoadingFromRemoteConfig: Boolean
        ) {
            configList.forEach {
                try {
                    it.value = when (it.value) {
                        is Int -> firebaseRemoteConfig.getString(it.key).toInt()
                            .let { value -> if (value == 0 && !forceLoadingFromRemoteConfig) it.value as Int else value }

                        is Long -> firebaseRemoteConfig.getLong(it.key)
                            .let { value -> if (value == 0L && !forceLoadingFromRemoteConfig) it.value as Long else value }

                        is Double -> firebaseRemoteConfig.getDouble(it.key)
                            .let { value -> if (value == 0.0 && !forceLoadingFromRemoteConfig) it.value as Double else value }

                        is Float -> firebaseRemoteConfig.getString(it.key).toFloat()
                            .let { value -> if (value == 0f && !forceLoadingFromRemoteConfig) it.value as Float else value }

                        is Boolean -> firebaseRemoteConfig.getBoolean(it.key)

                        is String -> firebaseRemoteConfig.getString(it.key)
                            .let { value -> if (value.isEmpty() && !forceLoadingFromRemoteConfig) it.value as String else value }

                        else -> firebaseRemoteConfig.getString(it.key)
                    }
                } catch (e: Exception) {
                    handleException(e)
                }
            }
        }
    }
}