package brandon.backend.backendAssignment1Manager

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

@SpringBootTest
class BackendAssignment1ManagerApplicationTests {

	val testUrls = listOf("https://google.com",
			"https://matplotlib.org/api/_as_gen/matplotlib.axes.Axes.axvline.html",
			"https://github.com/",
			"https://www.wikipedia.org/")

	val plainTextResult = listOf("total_words=7,top10=[Search,Chrome,English,Gmail,Google]",
			"total_words=486,top10=[or,the,float,a,of,matplotlib,default,axes,x,axvline]",
			"total_words=958,top10=[GitHub,to,and,the,your,for,a,you,more,work]",
			"total_words=554,top10=[Free,l,n,articles,ng,s,a,Wikipedia,e,Bahasa]")
	val jsonResult = listOf("{\"WordCount\":7,\"Words\":[\"Search\",\"Chrome\",\"English\",\"Gmail\",\"Google\"]}",
			"{\"WordCount\":486,\"Words\":[\"or\",\"the\",\"float\",\"a\",\"of\",\"matplotlib\",\"default\",\"axes\",\"x\",\"axvline\"]}",
			"{\"WordCount\":958,\"Words\":[\"GitHub\",\"to\",\"and\",\"the\",\"your\",\"for\",\"a\",\"you\",\"more\",\"work\"]}",
			"{\"WordCount\":554,\"Words\":[\"Free\",\"l\",\"n\",\"articles\",\"ng\",\"s\",\"a\",\"Wikipedia\",\"e\",\"Bahasa\"]}")
	val htmlResult = listOf("<html>\n" +
			"    <body>\n" +
			"        <div>\n" +
			"            Word count: 7\n" +
			"        </div>\n" +
			"        <div>\n" +
			"            Most frequently used words:\n" +
			"            <ol>\n" +
			"                <li>Search</li>\n" +
			"                <li>Chrome</li>\n" +
			"                <li>English</li>\n" +
			"                <li>Gmail</li>\n" +
			"                <li>Google</li>\n" +
			"            </ol>\n" +
			"        </div>\n" +
			"    </body>\n" +
			"</html>","<html>\n" +
			"    <body>\n" +
			"        <div>\n" +
			"            Word count: 486\n" +
			"        </div>\n" +
			"        <div>\n" +
			"            Most frequently used words:\n" +
			"            <ol>\n" +
			"                <li>or</li>\n" +
			"                <li>the</li>\n" +
			"                <li>float</li>\n" +
			"                <li>a</li>\n" +
			"                <li>of</li>\n" +
			"                <li>matplotlib</li>\n" +
			"                <li>default</li>\n" +
			"                <li>axes</li>\n" +
			"                <li>x</li>\n" +
			"                <li>axvline</li>\n" +
			"            </ol>\n" +
			"        </div>\n" +
			"    </body>\n" +
			"</html>",
			"<html>\n" +
					"    <body>\n" +
					"        <div>\n" +
					"            Word count: 958\n" +
					"        </div>\n" +
					"        <div>\n" +
					"            Most frequently used words:\n" +
					"            <ol>\n" +
					"                <li>GitHub</li>\n" +
					"                <li>to</li>\n" +
					"                <li>and</li>\n" +
					"                <li>the</li>\n" +
					"                <li>your</li>\n" +
					"                <li>for</li>\n" +
					"                <li>a</li>\n" +
					"                <li>you</li>\n" +
					"                <li>more</li>\n" +
					"                <li>work</li>\n" +
					"            </ol>\n" +
					"        </div>\n" +
					"    </body>\n" +
					"</html>",
			"<html>\n" +
					"    <body>\n" +
					"        <div>\n" +
					"            Word count: 554\n" +
					"        </div>\n" +
					"        <div>\n" +
					"            Most frequently used words:\n" +
					"            <ol>\n" +
					"                <li>Free</li>\n" +
					"                <li>l</li>\n" +
					"                <li>n</li>\n" +
					"                <li>articles</li>\n" +
					"                <li>ng</li>\n" +
					"                <li>s</li>\n" +
					"                <li>a</li>\n" +
					"                <li>Wikipedia</li>\n" +
					"                <li>e</li>\n" +
					"                <li>Bahasa</li>\n" +
					"            </ol>\n" +
					"        </div>\n" +
					"    </body>\n" +
					"</html>")

