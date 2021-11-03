package com.qfleng.um

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

import java.util.ArrayList


object PermissionsManager {

    val ALL_NEED_PERMISSIONS = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    fun checkPermission(context: Context, permission: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(permission)
        } else
            true
    }

    fun requestAllNeedPermissions(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val needReReqPermissions = ArrayList<String>(ALL_NEED_PERMISSIONS.size)
            for (p in ALL_NEED_PERMISSIONS) {
                if (!checkPermission(activity, p))
                    needReReqPermissions.add(p)
            }

            if (needReReqPermissions.size > 0)
                activity.requestPermissions(needReReqPermissions.toTypedArray(), 0)
        }
    }

    fun requestNeedPermissions(activity: Activity, permission: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            activity.requestPermissions(arrayOf(permission), 0)
        }
    }
}
