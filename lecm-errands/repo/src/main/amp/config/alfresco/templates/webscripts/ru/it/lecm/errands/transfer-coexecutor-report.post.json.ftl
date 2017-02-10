<#escape x as jsonUtils.encodeJSONString(x)>
{
    "formData":
        {
            "formAttachments": "${formData.formAttachments}",
            "formConnections": "${formData.formConnections}",
            "formText": "${formData.formText}"
        }
}
</#escape>
