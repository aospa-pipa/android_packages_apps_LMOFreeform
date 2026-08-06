package com.libremobileos.sidebar.utils

import android.app.Application
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.os.UserHandle

fun Application.getBadgedIcon(appInfo: ApplicationInfo, userHandle: UserHandle): Drawable =
    packageManager.getUserBadgedIcon(
        appInfo.loadIcon(packageManager),
        userHandle
    )

fun Application.getBadgedIcon(appInfo: ApplicationInfo, userId: Int): Drawable =
    getBadgedIcon(appInfo, UserHandle.of(userId))
