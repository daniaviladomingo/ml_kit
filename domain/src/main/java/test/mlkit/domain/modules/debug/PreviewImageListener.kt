package test.mlkit.domain.modules.debug

import test.mlkit.domain.model.Image

interface PreviewImageListener {
    fun onPreviewImage(image: Image)
}