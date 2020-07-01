package test.mlkit.di

import android.content.Context
import android.graphics.Bitmap
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
import test.mlkit.camera.ImageSourceImp
import test.mlkit.di.qualifier.QCamera
import test.mlkit.di.qualifier.QMLManager
import test.mlkit.domain.interactor.FaceDetectionUseCase
import test.mlkit.domain.interactor.GetImageRatioUseCase
import test.mlkit.domain.interactor.TextRecognitionUseCase
import test.mlkit.domain.model.Image
import test.mlkit.domain.model.Size
import test.mlkit.domain.model.mapper.Mapper
import test.mlkit.domain.modules.IImageRatio
import test.mlkit.domain.modules.IImageSource
import test.mlkit.domain.modules.IImageVisible
import test.mlkit.domain.modules.ILifecycleObserver
import test.mlkit.domain.modules.manager.IMLManager
import test.mlkit.domain.modules.ml.IFaceDetection
import test.mlkit.domain.modules.ml.ITextRecognition
import test.mlkit.image_transform.ImageVisibleImp
import test.mlkit.manager.MLManagerImp
import test.mlkit.schedulers.IScheduleProvider
import test.mlkit.schedulers.ScheduleProviderImp
import test.mlkit.ui.ViewModel
import test.mlkitl.ml.FaceDetectionImp
import test.mlkitl.ml.TextRecognitionImp
import test.mlkitl.ml.model.mapper.BitmapMapper
import java.util.concurrent.TimeUnit

val appModule = module {
    single { (androidContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay }

    single {
        val point = Point()
        (get() as Display).getSize(point)
        val heightNavigationBar = androidContext().resources.getDimensionPixelSize(
            androidContext().applicationContext.resources.getIdentifier(
                "navigation_bar_height",
                "dimen",
                "android"
            )
        )

        Point().apply { (get() as Display).getRealSize(this) }
            .let { point ->
                if (false) {
                    Size(point.x, point.y - heightNavigationBar)
                } else {
                    Size(point.x - heightNavigationBar, point.y)
                }
            }
    }

    single { TimeUnit.MILLISECONDS }

    single { 500L }
}

val activityModule = module {
    factory { (lifecycle: Lifecycle) ->
        LifecycleManager(arrayOf(get(QCamera), get(QMLManager)), lifecycle)
        Unit
    }
}

val viewModelModule = module {
    viewModel { ViewModel(get(), get(), get(), get()) }
}

val useCasesModules = module {
    single { GetImageRatioUseCase(get(QCamera)) }
    single { TextRecognitionUseCase(get(QMLManager)) }
    single { FaceDetectionUseCase(get(QMLManager)) }
}

val mlModule = module {
    single<ITextRecognition> { TextRecognitionImp(get(), get()) }
    single<IFaceDetection> { FaceDetectionImp(get(), get(), 0.7f) }

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
        MLManagerImp(get(QCamera), get(), get(), get(), get(), get())
    } binds arrayOf(IMLManager::class, ILifecycleObserver::class)
}

val imageTransformModule = module {
    single<IImageVisible> {
        ImageVisibleImp(get())
    }
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
            get(),
            get(),
            (get() as Size).ratio()
        )
    } binds arrayOf(IImageSource::class, IImageRatio::class, ILifecycleObserver::class)
}

val scheduleModule = module {
    factory<IScheduleProvider> { ScheduleProviderImp() }
}

val mapperModule = module {
    single<Mapper<Image, Bitmap>> {
        BitmapMapper()
    }
}

