package brandon.backend.backendAssignment1Manager.webResult

class WebResult constructor(var total_words: Int,
                            val top10: List<String>) {
    val resultTime = System.currentTimeMillis()
}