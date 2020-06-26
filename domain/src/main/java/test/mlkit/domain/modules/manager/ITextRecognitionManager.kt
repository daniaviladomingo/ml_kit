package test.mlkit.domain.modules.manager

import io.reactivex.Observable

interface ITextRecognitionManager {
    fun read(): Observable<String>
}