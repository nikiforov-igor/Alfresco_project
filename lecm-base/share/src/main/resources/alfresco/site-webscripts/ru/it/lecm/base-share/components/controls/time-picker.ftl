<#assign formId = args.htmlid?js_string + "-form">
<#assign containerId = formId + "-container_c">

<div class="form-field">
	<#if form.mode == "view">
		<div class="viewmode-field">
			<#if field.mandatory && !(field.value?is_number) && field.value == "">
				<span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
			</#if>
			<span class="viewmode-label">${field.label?html}:</span>
			<#if field.control.params.activateLinks?? && field.control.params.activateLinks == "true">
				<#assign fieldValue=field.value?html?replace("((http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?\\^=%&:\\/~\\+#]*[\\w\\-\\@?\\^=%&\\/~\\+#])?)", "<a href=\"$1\" target=\"_blank\">$1</a>", "r")>
			<#else>
				<#if field.value?is_number>
					<#assign fieldValue=field.value?c>
				<#else>
					<#assign fieldValue=field.value?html>
				</#if>
			</#if>
			<span class="viewmode-value"><#if fieldValue == "">${msg("form.control.novalue")}<#else>${fieldValue}</#if></span>
		</div>
	<#else>
		<label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
		<input id="${fieldHtmlId}" name="${field.name}" tabindex="0"
			<#if field.control.params.password??>type="password"<#else>type="text"</#if>
			<#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
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
					'jQueryUI',
					'jQueryUISliderAccess',
					'jQueryUITimepickerAddon'
				],
				skin: {}
			});

			loader.addModule({
				name: 'jQueryUI',
				type: 'js',
				fullpath: Alfresco.constants.URL_RESCONTEXT + 'scripts/lecm-base/third-party/jquery-ui-1.10.3.custom.js'
			});

			loader.addModule({
				name: 'jQueryUISliderAccess',
				type: 'js',
				fullpath: Alfresco.constants.URL_RESCONTEXT + 'scripts/lecm-base/third-party/jquery-ui-sliderAccess.js'
			});

			loader.addModule({
				name: 'jQueryUITimepickerAddon',
				type: 'js',
				fullpath: Alfresco.constants.URL_RESCONTEXT + 'scripts/lecm-base/third-party/jquery-ui-timepicker-addon.js'
			});

			loader.onSuccess = function() {
				var zIndex = $('#${containerId}').zIndex(),
					fieldNode = $('#${fieldHtmlId}');
				fieldNode.zIndex(zIndex+1);

				fieldNode.timepicker({
					timeFormat: 'HH:mm',
					timeOnlyTitle: 'Выберите время',
					timeText: 'Время',
					hourText: 'Часы',
					minuteText: 'Минуты',
					secondText: 'Секунды',
					currentText: 'Сейчас',
					closeText: 'Выбрать',
					onSelect: function (selectedDateTime) {
						YAHOO.Bubbling.fire('mandatoryControlValueUpdated', this);
					}
				});
			};

			YAHOO.util.Event.onAvailable('${fieldHtmlId}', loader.insert, loader, true);
		})();
		</script>

	</#if>
</div>
