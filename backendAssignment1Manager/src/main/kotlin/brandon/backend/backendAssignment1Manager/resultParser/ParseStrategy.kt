package brandon.backend.backendAssignment1Manager.resultParser

import brandon.backend.backendAssignment1Manager.webResult.WebResult

abstract class ParseStrategy {

    abstract fun parseWebResult(result: WebResult): String

}