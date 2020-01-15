package brandon.backend.backendAssignment1Manager.resultParser

import brandon.backend.backendAssignment1Manager.webResult.WebResult
import org.json.JSONObject

object JsonParseStrategy : ParseStrategy {

    override fun parseWebResult(result: WebResult): String
    {
        val answer = JSONObject()
        answer.put("WordCount",result.total_words)
        answer.put("Words",result.top10)
        return answer.toString()
    }
}