package brandon.backend.backendAssignment1Manager.webResult

data class WebResult constructor(val success: Boolean,
                            var total_words: Int,
                            val top10: List<String>?,
                            val etag: String?,
                            val errorMessage: String = "") {
    var wasCached = false
    var resultTime = System.currentTimeMillis()
    fun update() : WebResult{
        resultTime = System.currentTimeMillis()
        return this.copy()
    }
    fun setCached(){
        wasCached = true
    }
}