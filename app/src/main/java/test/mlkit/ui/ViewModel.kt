package test.mlkit.ui

import test.mlkit.domain.interactor.FaceDetectionUseCase
import test.mlkit.domain.interactor.GetImageRatioUseCase
import test.mlkit.domain.interactor.TextRecognitionUseCase
import test.mlkit.schedulers.IScheduleProvider
import test.mlkit.ui.model.HighLight
import test.mlkit.ui.model.mapper.HighLightMapper
import test.mlkit.util.BaseViewModel
import test.mlkit.util.SingleLiveEvent

class ViewModel(
    private val getImageRatioUseCase: GetImageRatioUseCase,
    private val textRecognitionUseCase: TextRecognitionUseCase,
    private val faceDetectionUseCase: FaceDetectionUseCase,
    private val highLightMapper: HighLightMapper,
    private val scheduleProvider: IScheduleProvider
) : BaseViewModel() {

    val errorLiveData = SingleLiveEvent<String>()

    val ratioLiveData = SingleLiveEvent<Float>()
    val textRecognitionLiveData = SingleLiveEvent<String>()
    val faceDetectionLiveData = SingleLiveEvent<List<List<HighLight>>>()

    fun adjustPreview() {
        addDisposable(getImageRatioUseCase.execute()
            .observeOn(scheduleProvider.ui())
            .subscribeOn(scheduleProvider.computation())
            .subscribe({ ratio ->
                ratioLiveData.value = ratio
            }) {
                errorLiveData.value = it.toString()
            })

    }

    fun readText() {
        addDisposable(textRecognitionUseCase.execute()
            .observeOn(scheduleProvider.ui())
            .subscribeOn(scheduleProvider.computation())
            .subscribe({ text ->
                textRecognitionLiveData.value = text
            }) {
                errorLiveData.value = it.toString()
            })
    }

    fun faceDetection() {
        addDisposable(faceDetectionUseCase.execute()
            .observeOn(scheduleProvider.ui())
            .subscribeOn(scheduleProvider.computation())
            .subscribe({ faces ->
                faceDetectionLiveData.value = highLightMapper.map(faces)
            }) {
                errorLiveData.value = it.toString()
            })
    }

}