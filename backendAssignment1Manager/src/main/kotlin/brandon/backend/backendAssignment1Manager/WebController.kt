package brandon.backend.backendAssignment1Manager

import brandon.backend.backendAssignment1Manager.ResultParser.JsonParseStrategy
import brandon.backend.backendAssignment1Manager.Scaling.ThreadRequestManager
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.async.DeferredResult

@RestController
class WebController{

    @GetMapping("/wc")
    @ResponseBody
    fun getWordCount(@RequestParam(required = true) url: String,
                     @RequestParam(defaultValue = "false") force: Boolean,
                     @RequestHeader(required = false) Accept: String): DeferredResult<String> {
        val asyncResult = DeferredResult<String>()
        ThreadRequestManager.addRequest(url, { asyncResult.setResult(JsonParseStrategy.parseWebResult(it)) }, force)
        return asyncResult
    }
}