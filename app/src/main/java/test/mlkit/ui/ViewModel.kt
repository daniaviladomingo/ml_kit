package test.mlkit.ui

import test.mlkit.domain.interactor.FaceDetectionUseCase
import test.mlkit.domain.interactor.GetImageRatioUseCase
import test.mlkit.domain.interactor.TextRecognitionUseCase
import test.mlkit.domain.model.face.FaceData
import test.mlkit.schedulers.IScheduleProvider
import test.mlkit.util.BaseViewModel
import test.mlkit.util.SingleLiveEvent

class ViewModel(
    private val getImageRatioUseCase: GetImageRatioUseCase,
    private val textRecognitionUseCase: TextRecognitionUseCase,
    private val faceDetectionUseCase: FaceDetectionUseCase,
    private val scheduleProvider: IScheduleProvider
) : BaseViewModel() {

    val errorLiveData = SingleLiveEvent<String>()

    val ratioLiveData = SingleLiveEvent<Float>()
    val textRecognitionLiveData = SingleLiveEvent<String>()
    val faceDetectionLiveData = SingleLiveEvent<List<FaceData>>()

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

    fun faceDetection(){
        addDisposable(faceDetectionUseCase.execute()
            .observeOn(scheduleProvider.ui())
            .subscribeOn(scheduleProvider.computation())
            .subscribe({ faces ->
                faceDetectionLiveData.value = faces
            }) {
                errorLiveData.value = it.toString()
            })
    }

}