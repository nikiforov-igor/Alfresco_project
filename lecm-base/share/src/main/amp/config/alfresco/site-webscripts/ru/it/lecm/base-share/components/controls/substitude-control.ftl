<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign params = field.control.params>

<#assign emptyValue = params.emptyValueKey!''>

<script type="text/javascript">//<![CDATA[
YAHOO.util.Event.onContentReady("${fieldHtmlId}-value", function () {
    Alfresco.util.Ajax.jsonRequest(
            {
                url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                method: "POST",
                dataObj: {
                    nodeRef: "${form.arguments.itemId}",
                    substituteString: "${params.formatString!}"
                },
                successCallback: {
                    fn: function (response) {
                        if (response.json != null && response.json.formatString != null) {
                            var result = response.json.formatString;
                            if (result.trim() == "true" || result.trim() == "false") {
                                result = result.trim() == "true" ? "${msg("message.yes")}" : "${msg("message.no")}";
                            }
                            if (!result.trim()) {
                                result = "${msg(emptyValue)}";
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

<div class="control substitude viewmode">
	<div class="label-div">
		<label>${field.label?html}:</label>
	</div>
	<div class="container">
		<div class="value-div">
			<span id="${fieldHtmlId}-value"></span>
		</div>
	</div>
</div>
<div class="clear"></div>