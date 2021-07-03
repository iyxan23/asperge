package com.iyxan23.asperge

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Decryptor {

    private val sketchwareCipherInstance = Cipher.getInstance("AES/CBC/PKCS5Padding")
    private val sketchwarePass = "sketchwaresecure".toByteArray()

    init {
        sketchwareCipherInstance.init(2, SecretKeySpec(sketchwarePass, "AES"), IvParameterSpec(sketchwarePass))
    }

    fun decrypt(content: ByteArray): String = String(sketchwareCipherInstance.doFinal(content))
}