package test.mlkitl.ml

import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.common.InputImage
import io.reactivex.Single
import test.mlkit.domain.model.BarcodeData
import test.mlkit.domain.model.Image
import test.mlkit.domain.model.Roi
import test.mlkit.domain.modules.ml.IBarcodeScanner

class BarcodeScannerImp(
    private val barcodeScanner: BarcodeScanner
) : IBarcodeScanner {
    override fun scan(image: Image): Single<List<BarcodeData>> = Single.create {
        val img = InputImage.fromByteArray(
            image.data,
            image.width,
            image.height,
            image.rotation,
            InputImage.IMAGE_FORMAT_NV21
        )

        barcodeScanner.process(img).addOnSuccessListener { barcode ->
            it.onSuccess(barcode.map {
                BarcodeData(it.rawValue ?: "", it.boundingBox?.run {
                    Roi(
                        top.toFloat(),
                        left.toFloat(),
                        right.toFloat(),
                        bottom.toFloat()
                    )
                } ?: Roi(0f, 0f, 0f, 0f))
            })
        }
    }
}