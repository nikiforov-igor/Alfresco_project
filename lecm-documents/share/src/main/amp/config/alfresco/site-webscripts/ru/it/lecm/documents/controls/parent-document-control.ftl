<#include "/org/alfresco/include/alfresco-macros.lib.ftl" />
<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign nodeRef=field.value!"">

<script type="text/javascript">//<![CDATA[
(function() {
	<#if nodeRef?? && (nodeRef?length > 0)>
		Alfresco.util.Ajax.jsonGet({
			url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/document/api/getProperties",
			dataObj: {
				nodeRef: "${nodeRef}"
			},
			successCallback: {
				fn:function(response){
					YAHOO.util.Dom.get("${fieldHtmlId}-value-link").innerHTML = response.json[0]["present-string"];
				},
				scope: this
			},
			failureMessage: "${msg('message.failure')}"
		});

        Alfresco.util.Ajax.jsonGet({
            url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/document/api/url/view",
            dataObj: {
                nodeRef: "${nodeRef}"
            },
            successCallback: {
                fn:function(response){
                    YAHOO.util.Dom.get("${fieldHtmlId}-value-link").href = response.json.url + "?nodeRef=${nodeRef}";
                },
                scope: this
            },
            failureMessage: "${msg('message.failure')}"
        });

	</#if>
})();
//]]></script>

<div class="form-field">
	<div class="viewmode-field">
		<span class="viewmode-label">${field.label?html}:</span>
		<span id="${fieldHtmlId}-currentValueDisplay" class="viewmode-value">
			<#if nodeRef?? && (nodeRef?length > 0)>
				<a id="${fieldHtmlId}-value-link" href="${siteURL("document?nodeRef=" + nodeRef)}"></a>
			</#if>
		</span>
		<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
	</div>
</div>