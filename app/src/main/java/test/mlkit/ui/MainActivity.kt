package test.mlkit.ui

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import test.mlkit.R
import test.mlkit.util.extension.isPermissionGranted
import test.mlkit.util.extension.isPermissionsGranted
import test.mlkit.util.extension.requestPermission

class MainActivity : AppCompatActivity() {

    private val surfaceView: SurfaceView by inject()

    private val vm: ViewModel by viewModel()

    private val lifecycleObserver: Unit by inject { parametersOf(this.lifecycle) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleObserver.run { }

        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        if (isPermissionGranted(Manifest.permission.CAMERA)) {
            init()
        } else {
            requestPermission(Manifest.permission.CAMERA, 0)
        }
    }

    private fun init() {
        setListener()
        vm.adjustPreview()
//        vm.readText()
        vm.faceDetection()
    }

    private fun setListener() {
        vm.errorLiveData.observe(this, Observer { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        })

        vm.ratioLiveData.observe(this, Observer { ratio ->
            surface_view_container.setRatio(ratio)
        })

        vm.textRecognitionLiveData.observe(this, Observer { text ->
            Log.d("aaa", "Texto -> $text")
        })

        vm.faceDetectionLiveData.observe(this, Observer { faces ->
            faces.forEach {
                Log.d(
                    "aaa",
                    "Smiling?: ${it.isSmiling}, Right Eyes Open?: ${it.isRightEyeOpen}, Left Eyes Open?: ${it.isLeftEyeOpen}"
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()
        surface_view_container.addView(surfaceView)
    }

    override fun onPause() {
        super.onPause()
        surface_view_container.removeView(surfaceView)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            0 -> {
                if (grantResults.isPermissionsGranted()) {
                    init()
                } else {
                    finish()
                }
            }
        }
    }
}
