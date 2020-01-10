package brandon.backend.backendAssignment1Manager.resultParser

import brandon.backend.backendAssignment1Manager.webResult.WebResult

object TextParseStrategy : ParseStrategy() {

    override fun parseWebResult(result: WebResult): String {
        val sb = StringBuilder()
        sb.append("total_words=${result.total_words}")
        sb.append(",top10=[")
        for(n in result.top10){
            sb.append("$n,")
        }
        sb.deleteCharAt(sb.length-1)
        sb.append("]")
        return sb.toString()
    }
}