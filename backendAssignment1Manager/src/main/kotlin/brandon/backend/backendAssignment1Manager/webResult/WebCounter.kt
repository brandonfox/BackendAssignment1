package brandon.backend.backendAssignment1Manager.webResult

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.collections.HashMap

class WebCounter constructor(private val url: String, val force: Boolean = true, val callback: (r:WebResult) -> Unit, private val cacheMap: Map<String, WebResult>? = null, val cacheCallback:((r:WebResult) -> Unit)? = null) : Runnable {

    val logger = LoggerFactory.getLogger(this.javaClass)

    override fun run() {
        try {
            val c = Jsoup.connect(url)
            val d: Document = c.get()
            val etag = c.response().header("Etag")
            if(!force && (cacheMap?.get(url) != null && cacheMap[url]?.etag != null && cacheMap[url]?.etag == etag)){
                logger.info("ETag at $url matches record. Returning cached version")
                val result = (cacheMap[url] ?: error("Kotlin library has a bug in checking for null values")).copy()
                result.setCached()
                callback(result)
                return
            }
            val pattern = "[a-zA-Z]{1,20}".toRegex()
            val matches = pattern.findAll(d.body().text())
            var wc = 0
            val wordCounts: HashMap<String, Int> = HashMap()
            for (m in matches) {
                wc++
                wordCounts.putIfAbsent(m.value, 0)
                wordCounts[m.value] = wordCounts[m.value]!! + 1
            }
            callback(WebResult(true,wc, getTop10(wordCounts),etag))
        }
        catch(e: Exception){
            callback(WebResult(false,-1,null,e.message.toString()))
        }
    }

    private fun getTop10(words: Map<String,Int>): List<String>{
        val lst = ArrayList<String>(10)
        val q = PriorityQueue<Pair<String,Int>>(Comparator{ o1, o2 -> o2.second - o1.second })
        for(k in words.keys){
            q.add(Pair(k, words[k] ?: error("This shouldn't happen. Map has key with no value")))
        }
        for(i in 0 until 10) {
            val next = q.poll()
            if(next != null) {
                lst.add(next.first)
            }
        }
        return lst
    }

}