package test.mlkit.ui

import test.mlkit.util.BaseViewModel
import test.mlkit.util.SingleLiveEvent
import test.mlkit.domain.interactor.GetImageRatioUseCase
import test.mlkit.schedulers.IScheduleProvider

class ViewModel(
    private val getImageRatioUseCase: GetImageRatioUseCase,
    private val scheduleProvider: IScheduleProvider
) : BaseViewModel() {

    val errorLiveData = SingleLiveEvent<String>()

    val barcodeLiveData = SingleLiveEvent<Float>()

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

}