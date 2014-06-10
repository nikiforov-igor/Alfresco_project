<#include "/org/alfresco/components/form/controls/common/editorparams.inc.ftl" />

<#if field.control.params.rows??><#assign rows=field.control.params.rows><#else><#assign rows=2></#if>
<#if field.control.params.columns??><#assign columns=field.control.params.columns><#else><#assign columns=60></#if>

<#if form.mode == "view" || field.disabled>
    <div class="control richtext viewmode">
        <div class="label-div">
            <#if field.mandatory && field.value == "">
            <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}"/><span>
            </#if>
            <label>${field.label?html}:</label>
        </div>
        <div class="container">
            <div class="value-div">
                <#if field.value == "">${msg("form.control.novalue")}<#else>${field.value}</#if>
            </div>
        </div>
    </div>
<#else>
    <script type="text/javascript">//<![CDATA[
    (function() {
    	function init() {
            LogicECM.module.Base.Util.loadScripts([
                'scripts/lecm-base/components/lecm-rich-text.js'
			], createRichText);
		}
		function createRichText() {
	        new LogicECM.RichTextControl("${fieldHtmlId}").setOptions(
	            {
	                <#if form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>disabled: true,</#if>
	                currentValue: "${field.value?js_string}",
	                mandatory: ${field.mandatory?string},
	                <@editorParameters field />
	            }).setMessages(${messages});
		}
		YAHOO.util.Event.onDOMReady(init);
    })();
    //]]></script>

    <div class="control richtext editmode">
        <div class="label-div">
            <label for="${fieldHtmlId}">
                ${field.label?html}:
                <#if field.mandatory>
                    <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
                </#if>
            </label>
        </div>
        <div class="container">
            <div class="value-div">
                <textarea id="${fieldHtmlId}" name="${field.name}" rows="${rows}" columns="${columns}" tabindex="0"
                          <#if field.description??>title="${field.description}"</#if>
                          <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
                          <#if field.control.params.style??>style="${field.control.params.style}"</#if>
                          <#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>>${field.value?html}</textarea>
            </div>
        </div>
    </div>
</#if>
<div class="clear"></div>
