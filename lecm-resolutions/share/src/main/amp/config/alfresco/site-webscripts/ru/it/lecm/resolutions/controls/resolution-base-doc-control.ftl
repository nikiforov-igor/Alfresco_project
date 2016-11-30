<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign params = field.control.params>

<#if params.documentArgName?? && form.arguments[params.documentArgName]?has_content && params.formatString??>
<script type="text/javascript">//<![CDATA[
YAHOO.util.Event.onDOMReady(function () {
    Alfresco.util.Ajax.jsonRequest(
            {
                url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                method: "POST",
                dataObj: {
                    nodeRef: "${form.arguments[params.documentArgName]}",
                    substituteString: "${params.formatString!}"
                },
                successCallback: {
                    fn: function (response) {
                        if (response.json != null && response.json.formatString != null) {
                            YAHOO.util.Dom.get("${fieldHtmlId}-value").innerHTML = response.json.formatString;
                        }
                    },
                    scope: this
                }
            });
});
//]]>
</script>
</#if>

<div class="control resolution-base-doc-control viewmode">
    <span id="${fieldHtmlId}-value"></span>
</div>
<div class="clear"></div>