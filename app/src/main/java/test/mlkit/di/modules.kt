package test.mlkit.di

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.view.Display
import android.view.SurfaceView
import android.view.ViewGroup
import android.view.WindowManager
import androidx.lifecycle.Lifecycle
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.text.TextRecognition
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.binds
import org.koin.dsl.module
import test.mlkit.LifecycleManager
import test.mlkit.camera.ImageResolutionImp
import test.mlkit.camera.ImageSourceImp
import test.mlkit.di.qualifier.QCamera
import test.mlkit.di.qualifier.QMLManager
import test.mlkit.domain.interactor.FaceDetectionUseCase
import test.mlkit.domain.interactor.GetImageRatioUseCase
import test.mlkit.domain.interactor.TextRecognitionUseCase
import test.mlkit.domain.model.CameraFacing
import test.mlkit.domain.model.Size
import test.mlkit.domain.modules.ICameraResolution
import test.mlkit.domain.modules.IImageSource
import test.mlkit.domain.modules.ILifecycleObserver
import test.mlkit.domain.modules.debug.PreviewImageListener
import test.mlkit.domain.modules.manager.IMLManager
import test.mlkit.domain.modules.ml.IFaceDetection
import test.mlkit.domain.modules.ml.ITextRecognition
import test.mlkit.manager.MLManagerImp
import test.mlkit.schedulers.IScheduleProvider
import test.mlkit.schedulers.ScheduleProviderImp
import test.mlkit.ui.ViewModel
import test.mlkit.ui.model.mapper.BitmapMapper
import test.mlkit.ui.model.mapper.HighLightMapper
import test.mlkit.ui.model.mapper.PointsMapper
import test.mlkitl.ml.FaceDetectionImp
import test.mlkitl.ml.TextRecognitionImp
import java.util.concurrent.TimeUnit

const val isPortrait = true

lateinit var imageSize: Size

val getImageSize: () -> Size = {
    imageSize
}

lateinit var previewImageListener: PreviewImageListener

val getPreviewImageListener: () -> PreviewImageListener = {
    previewImageListener
}

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
            (screenRealSize.x) - (screenSize.x) - heightNavigationBar

        Size(
            screenRealSize.x - lossDimension,
            screenRealSize.y
        )
    }

    single { TimeUnit.MILLISECONDS }

    single { 200L }
}

val activityModule = module {
    factory { (lifecycle: Lifecycle, PrIListener: PreviewImageListener) ->
        LifecycleManager(arrayOf(get(QCamera), get(QMLManager)), lifecycle)
        previewImageListener = PrIListener
        Unit
    }
}

val viewModelModule = module {
    viewModel { ViewModel(get(), get(), get(), get(), get()) }
}

val useCasesModules = module {
    single { GetImageRatioUseCase(get(QCamera)) }
    single { TextRecognitionUseCase(get(QMLManager)) }
    single { FaceDetectionUseCase(get(QMLManager)) }
}

val mlModule = module {
    single<ITextRecognition> { TextRecognitionImp(get()) }
    single<IFaceDetection> { FaceDetectionImp(get(), 0.7f) }

    single { TextRecognition.getClient() }
    single { FaceDetection.getClient(get()); }

    single {
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
    }
}

val managerModule = module {
    single(QMLManager) {
        MLManagerImp(get(QCamera), get(), get(), get(), get())
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
            isPortrait,
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
            isPortrait
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
            get()
        )
    }

    single {
        PointsMapper()
    }

    single {
        BitmapMapper()
    }
}

