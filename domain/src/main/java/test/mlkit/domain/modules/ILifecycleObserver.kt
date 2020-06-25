package test.mlkit.domain.modules

interface ILifecycleObserver {
    fun create()
    fun start()
    fun stop()
    fun destroy()
}