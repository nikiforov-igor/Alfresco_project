<#assign htmlId = fieldHtmlId/>

<script type="text/javascript">
    //    <![CDATA[
    function getCurrency() {
        Alfresco.util.Ajax.request(
                {
                    url: Alfresco.constants.PROXY_URI + "lecm/contracts/getCurrency",
                    dataObj: {
                        nodeRef: "${form.arguments.itemId}"
                    },
                    successCallback: {
                        fn: function (response) {
                            if (response.json.currency) {
                                var id = Dom.get("${htmlId}");
                                id.innerHTML = "${field.value} " + response.json.currency;
                                id.removeAttribute('id');
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
        <span id="${htmlId}" class="viewmode-value">${field.value}</span>
    </div>
</div>