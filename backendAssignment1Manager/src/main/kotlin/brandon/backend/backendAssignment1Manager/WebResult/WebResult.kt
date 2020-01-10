package brandon.backend.backendAssignment1Manager.WebResult

class WebResult constructor(var wordCount: Int,
                            val wordMap: Map<String,Int>) {
    val resultTime = System.currentTimeMillis()
}