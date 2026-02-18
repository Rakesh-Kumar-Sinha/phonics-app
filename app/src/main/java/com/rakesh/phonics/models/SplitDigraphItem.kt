package com.rakesh.phonics.models

data class SplitDigraphItem(
    val word: String,
    val indices: List<Int>,      // e.g. [1, 3]
    val digraphSound: String,     // asset path
    val wordSound: String,     // asset path
)

