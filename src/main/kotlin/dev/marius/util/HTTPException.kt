package dev.marius.util

class HTTPException(val requestURL: String, val responseCode: Int) : Exception()