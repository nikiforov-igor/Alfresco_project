<#assign htmlId = fieldHtmlId/>
<#assign itemId = (form.arguments.itemId?contains("SpacesStore")) ? string(form.arguments.itemId, '')/>
<script type="text/javascript">
    //    <![CDATA[
    function getCurrency() {
        if (${itemId}) {
            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + "lecm/contracts/getCurrency",
                dataObj: {
                    nodeRef: "${itemId}"
                },
                successCallback: {
                    fn: function (response) {
                        if (response.json.currency) {
                            var id = Dom.get("${htmlId}");
                            id.innerHTML = "${field.value} " + response.json.currency;
                        }
                    }
                },
                failureMessage: "${msg("failure.message")}"
            });
        }
    }
    YAHOO.util.Event.onDOMReady(getCurrency);
    //]]>
</script>

<div class="control currency viewmode">
	<div class="label-div">
		<label>${field.label?html}:</label>
	</div>
	<div class="container">
		<div class="value-div" id="${htmlId}">
			${field.value}
		</div>
	</div>
</div>
<div class="clear"></div>
