<#escape x as x?js_string>
[
    <#list list as item>
    {
        key : "${item.key}",
        amountContracts : "${item.amountContracts}",
        filter : "${item.filter}"
    }
        <#if item_has_next>,</#if>
    </#list>
]
</#escape>