package brandon.backend.backendAssignment1Manager.scaling

import brandon.backend.backendAssignment1Manager.webResult.WebResult
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap

abstract class AbstractRequestManager {

    //TODO Make request managers use same cache

    protected val timeoutTime = 60000 //1 min in milliseconds
    private val cacheSizeLimit = 1000 //TODO change 5 to bigger number after testing
    private val logger = LoggerFactory.getLogger(this.javaClass)

    //TODO Move cache stuff into another class

    protected val cacheMap: HashMap<String,WebResult> = HashMap()
    private val cacheTimeMap = PriorityQueue<Pair<String,Long>>(UrlTimeComparator())

    private val urlRequests = ConcurrentHashMap<String, LinkedList<(r: WebResult) -> Unit>>()
    protected val requestQ: LinkedList<Pair<String, (r: WebResult) -> Unit>> = LinkedList()
    private val urlsRequested = HashSet<String>()

    protected abstract fun processNextRequest()
    protected abstract fun processCacheRequest(url:String,callback: (r: WebResult) -> Unit)

    /**
     * Initialises request for queue and others. Returns whether the request has already been requested
     */
    private fun initRequest(url:String, callback: (r: WebResult) -> Unit) : Boolean{
        urlRequests.putIfAbsent(url,LinkedList())
        urlRequests[url]!!.add(callback)
        logger.info("Adding another request to $url. Current size: ${urlRequests[url]!!.size}")
        if(!urlsRequested.contains(url)){
            requestQ.add(Pair(url, callback))
            urlsRequested.add(url)
            return false
        }
        return true
    }

    fun addRequest(url: String, callback: (r: WebResult) -> Unit, force: Boolean){
        if(force || System.currentTimeMillis() - (cacheMap[url]?.resultTime ?: 0) > ThreadRequestManager.timeoutTime){
            logger.info("Starting new read request at url: $url")
            if(!initRequest(url,callback))
                processNextRequest()
        }
        else{
            logger.info("Sending cached result for url: $url")
            processCacheRequest(url,callback)
        }
    }

    fun finishRequest(url: String, result: WebResult){
        if(result.success)
            updateCache(url,result)
        handleAllRequestForUrl(url,result)
    }

    private fun handleAllRequestForUrl(url: String, result: WebResult){
        logger.info("Sending result to ${urlRequests[url]!!.size} clients")

        while(urlRequests[url]!!.isNotEmpty()){
            urlRequests[url]!!.poll()(result)
        }
        urlsRequested.remove(url)

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