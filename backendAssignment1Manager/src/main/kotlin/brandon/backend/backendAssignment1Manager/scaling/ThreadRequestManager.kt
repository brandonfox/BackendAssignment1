package brandon.backend.backendAssignment1Manager.scaling

import brandon.backend.backendAssignment1Manager.webResult.WebCounter
import java.util.concurrent.Executors

object ThreadRequestManager : AbstractRequestManager() {

    private const val maxThreads = 15

    //Idk to use cached or fixed threadpool
    private val threadPool = Executors.newFixedThreadPool(maxThreads)

    override fun processNextRequest(force: Boolean) {
        if(requestQ.size > 0) {
            val next = requestQ.poll()
            threadPool.execute(WebCounter(next.first,force, { finishRequest(next.first, it) },cacheMap))
        }
    }
}