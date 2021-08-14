package com.example.mock

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ToastUtils
import com.kingswim.mock.HttpMockInterceptor
import com.kingswim.mock.MockHelper
import okhttp3.*
import okio.IOException


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




    }

    override fun onResume() {
        super.onResume()

        //最好在Application里面初始化
        MockHelper.setMockMode(BuildConfig.DEBUG)
        //MockHelper.init(this,"bing.json") //针对单个文件
        MockHelper.init(this,"mock",true) //针对多个文件
        //开始模拟数据
        mock()
    }

    private fun mock() {
        Thread {
            val client = OkHttpClient().newBuilder().apply {
                //只有debug模式下才添加Mock数据拦截器
                if(BuildConfig.DEBUG){
                    addInterceptor(HttpMockInterceptor())
                }
            }.build()
            //创建一个Request
            val request: Request = Request.Builder()
                .get()
                .url("https://cn.bing.com")
                .build()
            //通过client发起请求
            client.newCall(request).enqueue(object : Callback {
                override
                fun onFailure(call: Call, e: IOException) {
                }

                @Throws(IOException::class)
                override
                fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        ThreadUtils.runOnUiThread {
                            ToastUtils.showLong(response.body?.string())
                        }
                    }
                }
            })
        }.start()
    }
}