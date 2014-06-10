{ "representatives": [
    <#list representatives as representative>
    {
        "nodeRef": "${representative["nodeRef"]}",
        "linkRef": "${representative["linkRef"]}",
        "shortName": "${representative["shortName"]}",
        "isPrimary": ${representative["isPrimary"]?string}
    }<#if representative_has_next>,</#if>
    </#list>
]}