	fun doTests(url: List<String>, expectedOutput: List<String>, headers: Map<String, String>? = null, callback: ((con: HttpURLConnection, time: Long) -> Unit)? = null){
		var testsComplete = 0
		for(i in url.indices){
			sendUnblockingRequest(url[i],expectedOutput[i],{ con,time ->
				println("Request to ${url[i]} took $time milliseconds")
				if(callback != null){
					callback(con,time)
				}
				testsComplete++
			},headers)
		}
		runBlocking {
			while(testsComplete < testUrls.size){
				delay(100)
			}
		}
	}

	@Test
	fun plainTextTest() {
		doTests(testUrls,plainTextResult,mapOf(Pair("Accept","text/plain")))
	}

	@Test
	fun jsonTest(){
		doTests(testUrls,jsonResult,mapOf(Pair("Accept","application/json")))
	}

	@Test
	fun htmlTest(){
		doTests(testUrls,htmlResult)
	}

	@Test
	fun cacheTest(){
		doTests(testUrls,htmlResult)
		doTests(testUrls,htmlResult,callback = {con,time ->
			if(con.url.query.split("=")[1] == testUrls[3]) {
				assert(con.getHeaderField("Cached") == "True")
				println("${con.url} took $time milliseconds to fetch from cache")
			}
		})
	}

	@Test
	fun trafficTest(){
		val testNos = 100

		val resultMap = ConcurrentHashMap<String,ArrayList<Long>>()

		@Synchronized
		fun addResult(l: Long, url:String){
			resultMap[url]!!.add(l)
		}

		for(i in testUrls.indices){
			resultMap.putIfAbsent(testUrls[i],ArrayList())
			for(n in 0 until testNos){
				sendUnblockingRequest(testUrls[i],plainTextResult[i], { con,time ->
					addResult(time,testUrls[i])
				}, mapOf(Pair("Accept","text/plain")))
			}
		}
		fun allTestsDone(): Boolean{
			var done = true
			for(k in resultMap.keys()){
				println("Tests for url: $k completed: ${resultMap[k]!!.size}/$testNos")
				if(resultMap[k]!!.size < testNos)
					done = false
			}
			return done
		}
		runBlocking {
			while(!allTestsDone())
				delay(100)
		}
		for(i in resultMap.keys()){
			var total = 0L
			var shortest = Long.MAX_VALUE
			var longest = 0L
			for(l in resultMap[i]!!){
				total += l
				if(l < shortest) shortest = l
				if(l > longest) longest = l
			}
			println("URL $i mean request time: ${total/testNos}, Longest request time: $longest, Shortest request time: $shortest")
		}
	}

	@Test
	fun forceTest(){
		val forceUrls = ArrayList<String>()
		for(i in testUrls.indices){
			forceUrls.add("${testUrls[i]}&force=true")
		}
		doTests(forceUrls,htmlResult,callback = {con, time -> assert(con.getHeaderField("Cached") == null) })
	}

	fun sendUnblockingRequest(url: String, expectedOutput: String, callback: ((con: HttpURLConnection,time: Long) -> Unit)? = null, headers: Map<String,String>? = null){
		val u = URL("http://127.0.0.1:8080/wc?url=$url")
		GlobalScope.launch {
			val startTime = System.currentTimeMillis()
			with(u.openConnection() as HttpURLConnection) {
				requestMethod = "GET"

				if(headers != null) {
					for (k in headers.keys) {
						setRequestProperty(k,headers[k])
					}
				}

				val response = inputStream.reader().readText()

				assert(response == expectedOutput)

				val timeTaken = System.currentTimeMillis() - startTime

				if (callback != null)
					callback(this,timeTaken)

				disconnect()

			}
		}
	}

}
