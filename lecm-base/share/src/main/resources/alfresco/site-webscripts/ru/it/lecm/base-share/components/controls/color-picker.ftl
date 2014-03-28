
<div class="form-field">
	<#if form.mode == "view">
		<div class="viewmode-field">
			<#if field.mandatory && !(field.value?is_number) && field.value == "">
				<span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
			</#if>
			<span class="viewmode-label">${field.label?html}:</span>
			<#assign fieldValue=field.value?html>


			<span class="viewmode-value">
				<#if fieldValue == "">
					${msg("form.control.novalue")}
				<#else>
					${fieldValue} <div style="background-color: ${fieldValue}; display: inline; padding: 0px 10px; margin-left: 3px;">&nbsp</div>
				</#if>
			</span>
		</div>
	<#else>
		<#assign fieldStyleClass = fieldHtmlId + "-class">
		<label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
		<input id="${fieldHtmlId}" name="${field.name}" tabindex="0" type="text" class="${fieldStyleClass} <#if field.control.params.styleClass??>${field.control.params.styleClass}</#if>"
				<#if field.control.params.style??>style="${field.control.params.style}"</#if>
				<#if field.value?is_number>value="${field.value?c}"<#else>value="${field.value?html}"</#if>
				<#if field.description??>title="${field.description}"</#if>
				<#if field.control.params.maxLength??>maxlength="${field.control.params.maxLength}"</#if>
				<#if field.control.params.size??>size="${field.control.params.size}"</#if>
				<#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if> />
		<@formLib.renderFieldHelp field=field />

		<script type="text/javascript">
		(function(){
			LogicECM.CurrentModules = LogicECM.CurrentModules || {};

			var loader = new YAHOO.util.YUILoader({
				require: [
					'lecmColorPicker'
				],
				skin: {}
			});

			loader.addModule({
				name: 'lecmColorPicker',
				type: 'js',
				fullpath: Alfresco.constants.URL_RESCONTEXT + 'scripts/lecm-base/components/lecm-color-picker.js'
			});


			loader.onSuccess = function() {
				LogicECM.CurrentModules['${fieldHtmlId}'] = new LogicECM.module.ColorPicker('${fieldHtmlId}');
				LogicECM.CurrentModules['${fieldHtmlId}'].setOptions({
					bindClass: '${fieldStyleClass}'
				});
				LogicECM.CurrentModules['${fieldHtmlId}'].init();
			};

			YAHOO.util.Event.onAvailable('${fieldHtmlId}', loader.insert, loader, true);
		})();
		</script>
	</#if>
</div>
