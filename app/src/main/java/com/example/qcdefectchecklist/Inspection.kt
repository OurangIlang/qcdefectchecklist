package com.example.qcdefectchecklist

data class Inspection(
    val id: Int = 0,
    val batchCode: String,
    val componentName: String,
    val defectType: String,
    val sampleSize: Int,
    val defectCount: Int,
    val status: String
)