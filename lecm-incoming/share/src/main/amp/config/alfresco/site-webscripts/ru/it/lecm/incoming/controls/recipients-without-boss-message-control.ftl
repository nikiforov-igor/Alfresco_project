<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign params = field.control.params>

<script type="text/javascript">//<![CDATA[
YAHOO.util.Event.onContentReady("${fieldHtmlId}-value", function () {
    Alfresco.util.Ajax.jsonGet(
            {
                url: Alfresco.constants.PROXY_URI + "lecm/incoming/getRecipientsUnitsWithoutBoss",
                dataObj: {
                    documentNodeRef: "${form.arguments.documentNodeRef!""}"
                },
                successCallback: {
                    fn: function (response) {
                        if (response.json && response.json.length) {
                            Dom.removeClass("${fieldHtmlId}-parent", "hidden1");

                            var result = "";
                            if (response.json.length == 1) {
                                result = Alfresco.component.Base.prototype.msg("message.primaryRouting.recipientUnitWithoutBoss", response.json[0].name);
                            } else {
                                var units = "", i;
                                for (i = 0; i < response.json.length; i++) {
                                    if (units.length) {
                                        units += ", ";
                                    }
                                    units += response.json[i].name;
                                }
                                result = Alfresco.component.Base.prototype.msg("message.primaryRouting.recipientsUnitsWithoutBoss", units);
                            }

                            YAHOO.util.Dom.get("${fieldHtmlId}-value").innerHTML = result;
                        }
                    },
                    scope: this
                }
            });
});
//]]>
</script>

<div id="${fieldHtmlId}-parent" class="control recipients-without-boss-message-control viewmode hidden1">
    <span id="${fieldHtmlId}-value"></span>
</div>
<div class="clear"></div>