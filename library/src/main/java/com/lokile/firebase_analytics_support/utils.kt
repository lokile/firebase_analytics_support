package com.lokile.firebase_analytics_support

import android.app.Application
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.lokile.applibraries.managers.*


fun handleException(throwable: Throwable) {
    FirebaseCrashlytics.getInstance().recordException(throwable)
    throwable.printStackTrace()
}

fun initFirebase(app: Application, configList: List<RemoteConfigValue>, allowLogEvent: Boolean,
                 loadingCallback: ((loadFromPreviousVersion: Boolean, configUpdated: Boolean, fetchSuccess: Boolean) -> Unit)? = null) {
    EventTrackingManager.init(app, Firebase.analytics, allowLogEvent)
    setCurrentAppVersion(
        app.packageManager.getPackageInfo(app.packageName, 0).versionName
    )
    RemoteConfigManager.reloadConfig(
        Firebase.remoteConfig,
        configList.plus(
            DefaultRemoteConfigValues.USER_SEGMENT_NAME
        )
    ) { fromPrevious, isUpdated, isSuccess ->
        if (isSuccess) {
            setUserSegmentName(DefaultRemoteConfigValues.USER_SEGMENT_NAME.value as String)
        }
        loadingCallback?.invoke(fromPrevious, isUpdated, isSuccess)
    }
}