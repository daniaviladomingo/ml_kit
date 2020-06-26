package test.mlkit

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import test.mlkit.domain.modules.ILifecycleObserver

class LifecycleManager(
    private val lifecycleObserver: Array<ILifecycleObserver>,
    lifecycle: Lifecycle
) : LifecycleObserver {
    init {
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun create() {
        lifecycleObserver.forEach { it.create() }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() {
        lifecycleObserver.forEach { it.start() }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        lifecycleObserver.forEach { it.stop() }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        lifecycleObserver.forEach { it.destroy() }
    }
}