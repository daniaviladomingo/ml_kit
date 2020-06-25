package test.mlkit.di

import android.content.Context
import android.graphics.Point
import android.view.Display
import android.view.SurfaceView
import android.view.ViewGroup
import android.view.WindowManager
import androidx.lifecycle.Lifecycle
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.binds
import org.koin.dsl.module
import test.mlkit.LifecycleManager
import test.mlkit.camera.ImageSourceImp
import test.mlkit.domain.interactor.GetImageRatioUseCase
import test.mlkit.domain.model.Size
import test.mlkit.domain.modules.IImageRatio
import test.mlkit.domain.modules.IImageSource
import test.mlkit.domain.modules.ILifecycleObserver
import test.mlkit.schedulers.IScheduleProvider
import test.mlkit.schedulers.ScheduleProviderImp
import test.mlkit.ui.ViewModel

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
}

val activityModule = module {
    factory { (lifecycle: Lifecycle) ->
        LifecycleManager(get(), lifecycle)
        Unit
    }
}

val viewModelModule = module {
    viewModel { ViewModel(get(), get()) }
}

val useCasesModules = module {
    single { GetImageRatioUseCase(get()) }
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

    single<IImageSource> {
        ImageSourceImp(
            get(),
            get(),
            (get() as Size).ratio()
        )
    } binds arrayOf(IImageRatio::class, ILifecycleObserver::class)
}

val scheduleModule = module {
    factory<IScheduleProvider> { ScheduleProviderImp() }
}

