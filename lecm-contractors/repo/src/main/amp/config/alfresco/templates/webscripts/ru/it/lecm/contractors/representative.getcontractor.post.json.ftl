<#escape x as jsonUtils.encodeJSONString(x)!''>
<#if representative?? && contractors??>
{
    "status": "success",
    "childName": "${representative.properties["lecm-representative:surname"]} ${representative.properties["lecm-representative:firstname"]}",
    <#if contractors??>
	"parents": [
        <#list contractors as contractor>
        {
            "nodeRef": "${contractor.nodeRef}",
            "name": "${contractor.properties["lecm-contractor:shortname"]}"
        }<#if contractor_has_next>,</#if>
        </#list>
    ]
    </#if>
}
</#if>
</#escape>
