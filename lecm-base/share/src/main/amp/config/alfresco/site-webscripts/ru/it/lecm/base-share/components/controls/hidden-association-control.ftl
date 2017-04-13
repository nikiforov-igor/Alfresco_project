<#assign fieldValue = field.value>
<#assign defaultValue=""/>
<#if field.control.params.contextProperty??>
	<#if context.properties[field.control.params.contextProperty]??>
		<#assign defaultValue = context.properties[field.control.params.contextProperty]>
	<#elseif args[field.control.params.contextProperty]??>
		<#assign defaultValue = args[field.control.params.contextProperty]>
	</#if>
<#elseif context.properties[field.name]??>
	<#assign defaultValue = context.properties[field.name]>
</#if>

<#if form.arguments[field.name]?has_content>
	<#assign defaultValue = form.arguments[field.name]>
</#if>

<#assign isValueSetFireEvent = true/>
<#if field.control.params.isValueSetFireEvent?? && field.control.params.isValueSetFireEvent == "false">
	<#assign isValueSetFireEvent = false/>
</#if>
<#if field.control.params.valueSetFireAction??>
	<#assign valueSetFireAction = field.control.params.valueSetFireAction>
<#else>
	<#assign valueSetFireAction = "afterSetItems">
</#if>
<script type="text/javascript">
	(function()
	{
		function init(){
            LogicECM.module.Base.Util.loadScripts([
                'scripts/lecm-base/components/controls/hidden-association-control.js'
            ], createControl);
		}
		function createControl(){
			new LogicECM.module.HiddenAssociationControl("${fieldHtmlId}").setOptions({
			<#if field.control.params.addedXpath??>
                addedXpath: "${field.control.params.addedXpath}",
			</#if>
			<#if defaultValue != "">
                defaultValue: "${defaultValue?string}",
			</#if>
                isValueSetFireEvent: ${isValueSetFireEvent?string},
                valueSetFireAction: "${valueSetFireAction}",
                fieldId: "${field.configName}",
                formId: "${args.htmlid}"
            }).setMessages( ${messages} );
		}
        YAHOO.util.Event.onContentReady("${fieldHtmlId}", init);
	})();
</script>

<div class="control hidden-assoc">
	<input type="hidden" id="${fieldHtmlId}-removed" name="${field.name}_removed"/>
	<input type="hidden" id="${fieldHtmlId}-added" name="${field.name}_added"/>
	<input type="hidden" id="${fieldHtmlId}" name="${field.name}"
	       <#if field.value?is_number>value="${fieldValue?c}"<#else>value="${fieldValue?html}"</#if> />
</div>