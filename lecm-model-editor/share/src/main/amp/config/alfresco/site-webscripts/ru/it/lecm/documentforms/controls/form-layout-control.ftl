<#assign fieldValue=field.value>
<#assign controlId = fieldHtmlId + "-cntrl">

<#if fieldValue?string == "" && field.control.params.defaultValueContextProperty??>
	<#if context.properties[field.control.params.defaultValueContextProperty]??>
		<#assign fieldValue = context.properties[field.control.params.defaultValueContextProperty]>
	<#elseif args[field.control.params.defaultValueContextProperty]??>
		<#assign fieldValue = args[field.control.params.defaultValueContextProperty]>
	</#if>
</#if>

<#if fieldValue?string == "">
	<#assign valueToShow=msg("form.control.novalue")>
<#else>
	<#assign valueToShow=fieldValue>
</#if>

<#if form.mode == "view">
	<div class="control form-layout-control viewmode">
		<div class="label-div">
			<#if field.mandatory && !(field.value?is_number) && field.value == "">
			<span class="incomplete-warning">
				<img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}"/>
			</span>
			</#if>
			<label>${field.label?html}:</label>
		</div>
		<div class="container">
			<div class="value-div">
				${valueToShow?html}
			</div>
		</div>
	</div>
<#else>
	<div class="control form-layout-control editmode">
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
				<select id="${fieldHtmlId}" name="${field.name}"
					<#if field.description??>title="${field.description}"</#if>
					<#if field.control.params.size??>size="${field.control.params.size}"</#if>
					<#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
					<#if field.control.params.style??>style="${field.control.params.style}"</#if>
					<#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>>
					<#if field.control.params.withEmpty?? && field.control.params.withEmpty == "true"><option value=""></option></#if>
				</select>
			</div>
		</div>
	</div>
	</#if>
<div class="clear"></div>
<@inlineScript>
(function() {
	Alfresco.util.Ajax.jsonGet({
		url: Alfresco.constants.URL_SERVICECONTEXT + "/lecm/forms/getConfig?action=getFormLayouts",
		successCallback: {
			scope: this,
			fn: function (response) {
				var oResults = response.json;
				if (oResults) {
					var select = Dom.get("${fieldHtmlId}");
					for (var i in oResults) {
						var option = document.createElement("option");
						option.value = oResults[i].template;
						option.innerHTML = oResults[i].localName;
						if (oResults[i].template == "${fieldValue}") {
							option.selected = true;
						}
						select.appendChild(option);
					}
				}
			}
		}
	});
})();
</@>
