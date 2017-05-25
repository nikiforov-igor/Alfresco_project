<#escape x as jsonUtils.encodeJSONString(x)>
{
   "initiatingDocuments": [
        <#if initiatingDocuments?? >
            <#list initiatingDocuments as document>
            "${document.nodeRef.toString()}"
            <#if document_has_next>,</#if>
            </#list>
        </#if>
    ]
}
</#escape>