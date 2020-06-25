package test.mlkit

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import test.mlkit.domain.modules.ILifecycleObserver

class LifecycleManager(
    private val lifecycleObserver: ILifecycleObserver,
    lifecycle: Lifecycle
) : LifecycleObserver {
    init {
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun create() {
        lifecycleObserver.create()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() {
        lifecycleObserver.start()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        lifecycleObserver.stop()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        lifecycleObserver.destroy()
    }
}