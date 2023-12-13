package com.mkpateldev.examplesmsretrive.comonUtils

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Arrays
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Aes256 {
    /**
     * OpenSSL"s magic initial bytes.
     */
    private const val SALTED_STR = "Salted__"
    private val SALTED_MAGIC = SALTED_STR.toByteArray(StandardCharsets.US_ASCII)

    /**
     * Encrypt the text
     * @param text The data to encrypt
     * @param password  The password / key to encrypt with.
     * @return A base64 encoded string containing the encrypted data.
     * @throws Exception
     */
    @JvmStatic
    @Throws(Exception::class)
    fun encrypt(text: String, password: String): String {
        val pass =
            password.toByteArray(StandardCharsets.US_ASCII)
        val salt = SecureRandom().generateSeed(8)
        val inBytes = text.toByteArray(StandardCharsets.UTF_8)
        val passAndSalt = array_concat(pass, salt)
        var hash = ByteArray(0)
        var keyAndIv = ByteArray(0)
        var i = 0
        while (i < 3 && keyAndIv.size < 48) {
            val hashData =
                array_concat(hash, passAndSalt)
            val md = MessageDigest.getInstance("MD5")
            hash = md.digest(hashData)
            keyAndIv = array_concat(keyAndIv, hash)
            i++
        }
        val keyValue = Arrays.copyOfRange(keyAndIv, 0, 32)
        val iv = Arrays.copyOfRange(keyAndIv, 32, 48)
        val key = SecretKeySpec(keyValue, "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(iv))
        var data = cipher.doFinal(inBytes)
        data = array_concat(
            array_concat(
                SALTED_MAGIC,
                salt
            ), data
        )
        return Base64.encodeToString(
            data,
            Base64.DEFAULT
        )
    }

    /**
     * Decrypt encrypted text
     * @param encrypted The encrypted data
     * @param password  Password
     * @return Decrypted text
     */
    @Throws(Exception::class)
    fun decrypt(encrypted: String, password: String): String {
        var encrypted = encrypted
        encrypted = encrypted.replace("\"", "")
        val pass =
            password.toByteArray(StandardCharsets.US_ASCII)
        val inBytes = Base64.decode(
            encrypted,
            Base64.DEFAULT
        )
        val shouldBeMagic = Arrays.copyOfRange(
            inBytes,
            0,
            SALTED_MAGIC.size
        )
        require(
            Arrays.equals(
                shouldBeMagic,
                SALTED_MAGIC
            )
        ) { "Initial bytes from input do not match OpenSSL SALTED_MAGIC salt value." }
        val salt = Arrays.copyOfRange(
            inBytes,
            SALTED_MAGIC.size,
            SALTED_MAGIC.size + 8
        )
        val passAndSalt = array_concat(pass, salt)
        var hash = ByteArray(0)
        var keyAndIv = ByteArray(0)
        var i = 0
        while (i < 3 && keyAndIv.size < 48) {
            val hashData =
                array_concat(hash, passAndSalt)
            val md = MessageDigest.getInstance("MD5")
            hash = md.digest(hashData)
            keyAndIv = array_concat(keyAndIv, hash)
            i++
        }
        val keyValue = Arrays.copyOfRange(keyAndIv, 0, 32)
        val key = SecretKeySpec(keyValue, "AES")
        val iv = Arrays.copyOfRange(keyAndIv, 32, 48)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
        val clear = cipher.doFinal(inBytes, 16, inBytes.size - 16)
        return String(clear, StandardCharsets.UTF_8)
    }

    private fun array_concat(a: ByteArray, b: ByteArray): ByteArray {
        val c = ByteArray(a.size + b.size)
        System.arraycopy(a, 0, c, 0, a.size)
        System.arraycopy(b, 0, c, a.size, b.size)
        return c
    }

    /**
     * Main function for tests
     * @param args
     * @throws Exception
     */
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val encrypted = encrypt("TEXT", "PASSWORD")
        println(encrypted)
        val decrypted =
            decrypt(encrypted, "PASSWORD")
        println(decrypted)
    }
}