<#assign htmlId = fieldHtmlId/>

<script type="text/javascript">
    //    <![CDATA[
    function getBoss() {
        Alfresco.util.Ajax.request(
                {
                    url: Alfresco.constants.PROXY_URI + "lecm/orgstructure/api/getOrganizationBoss",
                    successCallback: {
                        fn: function (response) {
                            if (response.json.boss) {
                                var bossField = Dom.get("${htmlId}");
                                if (bossField != null) {
                                    bossField.innerHTML = response.json.bossShortName;
                                }
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
    YAHOO.util.Event.onDOMReady(getBoss);
    //]]>
</script>

<div class="form-field">
    <div class="viewmode-field">
        <span class="viewmode-label">${field.label?html}:</span>
        <span id="${htmlId}" class="viewmode-value" style="font-weight: bold"></span>
    </div>
</div>