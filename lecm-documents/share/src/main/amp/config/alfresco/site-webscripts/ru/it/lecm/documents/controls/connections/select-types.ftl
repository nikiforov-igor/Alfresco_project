<#assign  formId = args.htmlid?js_string/>
<#assign fieldValue=field.value!"">
<#assign fieldId=field.id!"">

<#if fieldValue?string == "" && field.control.params.defaultValueContextProperty??>
	<#if context.properties[field.control.params.defaultValueContextProperty]??>
		<#assign fieldValue = context.properties[field.control.params.defaultValueContextProperty]>
	<#elseif args[field.control.params.defaultValueContextProperty]??>
		<#assign fieldValue = args[field.control.params.defaultValueContextProperty]>
	</#if>
</#if>

<#if field.control.params.notSelectedOptionShow?? && field.control.params.notSelectedOptionShow == "true">
	<#assign notSelectedOptionShow = true>
<#else>
	<#assign notSelectedOptionShow = false>
</#if>

<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<script type="text/javascript">//<![CDATA[
(function()
{

	function init() {
        LogicECM.module.Base.Util.loadScripts([
            'scripts/documents/connections/type-select.js'
		], createControl);
	}

	function createControl() {

		var typeSelector = new LogicECM.module.Connection.TypeSelect("${fieldHtmlId}").setMessages(${messages});
		typeSelector.setOptions(
		{
			<#if field.mandatory??>
				mandatory: ${field.mandatory?string},
			<#elseif field.endpointMandatory??>
				mandatory: ${field.endpointMandatory?string},
			</#if>
			formId: "${formId}",
			notSelectedOptionShow: ${notSelectedOptionShow?string},
			primaryDocumentInputId: "${formId}_${form.fields["assoc_lecm-connect_primary-document-assoc"].id}",
			connectedDocumentInputId: "${formId}_${form.fields["assoc_lecm-connect_connected-document-assoc"].id}"
		});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<#if disabled>
    <div class="control select-types viewmode">
        <div class="label-div">
            <#if field.mandatory && !(fieldValue?is_number) && fieldValue?string == "">
            <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}"/><span>
            </#if>
            <label>${field.label?html}:</label>
        </div>
        <div class="container">
            <div class="value-div">
                <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
                <span id="${fieldHtmlId}-currentValueDisplay" class="mandatory-highlightable"></span>
            </div>
        </div>
    </div>
<#else>
    <div class="control select-types editmode">
        <div class="label-div">
            <label for="${fieldHtmlId}-added">
                ${field.label?html}:
                <#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if>
            </label>
            <input type="hidden" id="${controlId}-removed" name="${field.name}_removed" value="${fieldValue}"/>
        </div>
        <div class="container">
            <div class="value-div">
                <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
                <select id="${fieldHtmlId}-added" name="${field.name}_added" tabindex="0"
                        <#if field.description??>title="${field.description}"</#if>
                        <#if field.control.params.size??>size="${field.control.params.size}"</#if>
                        <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
                        <#if field.control.params.style??>style="${field.control.params.style}"</#if>
                        <#if field.disabled  && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>>
                    <#if notSelectedOptionShow>
                        <option value="">
                            <#if field.control.params.notSelectedOptionLabel??>
                                ${field.control.params.notSelectedOptionLabel}
                            <#elseif field.control.params.notSelectedOptionLabelCode??>
                                ${msg(field.control.params.notSelectedOptionLabelCode)}
                            </#if>
                        </option>
                    </#if>
                </select>
            </div>
        </div>
    </div>
</#if>
<div class="clear"></div>