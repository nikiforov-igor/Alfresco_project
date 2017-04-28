<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<#if form.mode == "view">
    <div id="${fieldHtmlId}-parent" class="control selectone viewmode">
        <div class="label-div">
			<#if field.mandatory && !(field.value?is_number) && field.value == "">
            <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png"
                                                  title="${msg("form.field.incomplete")}"/><span>
			</#if>
            <label>${field.label?html}:</label>
        </div>
		<#if fieldValue?string == "">
			<#assign valueToShow=msg("form.control.novalue")>
		<#else>
			<#assign valueToShow=fieldValue>
			<#if field.control.params.options?? && field.control.params.options != "">
				<#list field.control.params.options?split(optionSeparator) as nameValue>
					<#if nameValue?index_of(labelSeparator) == -1>
						<#if nameValue == fieldValue?string || (fieldValue?is_number && fieldValue?c == nameValue)>
							<#assign valueToShow=nameValue>
							<#break>
						</#if>
					<#else>
						<#assign choice=nameValue?split(labelSeparator)>
						<#if choice[0] == fieldValue?string || (fieldValue?is_number && fieldValue?c == choice[0])>
							<#assign valueToShow=msgValue(choice[1])>
							<#break>
						</#if>
					</#if>
				</#list>
			</#if>
		</#if>
        <div class="container">
            <div class="value-div">
			${valueToShow?html}
            </div>
        </div>
    </div>
<#else>
    <div id="${fieldHtmlId}-parent" class="control selectone editmode">
        <div class="label-div">
            <label for="${fieldHtmlId}">${field.label?html}:
				<#if field.mandatory>
                    <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
				</#if>
            </label>
        </div>
        <div class="container">
            <div class="buttons-div"><@formLib.renderFieldHelp field=field /></div>
            <div class="value-div">
                <input id="${fieldHtmlId}" type="hidden" name="${field.name}" <#if disabled>disabled="true"</#if>/>
                <select id="${fieldHtmlId}-select" <#if disabled>disabled="true"</#if>>
                    <option>${msg("label.control.default")}</option>
                </select>
            </div>
            <table class="formFieldControlParamsTable"><tbody id="${fieldHtmlId}-params"></tbody></table>
            <div id="${fieldHtmlId}-hidden-params"></div>
        </div>
    </div>
</#if>
<div class="clear"></div>

<script type="text/javascript">//<![CDATA[
(function () {
    LogicECM.module.Base.Util.loadScripts([
        'scripts/lecm-forms-editor/lecm-form-field-component.js'
    ], createControl);

    function createControl() {
        var control = new LogicECM.module.FormsEditor.FieldComponent("${fieldHtmlId}").setMessages(${messages});
        control.setOptions({
		<#if disabled>
            disabled: true,
		</#if>
        defaultOption: "${msg("label.control.default")}",
		<#if field.mandatory??>
            mandatory: ${field.mandatory?string},
		<#elseif field.endpointMandatory??>
            mandatory: ${field.endpointMandatory?string},
		</#if>
			fromForm: ${(field.control.params.fromForm!"true")?string},
            updateOnAction: "${field.control.params.updateOnAction!""}",
            itemNodeRef: "${form.arguments.itemId!''}",
		<#if (field.value?? && field.value?length > 0)>
            value: ${field.value}
		</#if>
        });
    }
})();
//]]></script>
