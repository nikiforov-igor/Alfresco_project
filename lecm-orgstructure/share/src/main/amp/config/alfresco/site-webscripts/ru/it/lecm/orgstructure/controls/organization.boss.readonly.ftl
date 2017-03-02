<#assign htmlId = fieldHtmlId/>

<script type="text/javascript">
    //    <![CDATA[
    function getBoss() {
        Alfresco.util.Ajax.jsonGet({
            url: Alfresco.constants.PROXY_URI + "lecm/orgstructure/api/getOrganizationBoss",
            successCallback: {
                fn: function (response) {
                    if (response.json.boss) {
                        var bossField = Dom.get("${htmlId}");
                        if (bossField) {
                            bossField.innerHTML = response.json.bossShortName;
                        }
                    }
                },
                scope: this
            },
            failureMessage: "${msg('message.failure')}"
        });
    }
    YAHOO.util.Event.onDOMReady(getBoss);
    //]]>
</script>

<div class="control boss-readonly viewmode">
	<div class="label-div">
		<label>${field.label?html}:</label>
	</div>
	<div class="container">
		<div id="${htmlId}" class="value-div"></div>
	</div>
</div>
<div class="clear"></div>
