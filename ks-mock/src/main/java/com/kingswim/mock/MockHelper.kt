package com.kingswim.mock

import android.content.Context

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import kotlin.collections.HashMap

object MockHelper {
    const val TAG = "MOCK_MODULE"
    private val mockData: MutableMap<String, MutableMap<String, Any>> = HashMap()
    private var openMock: Boolean = false
    private var gson: Gson = Gson()

    /**
     * 初始化所有mock数据
     * 备注：最好是在Application里面调用，提前准备好数据
     * @param assetsPath 文件名或者文件夹名
     * @param isFolder  是否是文件夹
     */
    fun init(context: Context, assetsPath: String, isFolder: Boolean = false) {
        mockData.clear()
        if (isFolder) {
            context.assets.list(assetsPath)?.forEach {
                val parserMap = parserMockJson(context, ConvertUtils.join(assetsPath,it))
                mockData.putAll(parserMap)
            }
        } else {
            val parserMap = parserMockJson(context, assetsPath)
            mockData.putAll(parserMap)
        }
    }
    /**
     * 是否打开模拟数据
     */
    fun setMockMode(openMock: Boolean) {
        this.openMock = openMock
    }
    /**
     * 通过路径获取对应的模拟数据。
     * @param url
     * @return
     */
    fun getMockData(url: String): String {
        val keys: Set<String> = mockData.keys
        for (key in keys) {
            if (url.contains(key)) {
                val result = mockData[key]
                //除非手动指定为false,否则默认只要配置了模拟数据就会使用
                return if ( result != null && result["mock"] != false) {
                    gson.toJson(result)
                } else ""
            }
        }
        return ""
    }


    private fun parserMockJson(
        context: Context,
        assetsPath: String
    ): MutableMap<String, MutableMap<String, Any>> {
        val parserMap: MutableMap<String, MutableMap<String, Any>> = HashMap()
        if (openMock && assetsPath.isNotEmpty()) {
            try {
                val inputStream = context.assets.open(assetsPath)
                val mockData = ConvertUtils.inputStream2String(inputStream, "UTF-8")
                if (mockData.isNotEmpty()) {
                    val type = object : TypeToken<MutableMap<String, MutableMap<String, Any>>>() {}.type
                    //解析文件获得的总体Json
                    val alLData = gson.fromJson<MutableMap<String, MutableMap<String, Any>>>(mockData, type)
                    for ((urlPath, value) in alLData) {
                        //判断 key里面是否含有多个 key.指向同一个 value
                        //key之间用";"分割
                        if (urlPath.contains(";")) {
                            //将key一个个取出来。赋值同样的 value
                            for (key in urlPath.split(";".toRegex()).toTypedArray()) {
                                parserMap[key] = value
                            }
                        } else {
                            parserMap[urlPath] = value
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return parserMap
    }
}