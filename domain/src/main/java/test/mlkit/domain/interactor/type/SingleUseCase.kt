package test.mlkit.domain.interactor.type

import io.reactivex.Single

interface SingleUseCase<P> {
    fun execute(): Single<P>
}