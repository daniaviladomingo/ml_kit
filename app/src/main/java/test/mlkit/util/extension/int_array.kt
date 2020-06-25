package test.mlkit.util.extension

import android.content.pm.PackageManager

fun IntArray.isPermissionsGranted(): Boolean = none { it != PackageManager.PERMISSION_GRANTED }
