package com.lokile.firebase_analytics_support

import com.google.firebase.crashlytics.FirebaseCrashlytics


fun handleException(throwable: Throwable) {
    FirebaseCrashlytics.getInstance().recordException(throwable)
    throwable.printStackTrace()
}
