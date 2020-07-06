package test.mlkitl.ml

import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.common.InputImage
import io.reactivex.Single
import test.mlkit.domain.model.Image
import test.mlkit.domain.modules.ml.IBarcodeScanner

class BarcodeScannerImp(
    private val barcodeScanner: BarcodeScanner
): IBarcodeScanner {
    override fun scan(image: Image): Single<List<String>> = Single.create {
        val img = InputImage.fromByteArray(image.data, image.width, image.height, image.rotation, InputImage.IMAGE_FORMAT_NV21)

        barcodeScanner.process(img).addOnSuccessListener { barcode ->
            it.onSuccess(barcode.map { it.rawValue ?: "" })
        }
    }
}