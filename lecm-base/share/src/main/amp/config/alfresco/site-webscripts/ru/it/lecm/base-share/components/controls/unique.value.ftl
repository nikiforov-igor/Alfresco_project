<#if form.mode == "view">
	<div class="control unique-value viewmode">
		<div class="label-div">
			<#if field.mandatory && !(field.value?is_number) && field.value == "">
			<span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png"
			                                      title="${msg("form.field.incomplete")}"/><span>
			</#if>
			<label>${field.label?html}:</label>
		</div>
		<div class="container">
			<div class="value-div">
				<#if field.control.params.activateLinks?? && field.control.params.activateLinks == "true">
					<#assign fieldValue=field.value?html?replace("((http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?\\^=%&:\\/~\\+#]*[\\w\\-\\@?\\^=%&\\/~\\+#])?)", "<a href=\"$1\" target=\"_blank\">$1</a>", "r")>
				<#else>
					<#if field.value?is_number>
						<#assign fieldValue=field.value?c>
					<#else>
						<#assign fieldValue=field.value?html>
					</#if>
				</#if>
				<span><#if fieldValue == "">${msg("form.control.novalue")}<#else>${fieldValue}</#if></span>
			</div>
		</div>
	</div>
<#else>
	<#if field.value?is_number>
		<#assign fieldValue=field.value?c>
	<#else>
		<#assign fieldValue=field.value?html>
	</#if>
	<div class="control unique-value editmode">
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
				<input id="${fieldHtmlId}" name="${field.name}" tabindex="0"
				       <#if field.control.params.password??>type="password"<#else>type="text"</#if>
				       <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
				       <#if field.control.params.style??>style="${field.control.params.style}"</#if>
				       <#if field.value?is_number>value="${field.value?c}"<#else>value="${field.value?html}"</#if>
				       <#if field.description??>title="${field.description}"</#if>
				       <#if field.control.params.maxLength??>maxlength="${field.control.params.maxLength}"</#if>
				       <#if field.control.params.size??>size="${field.control.params.size}"</#if>
				       <#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if> />
			</div>
		</div>
	</div>

	<script type="text/javascript">//<![CDATA[
		(function () {
			var nodeRef = ("${form.arguments.itemKind}" == "node") ? "${form.arguments.itemId}" : "";
			var typeName = ("${form.arguments.itemKind}" == "node") ? "" : "${form.arguments.itemId}";

			new LogicECM.validation.Uniqueness("${fieldHtmlId}").setOptions({
				nodeRef: nodeRef,
				typeName: typeName,
				propertyName: "${field.configName}",
				storedValue: "${fieldValue}"
			}).setMessages(${messages});
		})();
	//]]></script>
</#if>
<div class="clear"></div>
