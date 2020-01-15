package brandon.backend.backendAssignment1Manager.scaling

import brandon.backend.backendAssignment1Manager.webResult.WebResult
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue
import kotlin.collections.HashMap

abstract class AbstractRequestManager {

    private val cacheSizeLimit = 1000
    private val logger = LoggerFactory.getLogger(this.javaClass)

    //TODO Move cache stuff into another class

    protected val cacheMap: HashMap<String,WebResult> = HashMap()
    private val cacheTimeMap = PriorityQueue<Pair<String,Long>>(UrlTimeComparator())

    private val handledClients = ConcurrentHashMap<String,Int>()
    private val urlRequests = ConcurrentHashMap<String, LinkedList<(r: WebResult) -> Unit>>()
    protected val requestQ: BlockingQueue<Pair<String, (r: WebResult) -> Unit>> = LinkedBlockingQueue()
    private val urlsRequested = Collections.synchronizedSet(HashSet<String>())

    protected abstract fun processNextRequest(force: Boolean)

    /**
     * Initialises request for queue and others. Returns whether the request has already been requested
     */
    private fun initRequest(url:String, callback: (r: WebResult) -> Unit) : Boolean{
        synchronized(urlRequests) {
            urlRequests.putIfAbsent(url, LinkedList())
        }
        synchronized(urlRequests[url]!!) {
            urlRequests[url]!!.add(callback)
            logger.info("Adding request to $url. Current size: ${urlRequests[url]!!.size}")
            if (!urlsRequested.contains(url)) {
                requestQ.add(Pair(url, callback))
                urlsRequested.add(url)
                return false
            }
            return true
        }
    }

    fun addRequest(url: String, callback: (r: WebResult) -> Unit, force: Boolean){
        if (!initRequest(url, callback))
            processNextRequest(force)
    }

    fun finishRequest(url: String, result: WebResult){
        if(result.success)
            updateCache(url,result)
        handleAllRequestForUrl(url,result)
    }

    private fun handleAllRequestForUrl(url: String, result: WebResult){
        synchronized(urlRequests[url]!!) {
            logger.info("Sending result to ${urlRequests[url]!!.size} clients for url: $url")


            handledClients.putIfAbsent(url,0)

            while (urlRequests[url]!!.isNotEmpty()) {
                urlRequests[url]!!.poll()(result)
                handledClients[url] = handledClients[url]!! + 1
            }
            urlsRequested.remove(url)
            logger.info("Total handled clients for url $url: ${handledClients[url]!!}")
        }
    }

    private fun updateCache(url: String, result: WebResult){
        if(cacheMap.containsKey(url)){
            cacheTimeMap.remove(Pair<String,Long>(url,0))
        }
        if(cacheMap.size > cacheSizeLimit){
            logger.info("Cache limit exceeded. Removing oldest cached record")
            val oldest = cacheTimeMap.poll()
            cacheMap.remove(oldest.first)
        }
        logger.info("Adding $url to cache")
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