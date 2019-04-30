package cn.zfs.commonsdemo

import com.snail.java.network.NetworkRequester
import com.snail.java.network.converter.StringResponseConverter
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

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
            NetworkRequester.get("http://120.78.49.23:8080/CM/api?action=3001&p=wg2x&t=gimbal&l=1",
                StringResponseConverter(), object : Observer<String> {
                    override fun onSubscribe(p0: Disposable) {
                        println("onSubscribe: $p0")
                    }

                    override fun onNext(fileVerInfo: String) {
                        println(fileVerInfo)
                    }

                    override fun onError(p0: Throwable) {
                        println("onError: $p0")
                    }

                    override fun onComplete() {
                        println("onComplete")
                    }
                })
        }
    }
}