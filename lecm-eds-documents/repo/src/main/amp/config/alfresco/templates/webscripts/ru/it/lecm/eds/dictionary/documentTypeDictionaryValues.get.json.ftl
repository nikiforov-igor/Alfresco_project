<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    <#if dictionary??>
        "dictionaryName": "${dictionary.name}",
        "dictionaryNodeRef": "${dictionary.nodeRef}",
        "attributes":
        {
            "name": "${dictionary.properties["cm:name"]}",
            "code": "${dictionary.properties["lecm-doc-dic-dt:documentType-code"]}"
            <#if dictionary.properties["lecm-doc-dic-dt:registration-required"]??>
                ,"isRegistrationRequired": ${dictionary.properties["lecm-doc-dic-dt:registration-required"]?string}
            </#if>
            <#if dictionary.properties["lecm-doc-dic-dt:auto-registration"]??>
                ,"isAutoRegistration": ${dictionary.properties["lecm-doc-dic-dt:auto-registration"]?string}
            </#if>
            <#if dictionary.properties["lecm-doc-dic-dt:es-sign-required"]??>
                ,"isESSignRequired": ${dictionary.properties["lecm-doc-dic-dt:es-sign-required"]?string}
            </#if>
            <#if dictionary.properties["lecm-doc-dic-dt:categories-of-attachments-to-sign"]??>
                ,"categoriesOfAttachmentsToSign": "${dictionary.properties["lecm-doc-dic-dt:categories-of-attachments-to-sign"]}"
            </#if>
        }
    </#if>
}
</#escape>