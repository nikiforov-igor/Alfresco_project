<#assign controlId = fieldHtmlId + "-cntrl">
<#assign hideValue = false>
<#if field.control.params.hideValue??>
	<#assign hideValue = true>
</#if>

<#assign mandatory = false>
<#if field.control.params.mandatory??>
	<#if field.control.params.mandatory == "true">
		<#assign mandatory = true>
	</#if>
<#elseif field.mandatory??>
	<#assign mandatory = field.mandatory>
</#if>

<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<script type="text/javascript">
	(function() {
		function createControl() {
		var control = new LogicECM.module.TextFieldWithReset("${fieldHtmlId}").setMessages(${messages});
		control.setOptions(
				{
                <#if field.control.params.defaultValue??>
                    defaultValue: "${field.control.params.defaultValue?string}",
                </#if>
                <#if field.control.params.defaultValueDataSource??>
                    defaultValueDataSource: "${field.control.params.defaultValueDataSource}",
                </#if>
                    parentId:"${form.arguments.itemId!""}",
                    fieldId: "${field.configName}",
					disabled: ${field.disabled?string},
                    formId: "${args.htmlid}"
				});
		}
		YAHOO.util.Event.onDOMReady(function() {
            LogicECM.module.Base.Util.loadResources(
                    ['scripts/lecm-base/components/lecm-textfield-with-reset.js'],
                    ['css/lecm-base/components/controls/textfield-with-reset.css'], createControl);
		});
	})();
</script>

<#if disabled>
<div class="control textfield textfield-reset viewmode">
    <div class="label-div">
		<#if mandatory && !(field.value?is_number) && field.value == "">
        <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}"/><span>
		</#if>
        <label>${field.label?html}:</label>
    </div>
    <div class="container">
        <div class="value-div">
            <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
            <span id="${controlId}-Dom.get(this.currentValueHtmlId).value" class="mandatory-highlightable">${field.value?html}</span>
        </div>
    </div>
</div>
<#else>
<div class="control textfield textfield-reset editmode">
    <div class="label-div">
        <label for="${fieldHtmlId}">${field.label?html}:
			<#if mandatory>
                <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
			</#if>
        </label>
    </div>
    <div class="container">
        <div class="buttons-div">
            <input type="button" id="${controlId}-reset-button" name="${field.name}-reset-button" value="По умолчанию" title="Вернуть значение по-умолчанию"/>
        </div>
        <div class="value-div">
            <input id="${fieldHtmlId}" type="text" class="autocomplete-input" name="${field.name}" value="${field.value?html}"/>
        </div>
    </div>
</div>
</#if>
<div class="clear"></div>
