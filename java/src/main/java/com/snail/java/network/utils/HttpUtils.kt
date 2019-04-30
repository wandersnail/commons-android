package com.snail.network.utils

import com.snail.java.network.converter.Converter
import io.reactivex.Observable
import okhttp3.ResponseBody
import java.io.IOException
import java.io.InputStream
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*



/**
 * 描述:
 * 时间: 2018/12/24 11:07
 * 作者: zengfansheng
 */
object HttpUtils {
    class SSLParams {
        var sSLSocketFactory: SSLSocketFactory? = null
        var trustManager: X509TrustManager? = null
    }

    @JvmStatic
    fun getSslSocketFactory(certificates: Array<InputStream>?, bksFile: InputStream?, password: String?): SSLParams {
        val sslParams = SSLParams()
        try {
            val trustManagers = prepareTrustManager(certificates)
            val keyManagers = prepareKeyManager(bksFile, password)
            val sslContext = SSLContext.getInstance("TLS")
            val trustManager: X509TrustManager
            if (trustManagers != null) {
                trustManager = MyTrustManager(chooseTrustManager(trustManagers)!!)
            } else {
                trustManager = UnSafeTrustManager()
            }
            sslContext.init(keyManagers, arrayOf<TrustManager>(trustManager), null)
            sslParams.sSLSocketFactory = sslContext.socketFactory
            sslParams.trustManager = trustManager
            return sslParams
        } catch (e: Exception) {
            throw AssertionError(e)
        }
    }

    class UnSafeTrustManager : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}

        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }
    }

    private fun prepareTrustManager(certificates: Array<InputStream>?): Array<TrustManager>? {
        if (certificates == null || certificates.isEmpty()) return null
        try {
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null)
            for ((index, certificate) in certificates.withIndex()) {
                val certificateAlias = Integer.toString(index)
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate))
                try {
                    certificate.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(keyStore)
            return trustManagerFactory.trustManagers
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun prepareKeyManager(bksFile: InputStream?, password: String?): Array<KeyManager>? {
        try {
            if (bksFile == null || password == null) return null
            val clientKeyStore = KeyStore.getInstance("BKS")
            clientKeyStore.load(bksFile, password.toCharArray())
            val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            keyManagerFactory.init(clientKeyStore, password.toCharArray())
            return keyManagerFactory.keyManagers
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun chooseTrustManager(trustManagers: Array<TrustManager>): X509TrustManager? {
        for (trustManager in trustManagers) {
            if (trustManager is X509TrustManager) {
                return trustManager
            }
        }
        return null
    }


    private class MyTrustManager @Throws(NoSuchAlgorithmException::class, KeyStoreException::class) 
    constructor(private val localTrustManager: X509TrustManager) : X509TrustManager {
        private val defaultTrustManager: X509TrustManager?

        init {
            val var4 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            var4.init(null as KeyStore?)
            defaultTrustManager = chooseTrustManager(var4.trustManagers)
        }


        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            try {
                defaultTrustManager!!.checkServerTrusted(chain, authType)
            } catch (ce: CertificateException) {
                localTrustManager.checkServerTrusted(chain, authType)
            }
        }

        override fun getAcceptedIssuers(): Array<X509Certificate?> {
            return arrayOfNulls(0)
        }
    }

    /**
     * 截取baseurl
     */
    @JvmStatic
    fun getBaseUrl(url: String): String {
        var index = url.indexOf("://")
        val subUrl = url.substring(index + 3)
        val urlHead = url.substring(0, index + 3)
        index = subUrl.indexOf("/")
        return if (index != -1) {
            urlHead + subUrl.substring(0, index)
        } else url
    }

    internal fun <T> convertObservable(observable: Observable<ResponseBody>, converter: Converter<ResponseBody, T>): Observable<T> {
        return observable.map { converter.convert(it) }
    }
}
