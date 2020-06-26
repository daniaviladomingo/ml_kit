package test.mlkit.ui

import test.mlkit.util.BaseViewModel
import test.mlkit.util.SingleLiveEvent
import test.mlkit.domain.interactor.GetImageRatioUseCase
import test.mlkit.domain.interactor.TextRecognitionUseCase
import test.mlkit.schedulers.IScheduleProvider

class ViewModel(
    private val getImageRatioUseCase: GetImageRatioUseCase,
    private val textRecognitionUseCase: TextRecognitionUseCase,
    private val scheduleProvider: IScheduleProvider
) : BaseViewModel() {

    val errorLiveData = SingleLiveEvent<String>()

    val barcodeLiveData = SingleLiveEvent<Float>()
    val textRecognitionLiveData = SingleLiveEvent<String>()

    fun adjustPreview(){
        addDisposable(getImageRatioUseCase.execute()
            .observeOn(scheduleProvider.ui())
            .subscribeOn(scheduleProvider.computation())
            .subscribe({ ratio ->
                barcodeLiveData.value = ratio
            }) {
                errorLiveData.value = it.toString()
            })
    }

    fun readText(){
        addDisposable(textRecognitionUseCase.execute()
            .observeOn(scheduleProvider.ui())
            .subscribeOn(scheduleProvider.computation())
            .subscribe({ text ->
                textRecognitionLiveData.value = text
            }) {
                errorLiveData.value = it.toString()
            })
    }

}