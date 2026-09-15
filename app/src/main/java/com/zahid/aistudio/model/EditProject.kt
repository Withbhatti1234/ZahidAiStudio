package com.zahid.aistudio.model

import android.net.Uri

data class EditProject(
    val source: Uri,
    val brightness: Float = 0f,
    val contrast: Float = 1f,
    val enhanced: Boolean = false,
    val backgroundRemoved: Boolean = false
)
