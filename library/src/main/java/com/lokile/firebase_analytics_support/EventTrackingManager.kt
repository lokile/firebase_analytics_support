package com.lokile.applibraries.managers

import android.app.Application
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

open class AnalyticsEvent(val eventType: String, var eventParams: Map<String, String> = emptyMap())
class EventTrackingManager constructor(
    private val app: Application,
    private val firebaseAnalytics: FirebaseAnalytics,
    private val allowLogEvent: Boolean
) {
    fun logEvent(event: AnalyticsEvent) {
        if (!allowLogEvent) {
            return
        }
        firebaseAnalytics.logEvent(event.eventType, Bundle().apply {
            event.eventParams.forEach {
                putString(it.key, it.value)
            }
        })
    }

    fun setCurrentAppVersion(version: String) {
        if (!allowLogEvent) {
            return
        }
        firebaseAnalytics.setUserProperty(
            "current_app_version",
            version
        )
    }

    fun setUserSegmentName(segment: String) {
        if (!allowLogEvent) {
            return
        }
        firebaseAnalytics.setUserProperty(
            "user_segment_name",
            segment
        )
    }

    companion object {
        var instance: EventTrackingManager? = null
        fun init(
            app: Application,
            firebaseAnalytics: FirebaseAnalytics,
            allowLogEvent: Boolean
        ) = EventTrackingManager(app, firebaseAnalytics, allowLogEvent).apply { instance = this }
    }
}

fun logEventTracking(event: AnalyticsEvent) {
    EventTrackingManager.instance?.logEvent(event)
}

fun setCurrentAppVersion(version: String) {
    EventTrackingManager.instance?.setCurrentAppVersion(version)
}

fun setUserSegmentName(userSegmentName: String) {
    EventTrackingManager.instance?.setUserSegmentName(userSegmentName)
}