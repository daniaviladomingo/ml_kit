package test.mlkit.domain.modules

import test.mlkit.domain.model.CameraFacing
import test.mlkit.domain.model.Size

interface ICameraResolution {
    fun getResolution(facing: CameraFacing): Size
}