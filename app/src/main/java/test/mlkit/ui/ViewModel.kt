package test.mlkit.ui

import test.mlkit.domain.interactor.*
import test.mlkit.schedulers.IScheduleProvider
import test.mlkit.ui.model.HighLight
import test.mlkit.ui.model.mapper.HighLightMapper
import test.mlkit.ui.model.mapper.RoiMapper
import test.mlkit.util.BaseViewModel
import test.mlkit.util.SingleLiveEvent

class ViewModel(
    private val getImageRatioUseCase: GetImageRatioUseCase,
    private val switchFacingCameraUseCase: SwitchFacingCameraUseCase,
    private val switchOrientationUseCase: SwitchOrientationUseCase,
    private val textRecognitionUseCase: TextRecognitionUseCase,
    private val faceDetectionUseCase: FaceDetectionUseCase,
    private val barcodeScannerUseCase: BarcodeScannerUseCase,
    private val highLightMapper: HighLightMapper,
    private val roiMapper: RoiMapper,
    private val scheduleProvider: IScheduleProvider
) : BaseViewModel() {

    val errorLiveData = SingleLiveEvent<String>()

    val ratioLiveData = SingleLiveEvent<Float>()

    val boundingBoxLiveData = SingleLiveEvent<List<HighLight>>()

    val textRecognitionLiveData = SingleLiveEvent<String>()
    val faceDetectionLiveData = SingleLiveEvent<List<HighLight>>()
    val barcodeScannedLiveData = SingleLiveEvent<List<String>>()

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

    fun switchFacingCamera() {
        addDisposable(switchFacingCameraUseCase.execute()
            .observeOn(scheduleProvider.ui())
            .subscribeOn(scheduleProvider.computation())
            .subscribe({}) {
                errorLiveData.value = it.toString()
            })
    }

    fun switchOrientation() {
        addDisposable(switchOrientationUseCase.execute()
            .observeOn(scheduleProvider.ui())
            .subscribeOn(scheduleProvider.computation())
            .subscribe({}) {
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
                val fh = mutableListOf<HighLight>()
                faces.forEach {
                    highLightMapper.map(it).forEach { hl ->
                        fh.add(hl)
                    }
                    fh.add(roiMapper.map(it.box))
                }
                faceDetectionLiveData.value = fh
//                    .apply { add(roiMapper.map(faces.map { it.box })) }
            }) {
                errorLiveData.value = it.toString()
            })
    }

    fun scanBarcode() {
        addDisposable(barcodeScannerUseCase.execute()
            .observeOn(scheduleProvider.ui())
            .subscribeOn(scheduleProvider.computation())
            .subscribe({ barcode ->
                boundingBoxLiveData.value = roiMapper.map(barcode.map { it.box })
                barcodeScannedLiveData.value = barcode.map { it.rawValue }
            }) {
                errorLiveData.value = it.toString()
            })
    }

}