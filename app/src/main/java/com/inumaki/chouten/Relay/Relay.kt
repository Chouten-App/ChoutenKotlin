package com.inumaki.chouten.Relay

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interception.logging.JavetStandardConsoleInterceptor
import com.caoccao.javet.interop.V8Host
import com.caoccao.javet.interop.V8Runtime
import com.caoccao.javet.values.reference.V8ValueArray
import com.caoccao.javet.values.reference.V8ValueObject
import com.caoccao.javet.values.reference.V8ValuePromise
import com.inumaki.chouten.Models.DiscoverData
import com.inumaki.chouten.Models.DiscoverSection
import com.inumaki.chouten.Models.InfoData
import com.inumaki.chouten.Models.Label
import com.inumaki.chouten.Models.MediaItem
import com.inumaki.chouten.Models.MediaList
import com.inumaki.chouten.Models.Pagination
import com.inumaki.chouten.Models.SeasonData
import com.inumaki.chouten.Models.Status
import com.inumaki.chouten.Models.Titles
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.BufferedReader
import java.util.concurrent.TimeUnit


class RequestInterceptor {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Increase connection timeout
        .readTimeout(30, TimeUnit.SECONDS) // Increase read timeout
        .writeTimeout(30, TimeUnit.SECONDS) // Increase write timeout
        .build()

    @V8Function
    fun consoleLog(message: String) {
        Log.d("Relay", message)

        sendLogToLogServer(message)

        // TODO: add message to local logging manager
    }

    private fun sendLogToLogServer(message: String) {
        // TODO: send message to log server
        val url = "http://192.168.1.163:3000/log" // Replace with the actual IP

        val request = Request.Builder()
            .url(url)
            .method("POST", message.toRequestBody("text/plain".toMediaTypeOrNull()))
            .build()

        Log.d("Log Server", "Sending to log server.")

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    println("Failed to send log: ${response.message}")
                } else {
                    println("Log sent successfully")
                }
            }
        } catch (e: Exception) {
            Log.e("Log Server", e.message.toString())
        }

    }

    @V8Function
    fun request(url: String, method: String, headers: Map<String, String> = mapOf()): String {
        val deferred = CoroutineScope(Dispatchers.IO).async {
            performRequest(url, method, headers)
        }

        return runBlocking { deferred.await() }
    }

    private fun performRequest(url: String, method: String, headers: Map<String, String> = mapOf()): String {
        val headersList = headers.toHeaders()

        val request = Request.Builder()
            .url(url)
            .method(method.uppercase(), null)
            .headers(headersList)
            .build()

        Log.d("Relay", "URL: $url")

        return try {
            client.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""

                println("Body: $bodyString")

                JSONObject().apply {
                    put("statusCode", response.code)
                    put("headers", JSONObject(response.headers.toMap()))
                    put("contentType", response.header("Content-Type") ?: "unknown")
                    put("body", bodyString)
                }.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            JSONObject().apply {
                put("statusCode", 500)
                put("headers", JSONObject())
                put("contentType", "application/json")
                put("body", "Error: ${e.message}")
            }.toString()
        }
    }
}

object Relay {
    private lateinit var appContext: Context
    private lateinit var v8Runtime: V8Runtime

    var commonJs = ""

    fun setContext(context: Context) {
        if (!this::appContext.isInitialized) {
            appContext = context.applicationContext
        }
    }

    fun initialize() {
        v8Runtime = V8Host.getV8Instance().createV8Runtime()

        val javetStandardConsoleInterceptor = JavetStandardConsoleInterceptor(v8Runtime)
        javetStandardConsoleInterceptor.register(v8Runtime.globalObject)

        commonJs = loadJsFromAssets(appContext, "common.js")

        // fetch code.js from Documents folder
        val codeJs = loadCodeJs(appContext) ?: return // loadJsFromAssets(appContext, "code.js")

        Log.d("Relay", "Code.js initialized.")

        val relayInterceptor = RequestInterceptor()

        v8Runtime.createV8ValueObject().use { v8ValueObject ->
            v8Runtime.globalObject.set("RelayBridge", v8ValueObject)
            v8ValueObject.bind(relayInterceptor)
        }

        v8Runtime.getExecutor(commonJs.trimIndent()).executeVoid()
        v8Runtime.getExecutor(codeJs.trimIndent()).executeVoid()

        // Next: Make it load a proper module
        v8Runtime.getExecutor("const instance = new source.default();".trimIndent()).executeVoid()
    }

    private fun loadCodeJs(context: Context): String? {
        val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val folderUriString = sharedPrefs.getString("folder_uri", null) ?: return null
        val folderUri = Uri.parse(folderUriString)

        val folder = DocumentFile.fromTreeUri(context, folderUri) ?: return null
        val codeJsFile = folder.findFile("code.js") ?: return null

        context.contentResolver.openInputStream(codeJsFile.uri)?.use { inputStream ->
            return inputStream.bufferedReader().use { it.readText() }
        }

        return null
    }

    suspend fun discover(): List<DiscoverSection> = withContext(Dispatchers.IO) {
        val promise = v8Runtime.getExecutor("instance.discover().then(res => res)").execute<V8ValuePromise>()

        v8Runtime.await() // Make sure this doesn't block the UI

        if (promise == null || promise.isRejected) {
            println("Error: Promise was rejected")
            return@withContext emptyList()
        }

        val returnObject = promise.getResult<V8ValueArray>() ?: return@withContext emptyList()

        return@withContext v8ArrayToDiscoverArray(returnObject)
    }

