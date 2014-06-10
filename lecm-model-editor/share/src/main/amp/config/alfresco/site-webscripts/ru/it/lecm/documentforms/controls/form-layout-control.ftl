<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign fieldValue=field.value>
<#assign controlId = fieldHtmlId + "-cntrl">

<#if fieldValue?string == "" && field.control.params.defaultValueContextProperty??>
	<#if context.properties[field.control.params.defaultValueContextProperty]??>
		<#assign fieldValue = context.properties[field.control.params.defaultValueContextProperty]>
	<#elseif args[field.control.params.defaultValueContextProperty]??>
		<#assign fieldValue = args[field.control.params.defaultValueContextProperty]>
	</#if>
</#if>

<div class="form-field">
	<label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
	<select id="${fieldHtmlId}" name="${field.name}" tabindex="0"
	        <#if field.description??>title="${field.description}"</#if>
	        <#if field.control.params.size??>size="${field.control.params.size}"</#if>
	        <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
	        <#if field.control.params.style??>style="${field.control.params.style}"</#if>
	        <#if field.disabled  && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>>
	<#if field.control.params.withEmpty?? && field.control.params.withEmpty == "true"><option value=""></option></#if>
	</select>

	<@formLib.renderFieldHelp field=field />
	<script type="text/javascript">//<![CDATA[
		(function() {
			function init() {
				Alfresco.util.Ajax.jsonGet({
						url: Alfresco.constants.URL_SERVICECONTEXT + "/lecm/forms/getConfig?action=getFormLayouts",
						successCallback:
						{
							fn: function (response) {
								var oResults = response.json;
								if (oResults != null) {
									var select = Dom.get("${fieldHtmlId}");
									for (var i = 0; i < oResults.length; i++) {
										var option = document.createElement("option");
										option.value = oResults[i].template;
										option.innerHTML = oResults[i].localName;
										if (oResults[i].template == "${fieldValue}") {
											option.selected = true;
										}
										select.appendChild(option);
									}
								}
							},
							scope: this
						}
				});
			}

			YAHOO.util.Event.onDOMReady(init);
		})();
	//]]></script>
</div>