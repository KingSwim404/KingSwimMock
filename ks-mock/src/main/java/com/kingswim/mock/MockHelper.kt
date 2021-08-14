package com.kingswim.mock

import android.content.Context
import com.google.gson.GsonBuilder
import com.blankj.utilcode.BuildConfig
import android.text.TextUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.PathUtils
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

object MockHelper {
    const val TAG = "MOCK_MODULE"
    private val mockData: MutableMap<String, MutableMap<String, Any>> = HashMap()
    private var openMock: Boolean = false

    /**
     * 初始化所有mock数据
     * 备注：最好是在Application里面调用，提前准备好数据
     */
    fun init(context: Context, assetsFileName: String, isFolder: Boolean = false) {
        mockData.clear()
        if (isFolder) {
            context.assets.list(assetsFileName)?.forEach {
                val parserMap = parserMockJson(context, PathUtils.join(assetsFileName,it))
                mockData.putAll(parserMap)
            }
        } else {
            val parserMap = parserMockJson(context, assetsFileName)
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
                return if ( result != null && result["mock"] != false) {
                    GsonUtils.toJson(result)
                } else ""
            }
        }
        return ""
    }


    private fun parserMockJson(
        context: Context,
        assetsFileName: String
    ): MutableMap<String, MutableMap<String, Any>> {
        val parserMap: MutableMap<String, MutableMap<String, Any>> = HashMap()
        if (openMock && assetsFileName.isNotEmpty()) {
            try {
                val inputStream = context.assets.open(assetsFileName)
                val mockData = ConvertUtils.inputStream2String(inputStream, "UTF-8")
                if (mockData.isNotEmpty()) {
                    val type = object : TypeToken<MutableMap<String, MutableMap<String, Any>>>() {}.type
                    //解析文件获得的总体Json
                    val alLData = GsonUtils.fromJson<MutableMap<String, MutableMap<String, Any>>>(mockData, type)
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