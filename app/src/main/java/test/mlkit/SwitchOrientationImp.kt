package test.mlkit

import android.app.Activity
import android.content.pm.ActivityInfo
import test.mlkit.domain.model.Orientation
import test.mlkit.domain.modules.ILifecycleObserver
import test.mlkit.domain.modules.ISwitchOrientation

class SwitchOrientationImp(
    orientation: Orientation,
    private val activity: Activity
) : ISwitchOrientation, ILifecycleObserver {

    private var currentOrientation = orientation

    override fun switchOrientation() {
//        currentOrientation =
//            if (currentOrientation == Orientation.PORTRAIT) Orientation.LANDSCAPE else Orientation.PORTRAIT
//
//        changeOrientation()
    }

    override fun create() {
        changeOrientation()
    }

    override fun start() {

    }

    override fun stop() {

    }

    override fun destroy() {

    }

    private fun changeOrientation() {
//        activity.requestedOrientation = when (currentOrientation) {
//            Orientation.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//            Orientation.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//        }
    }
}