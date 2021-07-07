package com.iyxan23.asperge.sketchware.models

import com.iyxan23.asperge.Decryptor
import java.io.File

data class RawSketchwareProject(
    val logic: ByteArray,
    val view: ByteArray,
    val file: ByteArray,
    val library: ByteArray,
    val resource: ByteArray,
    val project: ByteArray,
) {
    fun decrypt(): DecryptedRawSketchwareProject =
        DecryptedRawSketchwareProject(
            Decryptor.decrypt(logic),
            Decryptor.decrypt(view),
            Decryptor.decrypt(file),
            Decryptor.decrypt(library),
            Decryptor.decrypt(resource),
            Decryptor.decrypt(project),
        )

    fun writeToFolder(folder: File) {
        if (!folder.exists()) folder.mkdir()

        File(folder, "logic"    ).writeBytes(logic      )
        File(folder, "view"     ).writeBytes(view       )
        File(folder, "file"     ).writeBytes(file       )
        File(folder, "library"  ).writeBytes(library    )
        File(folder, "resource" ).writeBytes(resource   )
        File(folder, "project"  ).writeBytes(project    )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RawSketchwareProject

        if (!logic.contentEquals(other.logic)) return false
        if (!view.contentEquals(other.view)) return false
        if (!file.contentEquals(other.file)) return false
        if (!library.contentEquals(other.library)) return false
        if (!resource.contentEquals(other.resource)) return false
        if (!project.contentEquals(other.project)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = logic.contentHashCode()
        result = 31 * result + view.contentHashCode()
        result = 31 * result + file.contentHashCode()
        result = 31 * result + library.contentHashCode()
        result = 31 * result + resource.contentHashCode()
        result = 31 * result + project.contentHashCode()
        return result
    }
}