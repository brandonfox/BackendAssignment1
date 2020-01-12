package brandon.backend.backendAssignment1Manager.webResult

class WebResult constructor(val success: Boolean,
                            var total_words: Int,
                            val top10: List<String>?,
                            val errorMessage: String = "") {
    val resultTime = System.currentTimeMillis()
}