package com.iyxan23.asperge.sketchware.models

import com.iyxan23.asperge.sketchware.models.library.LibraryItem

data class Library(
    val firebaseDB: LibraryItem,
    val compat: LibraryItem,
    val admob: LibraryItem,
    val googleMap: LibraryItem,
)