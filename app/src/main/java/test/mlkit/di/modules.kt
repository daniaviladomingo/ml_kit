package test.mlkit.di

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.util.Log
import android.view.Display
import android.view.SurfaceView
import android.view.ViewGroup
import android.view.WindowManager
import androidx.lifecycle.Lifecycle
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.text.TextRecognition
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.binds
import org.koin.dsl.module
import test.mlkit.LifecycleManager
import test.mlkit.SwitchOrientationImp
import test.mlkit.camera.ImageResolutionImp
import test.mlkit.camera.ImageSourceImp
import test.mlkit.di.qualifier.QCamera
import test.mlkit.di.qualifier.QMLManager
import test.mlkit.di.qualifier.QOrientation
import test.mlkit.domain.interactor.*
import test.mlkit.domain.model.CameraFacing
import test.mlkit.domain.model.Orientation
import test.mlkit.domain.model.Size
import test.mlkit.domain.modules.ICameraResolution
import test.mlkit.domain.modules.IImageSource
import test.mlkit.domain.modules.ILifecycleObserver
import test.mlkit.domain.modules.debug.PreviewImageListener
import test.mlkit.domain.modules.manager.IMLManager
import test.mlkit.domain.modules.ml.IBarcodeScanner
import test.mlkit.domain.modules.ml.IFaceDetection
import test.mlkit.domain.modules.ml.ITextRecognition
import test.mlkit.manager.MLManagerImp
import test.mlkit.schedulers.IScheduleProvider
import test.mlkit.schedulers.ScheduleProviderImp
import test.mlkit.ui.ViewModel
import test.mlkit.ui.custom.AdaptableContainerSurfaceView
import test.mlkit.ui.custom.HighLightView
import test.mlkit.ui.model.mapper.BitmapMapper
import test.mlkit.ui.model.mapper.HighLightMapper
import test.mlkit.ui.model.mapper.PointsMapper
import test.mlkit.ui.model.mapper.RoiMapper
import test.mlkitl.ml.BarcodeScannerImp
import test.mlkitl.ml.FaceDetectionImp
import test.mlkitl.ml.TextRecognitionImp
import java.util.concurrent.TimeUnit

val orientation = Orientation.LANDSCAPE

lateinit var imageSize: Size

val getImageSize: () -> Size = {
    imageSize
}

lateinit var previewImageListener: PreviewImageListener

val getPreviewImageListener: () -> PreviewImageListener = {
    previewImageListener
}

lateinit var activity: Activity

val appModule = module {
    single { (androidContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay }

    single {
        val screenSize = Point()
        (get() as Display).getSize(screenSize)
        val heightNavigationBar = androidContext().resources.getDimensionPixelSize(
            androidContext().applicationContext.resources.getIdentifier(
                "navigation_bar_height",
                "dimen",
                "android"
            )
        )

        val screenRealSize = Point()
        (get() as Display).getRealSize(screenRealSize)

        val lossDimension =
            (if (orientation == Orientation.PORTRAIT) screenRealSize.y else screenRealSize.x) - (if (orientation == Orientation.PORTRAIT) screenSize.y else screenSize.x) - heightNavigationBar

        if (orientation == Orientation.PORTRAIT)
            Size(
                screenRealSize.x,
                screenRealSize.y - lossDimension
            )
        else
            Size(
                screenRealSize.x - lossDimension,
                screenRealSize.y
            )
    }

    single { TimeUnit.MILLISECONDS }

    single { 250L }
}

val activityModule = module {

    factory { (lifecycle: Lifecycle, act: Activity) ->
        previewImageListener = act as PreviewImageListener
        activity = act
        LifecycleManager(arrayOf(get(QCamera), get(QMLManager), get(QOrientation)), lifecycle)
        Unit
    }

    factory(QOrientation) {
        SwitchOrientationImp(
            orientation,
            activity
        )
    }

    factory {
        HighLightView(androidContext())
    }
}

val viewModelModule = module {
    viewModel { ViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}

val useCasesModules = module {
    factory { GetImageRatioUseCase(get(QCamera)) }
    factory { SwitchFacingCameraUseCase(get(QCamera)) }
    factory { SwitchOrientationUseCase(get(QOrientation)) }
    factory { TextRecognitionUseCase(get(QMLManager)) }
    factory { FaceDetectionUseCase(get(QMLManager)) }
    factory { BarcodeScannerUseCase(get(QMLManager)) }
}

val mlModule = module {
    single<ITextRecognition> { TextRecognitionImp(get()) }
    single<IFaceDetection> { FaceDetectionImp(get(), 0.7f) }
    single<IBarcodeScanner> { BarcodeScannerImp(get()) }

    single { TextRecognition.getClient() }
    single { FaceDetection.getClient(get()); }
    single { BarcodeScanning.getClient() }

    single {
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
    }

    single {
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_PDF417)
    }
}

val managerModule = module {
    single(QMLManager) {
        MLManagerImp(get(QCamera), get(), get(), get(), get(), get())
    } binds arrayOf(IMLManager::class, ILifecycleObserver::class)
}

val imageSourceModule = module {
    single {
        SurfaceView(androidContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    single(QCamera) {
        ImageSourceImp(
            orientation,
            get(),
            get(),
            get(),
            {
                imageSize = it
            },
            getPreviewImageListener,
            CameraFacing.BACK
        )
    } binds arrayOf(
        IImageSource::class,
        ILifecycleObserver::class
    )

    single<ICameraResolution> {
        ImageResolutionImp(
            720,
            900,
            get(),
            orientation
        )
    }
}

val scheduleModule = module {
    factory<IScheduleProvider> { ScheduleProviderImp() }
}

val mapperModule = module {
    single {
        HighLightMapper(
            get(),
            getImageSize,
            Color.BLUE,
            Color.GREEN,
            Color.RED,
            get(),
            orientation
        )
    }

    single {
        PointsMapper()
    }

    single {
        RoiMapper(
            Color.BLUE,
            get(),
            getImageSize,
            orientation
        )
    }

    single {
        BitmapMapper()
    }
}

