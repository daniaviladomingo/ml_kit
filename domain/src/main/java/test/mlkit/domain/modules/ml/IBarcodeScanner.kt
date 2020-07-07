package test.mlkit.domain.modules.ml

import io.reactivex.Single
import test.mlkit.domain.model.BarcodeData
import test.mlkit.domain.model.Image

interface IBarcodeScanner {
    fun scan(image: Image): Single<List<BarcodeData>>
}