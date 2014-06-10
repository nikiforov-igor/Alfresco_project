[
    <#if tags?? && tags?length gt 0>
        <#list tags as tag>
            "${jsonUtils.encodeJSONString(tag)}"<#if tag_has_next>,</#if>
        </#list>
    </#if>
]