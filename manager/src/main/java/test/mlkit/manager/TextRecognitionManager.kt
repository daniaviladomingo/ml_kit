package test.mlkit.manager

import io.reactivex.Observable
import test.mlkit.domain.modules.IImageSource
import test.mlkit.domain.modules.IImageVisible
import test.mlkit.domain.modules.ILifecycleObserver
import test.mlkit.domain.modules.manager.ITextRecognitionManager
import test.mlkit.domain.modules.ml.ITextRecognition
import java.util.concurrent.TimeUnit

class TextRecognitionManager(
    private val imageSource: IImageSource,
    private val textRecognition: ITextRecognition,
    private val imageVisible: IImageVisible,
    private val period: Long,
    private val timeUnit: TimeUnit
) : ITextRecognitionManager, ILifecycleObserver {
    private var resume = false

    override fun read(): Observable<String> = Observable
        .interval(period, timeUnit)
        .filter { resume }
        .flatMap {
            imageSource.getImage().toObservable().flatMap { image ->
                imageVisible.visible(image).toObservable().flatMap { visibleImage ->
                    textRecognition.extractText(visibleImage).toObservable()
                }
            }
        }

    override fun create() {}

    override fun start() {
        resume = true
    }

    override fun stop() {
        resume = false
    }

    override fun destroy() {}
}