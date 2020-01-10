package brandon.backend.backendAssignment1Manager.webResult

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.*
import kotlin.collections.HashMap

class WebCounter constructor(val url: String, val callback: (r: WebResult) -> Unit) : Runnable {
    override fun run() {
        val d: Document = Jsoup.connect(url).get()
        val pattern = "[a-zA-Z]{1,20}".toRegex()
        val matches = pattern.findAll(d.body().text())
        var wc = 0
        val wordCounts: HashMap<String,Int> = HashMap()
        for(m in matches){
            wc++
            wordCounts.putIfAbsent(m.value,0)
            wordCounts[m.value] = wordCounts[m.value]!! + 1
        }
        callback(WebResult(wc,getTop10(wordCounts)))
    }

    private fun getTop10(words: Map<String,Int>): List<String>{
        val lst = ArrayList<String>(10)
        val q = PriorityQueue<Pair<String,Int>>(Comparator{ o1, o2 -> o1.second - o2.second })
        for(k in words.keys){
            q.add(Pair(k, words[k] ?: error("This shouldn't happen. Map has key with no value")))
        }
        for(i in 0 until 10) {
            lst.add(q.poll().first)
        }
        return lst
    }

}