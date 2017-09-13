<#assign htmlId = fieldHtmlId/>

<script type="text/javascript">
    //    <![CDATA[
    function getCurrency() {
        var nodeRef = '${itemId}';
        if (nodeRef && LogicECM.module.Base.Util.isNodeRef(nodeRef)) {
            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + "lecm/contracts/getCurrency",
                dataObj: {
                    nodeRef: "${form.arguments.itemId}"
                },
                successCallback: {
                    fn: function (response) {
                        if (response.json.currency) {
                            var id = Dom.get(nodeRef);
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
