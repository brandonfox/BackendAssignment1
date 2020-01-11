<html>
    <body>
        <div>
            Word count: ${wordCount}
        </div>
        <div>
            Most frequently used words:
            <ol>
            <#list words as word>
                <li>${word}</li>
            </#list>
            </ol>
        </div>
    </body>
</html>