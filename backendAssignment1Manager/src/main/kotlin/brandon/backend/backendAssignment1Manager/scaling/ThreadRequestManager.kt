package brandon.backend.backendAssignment1Manager.scaling

import brandon.backend.backendAssignment1Manager.webResult.WebCounter
import java.util.concurrent.Executors

object ThreadRequestManager : AbstractRequestManager() {

    private const val maxThreads = 5

    //Idk to use cached or fixed threadpool
    private val threadPool = Executors.newFixedThreadPool(maxThreads)

    override fun processNextRequest() {
        if(requestQ.size > 0) {
            val next = requestQ.poll()
            threadPool.execute(WebCounter(next.first) { finishRequest(next.first, it) })
        }
    }
}