package brandon.backend.backendAssignment1Manager.ResultParser

import brandon.backend.backendAssignment1Manager.WebResult.WebResult

abstract class ParseStrategy {

    abstract fun parseWebResult(result: WebResult): String

}