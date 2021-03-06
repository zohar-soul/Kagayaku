package org.gosky.aroundight.service

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.vertx.core.impl.StringEscapeUtils
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.ext.mongo.MongoClient
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.gosky.aroundight.http.UploadApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File

/**
 * @Auther: guozhong
 * @Date: 2019-05-27 23:28
 * @Description:
 */


@Service
class UploadService {

    @Autowired
    private lateinit var uploadApi: UploadApi

    @Autowired
    private lateinit var mongo: MongoClient

    fun smms(fileName: String, requestBody: RequestBody): Observable<JsonObject> {

//
        val part = MultipartBody.Part.createFormData("smfile", fileName, requestBody)

        return uploadApi.smms(part)
                .subscribeOn(Schedulers.io())
                .map { response ->
                    File("file-uploads").deleteRecursively()
                    val jsonObject = JsonObject(response.string())
                    if (jsonObject.getString("code") == "success") {
                        val url = jsonObject.getJsonObject("data").getString("url")

                        val document = JsonObject()
                                .put("uuid", fileName)
                                .put("url", url)
                                .put("type", "smms")
                        return@map document
                    } else {
                        throw RuntimeException("smms upload faild!")
                    }
                }


    }


    fun juejin(fileName: String, requestBody: RequestBody): Observable<JsonObject> {

//
        val part = MultipartBody.Part.createFormData("file", fileName, requestBody)

        return uploadApi.juejin(part)
                .subscribeOn(Schedulers.io())
                .map { response ->
                    File("file-uploads").deleteRecursively()
                    val jsonObject = JsonObject(response.string())
                    if (jsonObject.getString("m") == "ok") {
                        val url = jsonObject.getJsonObject("d").getJsonObject("url").getString("https")

                        val document = JsonObject()
                                .put("uuid", fileName)
                                .put("url", url)
                                .put("type", "juejin")
                        return@map document
                    } else {
                        throw RuntimeException("smms upload faild!")
                    }
                }

    }

    fun souhu(fileName: String, requestBody: RequestBody): Observable<JsonObject> {

//
        val part = MultipartBody.Part.createFormData("file", fileName, requestBody)

        return uploadApi.souhu(part)
                .subscribeOn(Schedulers.io())
                .map { response ->
                    File("file-uploads").deleteRecursively()
                    val jsonObject = JsonObject(StringEscapeUtils.unescapeJava(response.string()).let { it.substring(1, it.length - 1) })

                    val url = jsonObject.getString("url")

                    val document = JsonObject()
                            .put("uuid", fileName)
                            .put("url", url)
                            .put("type", "souhu")
                    return@map document

                }


    }
}
