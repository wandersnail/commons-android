package cn.zfs.commonsdemo

import com.snail.java.network.NetworkRequester
import com.snail.java.network.callback.RequestCallback
import com.snail.java.network.converter.StringResponseConverter

/**
 *
 *
 * date: 2019/4/30 18:26
 * author: zengfansheng
 */
class TestJava {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            NetworkRequester.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=MP_APPID&secret=MP_APPSECRET",
                StringResponseConverter(), object : RequestCallback<String> {

                    override fun onSuccess(parsedResp: String) {
                        println(parsedResp)
                        System.exit(0)
                    }

                    override fun onError(t: Throwable) {
                        println("onError: $t")
                        System.exit(0)
                    }
                })
        }
    }
}