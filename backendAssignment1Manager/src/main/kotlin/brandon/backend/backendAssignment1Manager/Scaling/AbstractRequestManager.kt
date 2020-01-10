package brandon.backend.backendAssignment1Manager.Scaling

import brandon.backend.backendAssignment1Manager.WebResult.WebResult
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.collections.HashMap

abstract class AbstractRequestManager {

    protected val timeoutTime = 60000 //1 min in milliseconds
    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val cacheMap: HashMap<String,WebResult> = HashMap()

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
        cacheMap[url] = result

        processNextRequest()
    }

}