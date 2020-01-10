package brandon.backend.backendAssignment1Manager.WebResult

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.regex.Matcher
import java.util.regex.Pattern

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
        callback(WebResult(wc,wordCounts))
    }
}