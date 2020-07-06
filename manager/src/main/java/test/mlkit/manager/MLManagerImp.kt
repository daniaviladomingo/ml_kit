package test.mlkit.manager

import io.reactivex.Observable
import test.mlkit.domain.model.face.FaceData
import test.mlkit.domain.modules.IImageSource
import test.mlkit.domain.modules.ILifecycleObserver
import test.mlkit.domain.modules.manager.IMLManager
import test.mlkit.domain.modules.ml.IBarcodeScanner
import test.mlkit.domain.modules.ml.IFaceDetection
import test.mlkit.domain.modules.ml.ITextRecognition
import java.util.concurrent.TimeUnit

class MLManagerImp(
    private val imageSource: IImageSource,
    private val textRecognition: ITextRecognition,
    private val faceDetection: IFaceDetection,
    private val barcodeScanner: IBarcodeScanner,
//    private val imageVisible: IImageVisible,
    private val period: Long,
    private val timeUnit: TimeUnit
) : IMLManager, ILifecycleObserver {
    private var resume = false

    override fun recognizedText(): Observable<String> = period()
        .flatMap {
            imageSource.getImage().toObservable().flatMap { image ->
                textRecognition.extractText(image).toObservable()
            }
        }

    override fun faceDetection(): Observable<List<FaceData>> = period()
        .flatMap {
            imageSource.getImage().toObservable().flatMap { image ->
                faceDetection.detection(image).toObservable()
            }
        }

    override fun scanBarcode(): Observable<List<String>> = period()
        .flatMap {
            imageSource.getImage().toObservable().flatMap { image ->
                barcodeScanner.scan(image).toObservable()
            }
        }

    private fun period(): Observable<Long> = Observable
        .interval(period, timeUnit)
        .filter { resume }

    override fun create() {}

    override fun start() {
        resume = true
    }

    override fun stop() {
        resume = false
    }

    override fun destroy() {}
}