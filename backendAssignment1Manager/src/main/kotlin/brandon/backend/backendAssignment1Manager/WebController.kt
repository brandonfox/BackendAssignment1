package brandon.backend.backendAssignment1Manager

import brandon.backend.backendAssignment1Manager.resultParser.HtmlParseStrategy
import brandon.backend.backendAssignment1Manager.resultParser.JsonParseStrategy
import brandon.backend.backendAssignment1Manager.resultParser.ParseStrategy
import brandon.backend.backendAssignment1Manager.resultParser.TextParseStrategy
import brandon.backend.backendAssignment1Manager.scaling.ThreadRequestManager
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.async.DeferredResult

@RestController
class WebController() {

    private final val parseStrats = HashMap<String,ParseStrategy>()

    init {
        parseStrats["application/json"] = JsonParseStrategy
        parseStrats["text/plain"] = TextParseStrategy
    }

    @GetMapping("/wc")
    @ResponseBody
    fun getWordCount(@RequestParam(required = true) url: String,
                     @RequestParam(defaultValue = "false") force: Boolean,
                     @RequestHeader(required = false) Accept: String): DeferredResult<String> {
        val asyncResult = DeferredResult<String>()
        val result = if(parseStrats.containsKey(Accept)) parseStrats[Accept] else HtmlParseStrategy
        ThreadRequestManager.addRequest(url, { asyncResult.setResult(result!!.parseWebResult(it)) }, force)
        return asyncResult
    }
}