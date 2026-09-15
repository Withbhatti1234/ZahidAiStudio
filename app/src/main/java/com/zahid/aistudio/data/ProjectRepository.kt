package com.zahid.aistudio.data

import com.zahid.aistudio.model.EditProject

class ProjectRepository {
    private var current: EditProject? = null
    fun save(project: EditProject) { current = project }
    fun get(): EditProject? = current
}
