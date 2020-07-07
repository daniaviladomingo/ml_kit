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
import test.mlkit.domain.model.Image
import test.mlkit.domain.modules.debug.PreviewImageListener
import test.mlkit.ui.model.HighLight
import test.mlkit.ui.model.mapper.BitmapMapper
import test.mlkit.util.extension.isPermissionGranted
import test.mlkit.util.extension.isPermissionsGranted
import test.mlkit.util.extension.requestPermission

class MainActivity : AppCompatActivity(), PreviewImageListener {

    private val surfaceView: SurfaceView by inject()

    private val bitmapMapper: BitmapMapper by inject()

    private val lifecycleObserver: Unit by inject { parametersOf(this.lifecycle, this) }

    private val vm: ViewModel by viewModel()

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
//        vm.faceDetection()
        vm.scanBarcode()

        switch_facing.setOnClickListener {
            vm.switchFacingCamera()
        }

        switch_orientation.setOnClickListener {
            vm.switchOrientation()
        }
    }

    private fun setListener() {
        vm.errorLiveData.observe(this, Observer { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        })

        vm.ratioLiveData.observe(this, Observer { ratio ->
            surface_view_container.setRatio(ratio)
        })

        vm.boundingBoxLiveData.observe(this, Observer { boxes ->
            drawHighLights(boxes)
        })

        vm.textRecognitionLiveData.observe(this, Observer { text ->
            Log.d("aaa", "Texto -> $text")
        })

        vm.faceDetectionLiveData.observe(this, Observer { highLightsFace ->
            if (highLightsFace.isEmpty()) {
                view_highLights.clearHighLight()
            } else {
                highLightsFace.forEach { faceHighLight ->
                    drawHighLights(faceHighLight)
                }
            }
        })


    }

    private fun drawHighLights(highLights: List<HighLight>) {
        if (highLights.isEmpty()) {
            view_highLights.clearHighLight()
        } else {
            view_highLights.drawHighLight(highLights)
        }
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

    override fun onPreviewImage(image: Image) {
        val bitmap = bitmapMapper.map(image)
//        runOnUiThread {
//            preview_image.setImageBitmap(bitmap)
//        }
    }
}
