package test.mlkit.ui

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_choose_service.*
import test.mlkit.R
import test.mlkit.ui.model.MLService

class MLServiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_service)

        ml_text_recognition.setOnClickListener {
            startActivity(MLService.TEXT_RECOGNITION)
        }

        ml_face_detection.setOnClickListener {
            startActivity(MLService.FACE_DETECTOR)
        }

        ml_barcode_scanner.setOnClickListener {
            startActivity(MLService.BARCODE_SCANNER)
        }

    }

    private fun startActivity(mlService: MLService) {
        startActivity(Intent(this, MLMainActivity::class.java).apply {
            putExtra(MLMainActivity.ML_SERVICE, mlService as Parcelable)
        })
    }
}
