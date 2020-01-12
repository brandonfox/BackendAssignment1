package brandon.backend.backendAssignment1Manager.resultParser

import brandon.backend.backendAssignment1Manager.webResult.WebResult
import freemarker.template.Configuration
import freemarker.template.Template
import java.io.StringWriter

object HtmlParseStrategy : ParseStrategy() {

    private val freeCfg = Configuration()

    init{
        freeCfg.setClassForTemplateLoading(this.javaClass,"/templates/")
    }

    override fun parseWebResult(result: WebResult): String
    {
        val template: Template = freeCfg.getTemplate("output.ftl")
        val w = StringWriter()
        val dataModel = HashMap<String, Any>()
        dataModel["wordCount"] = result.total_words
        dataModel["words"] = result.top10!!
        template.process(dataModel,w)
        return w.toString()
    }
}