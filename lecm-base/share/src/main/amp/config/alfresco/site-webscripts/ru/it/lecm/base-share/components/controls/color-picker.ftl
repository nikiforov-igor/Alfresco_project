
<#if form.mode == "view">
	<div class="control color-picker viewmode">
		<div class="label-div">
			<#if field.mandatory && !(field.value?is_number) && field.value == "">
			<span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}"/><span>
			</#if>
			<label>${field.label?html}:</label>
		</div>
		<#assign fieldValue=field.value?html>
		<div class="container">
			<div class="value-div">
				<#if fieldValue == "">
					${msg("form.control.novalue")}
				<#else>
					${fieldValue} <div class="color-block" style="background-color: ${fieldValue};">&nbsp</div>
				</#if>
			</div>
		</div>
	</div>
<#else>
	<#assign fieldStyleClass = fieldHtmlId + "-class">
	<div class="control color-picker editmode">
		<div class="label-div">
			<label for="${fieldHtmlId}">
				${field.label?html}:
				<#if field.mandatory>
					<span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
				</#if>
			</label>
		</div>
		<div class="container">
            <div class="buttons-div">
                <@formLib.renderFieldHelp field=field />
            </div>
            <div class="value-div">
				<input id="${fieldHtmlId}" name="${field.name}" tabindex="0" type="text" class="${fieldStyleClass} <#if field.control.params.styleClass??>${field.control.params.styleClass}</#if>"
				       <#if field.control.params.style??>style="${field.control.params.style}"</#if>
				       <#if field.value?is_number>value="${field.value?c}"<#else>value="${field.value?html}"</#if>
				       <#if field.description??>title="${field.description}"</#if>
				       <#if field.control.params.maxLength??>maxlength="${field.control.params.maxLength}"</#if>
				       <#if field.control.params.size??>size="${field.control.params.size}"</#if>
				       <#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if> />
			</div>
		</div>
	</div>

	<script type="text/javascript">
		(function(){
			LogicECM.CurrentModules = LogicECM.CurrentModules || {};

			function init() {
				LogicECM.module.Base.Util.loadScripts([
					'scripts/lecm-base/components/lecm-color-picker.js',
					'yui/event-simulate/event-simulate.js'
				], initColorPicker);
			}

			function initColorPicker() {
				LogicECM.CurrentModules['${fieldHtmlId}'] = new LogicECM.module.ColorPicker('${fieldHtmlId}');
				LogicECM.CurrentModules['${fieldHtmlId}'].setOptions({
					bindClass: '${fieldStyleClass}'
				});
				LogicECM.CurrentModules['${fieldHtmlId}'].init();
			}

			YAHOO.util.Event.onAvailable('${fieldHtmlId}', init, true);
		})();
	</script>
</#if>
<div class="clear"></div>