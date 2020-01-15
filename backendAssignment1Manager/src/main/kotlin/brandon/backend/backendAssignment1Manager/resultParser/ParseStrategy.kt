package brandon.backend.backendAssignment1Manager.resultParser

import brandon.backend.backendAssignment1Manager.webResult.WebResult

interface ParseStrategy {

    fun parseWebResult(result: WebResult): String

}