<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<script type="text/javascript">//<![CDATA[
(function() {
    function init() {
        LogicECM.module.Base.Util.loadScripts([
            'scripts/components/format-packageItems-document.js'
        ], createControl);
    }
    function createControl() {
        var control = new LogicECM.module.FormatPackageItemsDocument("${fieldHtmlId}").setMessages(${messages});
        control.setOptions(
                {
                    substituteString: "${field.control.params.substituteString!'{cm:name}'}"
                });
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div class="control format-package-items viewmode">
	<div class="label-div">
		<#if field.mandatory && !(field.value?is_number) && field.value == "">
		<span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png"
		                                      title="${msg("form.field.incomplete")}"/><span>
		</#if>
		<label>${field.label?html}:</label>
	</div>
	<div class="container">
		<div class="value-div">
			<span id="${fieldHtmlId}-valueDisplay"></span>
			<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
		</div>
	</div>
</div>
<div class="clear"></div>