    suspend fun info(url: String): InfoData? = withContext(Dispatchers.IO) {
        val promise = v8Runtime.getExecutor("instance.info(\"$url\").then(res => res)").execute<V8ValuePromise>()

        v8Runtime.await() // Make sure this doesn't block the UI

        if (promise == null || promise.isRejected) {
            println("Error: Promise was rejected")
            return@withContext null
        }

        val returnObject = promise.getResult<V8ValueObject>() ?: return@withContext null

        Log.d("Relay", "Info Data conversion started.")

        return@withContext v8ObjectToInfoData(returnObject, url = url)
    }

    suspend fun media(url: String): List<MediaList> = withContext(Dispatchers.IO) {
        val promise = v8Runtime.getExecutor("instance.media(\"$url\").then(res => res)").execute<V8ValuePromise>()

        v8Runtime.await() // Make sure this doesn't block the UI

        if (promise == null || promise.isRejected) {
            println("Error: Promise was rejected")
            return@withContext emptyList()
        }

        val returnObject = promise.getResult<V8ValueArray>() ?: return@withContext emptyList()

        Log.d("Relay", "MediaList conversion started.")

        return@withContext v8ArrayToMediaList(returnObject)
    }

    private fun v8ObjectToInfoData(v8Object: V8ValueObject, url: String): InfoData {
        val titlesObj = v8Object.get<V8ValueObject>("titles")

        val titles = Titles(
            primary = titlesObj.getString("primary"),
            secondary = titlesObj.getString("secondary") ?: null
        )

        val poster = v8Object.getString("poster")
        val banner: String? = v8Object.getString("banner")
        val description = v8Object.getString("description")
        val status = when(v8Object.getInteger("status")) {
            else -> {
                Status.COMPLETED
            }
        }

        val rating = v8Object.getFloat("rating")
        val yearReleased = v8Object.getInteger("yearReleased")

        val seasonsList = v8Object.get<V8ValueArray>("seasons")

        val seasons = mutableListOf<SeasonData>()

        for (i in 0 until seasonsList.length) {
            val seasonObj = seasonsList.get<V8ValueObject>(i) ?: continue

            val name: String? = seasonObj.getString("name")
            val seasonUrl = seasonObj.getString("url")

            seasons.add(
                SeasonData(
                    name = name ?: "N/A",
                    url = seasonUrl
                )
            )
        }

        return InfoData(
            url = url,
            titles = titles,
            altTitles = emptyList(),
            poster = poster,
            banner = banner,
            description = description,
            status = status,
            rating = rating,
            yearReleased = yearReleased,
            mediaType = 0,
            seasons = seasons
        )
    }

    private fun v8ArrayToDiscoverArray(v8Array: V8ValueArray): List<DiscoverSection> {
        val sections = mutableListOf<DiscoverSection>()

        for (i in 0 until v8Array.length) {
            val sectionObj = v8Array.get<V8ValueObject>(i) ?: continue

            val title = sectionObj.getString("title")
            val type = sectionObj.getInteger("type")

            val list = mutableListOf<DiscoverData>()
            try {
                val listArray = sectionObj.get<V8ValueArray>("data")

                println(listArray.length)

                for (j in 0 until listArray.length) {
                    println(j)
                    val dataObj = listArray.get<V8ValueObject>(j)

                    val url = dataObj.getString("url")
                    val titlesObj = dataObj.get<V8ValueObject>("titles")
                    val poster = dataObj.getString("poster")
                    val banner: String? = dataObj.getString("banner")
                    val description = dataObj.getString("description")
                    val indicator: String? = dataObj.getString("indicator")
                    val current: Int? = dataObj.getInteger("current")
                    val total: Int? = dataObj.getInteger("total")

                    println("FOUND: $poster")

                    val titles = Titles(
                        primary = titlesObj.getString("primary"),
                        secondary = "" // titlesObj.getString("secondary")
                    )

                    val label = Label(
                        text = "",
                        color = ""
                    )

                    list.add(
                        DiscoverData(
                            url = url,
                            titles = titles,
                            poster = poster,
                            banner = banner,
                            description = description,
                            label = label,
                            indicator = indicator,
                            isWidescreen = false,
                            current = current,
                            total = total
                        )
                    )
                }
            } catch (e: Exception) {
                println(e.message)
            }

            sections.add(DiscoverSection(title = title, type = type, list = list))
        }

        println("SECTIONS CONVERTED: $sections")

        return sections
    }

    private fun v8ArrayToMediaList(v8Array: V8ValueArray): List<MediaList> {
        val mediaLists = mutableListOf<MediaList>()

        for (i in 0 until v8Array.length) {
            val mediaListObj = v8Array.get<V8ValueObject>(i)

            val title = mediaListObj.getString("title")
            val pagination = mediaListObj.get<V8ValueArray>("pagination") ?: continue

            val paginationList = mutableListOf<Pagination>()

            for (j in 0 until pagination.length) {
                val paginationObj = pagination.get<V8ValueObject>(j)

                val id = paginationObj.getString("id")
                val pagTitle: String? = paginationObj.getString("title")
                val items = paginationObj.get<V8ValueArray>("items")

                val itemsList = mutableListOf<MediaItem>()

                for (itemIndex in 0 until items.length) {
                    val itemObj = items.get<V8ValueObject>(itemIndex)

                    val url = itemObj.getString("url")
                    val number = itemObj.getInteger("number")
                    val itemTitle: String? = itemObj.getString("title")
                    val thumbnail: String? = itemObj.getString("thumbnail")
                    val description: String? = itemObj.getString("description")

                    itemsList.add(
                        MediaItem(
                            url, number, itemTitle, thumbnail, description
                        )
                    )
                }

                paginationList.add(Pagination(id = id, title = pagTitle, items = itemsList))
            }

            mediaLists.add(MediaList(title = title, pagination = paginationList))
        }

        return mediaLists
    }

    private fun loadJsFromAssets(context: Context, fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use(BufferedReader::readText)
    }
}
