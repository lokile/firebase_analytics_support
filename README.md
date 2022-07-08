# firebase_analytics_support
Support to work with firebase_analytics and firebase_crashlytics and RemoteConfig on Android

## Installation
Add it in your root build.gradle at the end of repositories:
```
  allprojects {
    repositories {
      ...
      maven { url 'https://jitpack.io' }
    }
  }
```
Step 2. Add the dependency, latest_version: [![](https://jitpack.io/v/lokile/firebase_analytics_support.svg)](https://jitpack.io/#lokile/firebase_analytics_support)
```
  dependencies {
    implementation 'com.github.lokile:firebase_analytics_support:latest_version'
  }
```

## Usage:
- To init the EventTracking and load the RemoteConfig, call the following in your Application class
```
initFirebase(...)
```
- Log exception to Crashlytics:
```
handleException(throwable)
```
- Log event tracking:
```
logEventTracking(...)
```
- Set current app version property:
```
setCurrentAppVersion("your app version")
```
- Set User segment name:
```
setUserSegmentName("your user segment name")
```
