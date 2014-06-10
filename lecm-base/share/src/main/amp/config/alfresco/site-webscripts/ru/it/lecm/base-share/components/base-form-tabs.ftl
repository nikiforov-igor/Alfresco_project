<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />

<#if formUI == "true">
    <@formLib.renderFormsRuntime formId=formId />
</#if>

<@formLib.renderFormContainer formId=formId>
<div id="${formId}-tabs" class="yui-navset form-tabs">
    <ul class="yui-nav">
        <#list form.structure as item>
            <#if item.kind == "set">
                <li <#if (item_index == 0 && !args.setId??) || (args.setId?? && item.id == args.setId)>class="selected"</#if>>
                    <a href="#tab${item_index + 1}">
                        <em>${item.label}</em>
                    </a>
                </li>
            </#if>
        </#list>
    </ul>
    <div class="yui-content">
        <#list form.structure as item>
            <#if item.kind == "set">
                <div class="tab-${item.id!""}"><@formLib.renderSet set=item /></div>
            </#if>
        </#list>
    </div>
</div>
</@>

<script type="text/javascript">//<![CDATA[
(function() {
	function init() {
		LogicECM.module.Base.Util.loadScripts([
			'scripts/lecm-base/components/lecm-form-tabs.js'
		], createControl, ["tabview"]);
	}

    function createControl() {
	    new LogicECM.BaseFormTabs("${formId}-tabs").setOptions({
		    formId: "${formId}"
	    }).setMessages(${messages});
    }

	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>