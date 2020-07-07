package test.mlkit.ui.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class MLService: Parcelable {
    TEXT_RECOGNITION,
    FACE_DETECTOR,
    BARCODE_SCANNER
}