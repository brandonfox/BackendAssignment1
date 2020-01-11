package brandon.backend.backendAssignment1Manager.scaling

import brandon.backend.backendAssignment1Manager.webResult.WebResult
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.collections.HashMap

abstract class AbstractRequestManager {

    //TODO Make request managers use same cache

    protected val timeoutTime = 60000 //1 min in milliseconds
    private val cacheSizeLimit = 3 //TODO change 5 to bigger number after testing
    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val cacheMap: HashMap<String,WebResult> = HashMap()
    private val cacheTimeMap = PriorityQueue<Pair<String,Long>>(UrlTimeComparator())

    private val urlRequests = HashMap<String, ArrayList<(r: WebResult) -> Unit>>()
    protected val requestQ: LinkedList<Pair<String, (r: WebResult) -> Unit>> = LinkedList()
    private val urlsRequested = HashSet<String>()

    protected abstract fun processNextRequest()

    private fun initRequest(url:String, callback: (r: WebResult) -> Unit){
        if(!urlsRequested.contains(url)){
            requestQ.add(Pair(url, callback))
        }
        urlRequests.putIfAbsent(url,ArrayList())
        urlRequests[url]!!.add(callback)
        urlsRequested.add(url)
    }

    fun addRequest(url: String, callback: (r: WebResult) -> Unit, force: Boolean){
        if(force || System.currentTimeMillis() - (cacheMap[url]?.resultTime ?: 0) > ThreadRequestManager.timeoutTime){
            logger.info("Starting new read request at url: $url")
            initRequest(url,callback)
            processNextRequest()
        }
        else{
            logger.info("Sending cached result for url: $url")
            callback(cacheMap[url]!!)
        }
    }

    fun finishRequest(url: String, result: WebResult){
        for(rc in urlRequests[url]!!){
            rc(result)
        }
        urlsRequested.remove(url)
        urlRequests[url]!!.clear()

        updateCache(url,result)

        processNextRequest()
    }

    private fun updateCache(url: String, result: WebResult){
        if(cacheMap.containsKey(url)){
            logger.info("Result already cached. Updating cache")
            cacheTimeMap.remove(Pair<String,Long>(url,0))
        }
        if(cacheMap.size > cacheSizeLimit){
            logger.info("Cache limit exceeded. Removing oldest cached record")
            val oldest = cacheTimeMap.poll()
            cacheMap.remove(oldest.first)
        }
        cacheTimeMap.add(Pair(url,result.resultTime))
        cacheMap[url] = result
    }

    private class UrlTimeComparator : Comparator<Pair<String,Long>> {

        override fun compare(o1: Pair<String, Long>?, o2: Pair<String, Long>?): Int {
            if(o1!!.first == o2!!.first) return 0
            return o1.second.compareTo(o2.second)
        }

    }

}