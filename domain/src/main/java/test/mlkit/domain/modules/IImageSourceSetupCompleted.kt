package test.mlkit.domain.modules

import io.reactivex.Completable

interface IImageSourceSetupCompleted {
    fun setupCompleted(): Completable
}