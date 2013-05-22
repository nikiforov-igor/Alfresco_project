<#assign htmlId = fieldHtmlId/>

<script type="text/javascript">
    //    <![CDATA[
    function getCurrency() {
        Alfresco.util.Ajax.request(
                {
                    url: Alfresco.constants.PROXY_URI + "lecm/contracts/substituteString",
                    dataObj: {
                        nodeRef: "${field.value}",
                        <#if field.control.params.nameSubstituteString??>
                            nameSubstituteString: "${field.control.params.nameSubstituteString}"
                        </#if>
                    },
                    successCallback: {
                        fn: function (response) {
                            console.log(response);
                            if (response.json.substituteString) {
                                var id = Dom.get("${htmlId}");
                                id.innerHTML = "<a href='" + Alfresco.constants.URL_PAGECONTEXT + "document?nodeRef=${field.value}" + "'>" + response.json.substituteString + "</a>";
                            }
                        }
                    },
                    failureMessage:  {
                        fn: function () {
                            alert("failure.message");
                        }
                    }
                });
    }
    YAHOO.util.Event.onDOMReady(getCurrency);
    //]]>
</script>

<div class="form-field">
    <div class="viewmode-field">
        <span class="viewmode-label">${field.label?html}:</span>
        <span id="${htmlId}" class="viewmode-value"></span>
    </div>
</div>