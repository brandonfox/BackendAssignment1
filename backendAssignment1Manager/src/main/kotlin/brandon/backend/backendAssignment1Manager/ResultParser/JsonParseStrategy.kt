package brandon.backend.backendAssignment1Manager.ResultParser

import brandon.backend.backendAssignment1Manager.WebResult.WebResult
import org.json.JSONObject

object JsonParseStrategy : ParseStrategy() {

    override fun parseWebResult(result: WebResult): String {
        val answer = JSONObject()
        answer.put("WordCount",result.wordCount)
        answer.put("Words",result.wordMap)
        return answer.toString()
    }
}