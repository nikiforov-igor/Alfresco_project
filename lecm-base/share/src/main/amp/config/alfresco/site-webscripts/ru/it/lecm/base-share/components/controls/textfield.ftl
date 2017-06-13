<#assign controlId = fieldHtmlId + "-cntrl">
<#assign hideValue = false>
<#if field.control.params.hideValue??>
    <#assign hideValue = true>
</#if>

<#assign nameSuffix = "">
<#if field.control.params.nameSuffix??>
    <#assign nameSuffix = field.control.params.nameSuffix>
</#if>

<#assign mandatory = false>
<#if field.control.params.mandatory??>
    <#if field.control.params.mandatory == "true">
        <#assign mandatory = true>
    </#if>
<#elseif field.mandatory??>
    <#assign mandatory = field.mandatory>
</#if>

<#assign readonly = false>
<#assign defaultValue=field.value>
<#if form.mode == "create">
    <#if form.arguments[field.name]?has_content>
        <#assign defaultValue=form.arguments[field.name]>
    <#elseif form.arguments['readonly_' + field.name]?has_content>
        <#assign defaultValue=form.arguments['readonly_' + field.name]>
        <#assign readonly = true>
    <#elseif field.control.params.defaultValue??>
        <#assign defaultValue=field.control.params.defaultValue>
    </#if>
</#if>

<#assign disabled = field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>
<#assign styleClass = ""/>
<#if field.control.params.styleClass??>
	<#assign styleClass = field.control.params.styleClass/>
</#if>
<#if disabled>
    <#if (styleClass?length > 0)>
	    <#assign styleClass = styleClass + " "/>
    </#if>
	<#assign styleClass = styleClass + "initially-disabled"/>
</#if>

<#if form.mode == "view">
    <div id="${controlId}" class="control textfield viewmode">
        <div class="label-div">
            <#if mandatory && !(field.value?is_number) && field.value == "">
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
    <div id="${controlId}" class="control textfield editmode <#if field.control.params.containerStyleClass??>${field.control.params.containerStyleClass}</#if>">
        <div class="label-div">
            <label for="${fieldHtmlId}">${field.label?html}:
                <#if mandatory>
                    <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
                </#if>
            </label>
        </div>
        <div class="container">
            <div class="buttons-div"><@formLib.renderFieldHelp field=field /></div>
            <div class="value-div">
                <input id="${fieldHtmlId}" name="${field.name}${nameSuffix}" tabindex="0"
                       <#if field.control.params.password??>type="password"<#else>type="text"</#if>
                       <#if (styleClass?length > 0)>class="${styleClass}"</#if>
                       <#if field.control.params.style??>style="${field.control.params.style}"</#if>
                       <#if !hideValue>
                          <#if defaultValue?is_number>value="${defaultValue?c}"<#else>value="${defaultValue?html}"</#if>
                       </#if>
                       <#if field.description??>title="${field.description}"</#if>
                       <#if field.control.params.maxLength??>maxlength="${field.control.params.maxLength}"</#if>
                       <#if field.control.params.size??>size="${field.control.params.size}"</#if>
                       <#if disabled>disabled="true"</#if> />
            </div>
        </div>
    </div>
<script type="text/javascript">//<![CDATA[
(function () {

    function init() {
        LogicECM.module.Base.Util.loadScripts([
                    'scripts/lecm-base/components/lecm-textfield.js'
                ],
                createControl);
    }

    function createControl(){
        var nodeRef = ("${form.arguments.itemKind}" == "node") ? "${form.arguments.itemId}" : null;
        var typeName = ("${form.arguments.itemKind}" == "node") ? null : "${form.arguments.itemId}";

        new LogicECM.module.TextField("${fieldHtmlId}").setOptions({
            objectNodeRef: nodeRef,
            typeName: typeName,
            fieldId: "${field.configName}",
            formId: "${args.htmlid}",
            disabled: ${disabled?string},
            <#if defaultValue?has_content>
                currentValue: "${defaultValue?js_string}",
            </#if>
            isUniqueValue: ${field.control.params.isUniqueValue!false?string},
            checkInArchive: ${field.control.params.checkInArchive!false?string},
            validationMessageId: "${field.control.params.validationMessageId!'LogicECM.constraints.isUnique.message'}",
            <#if field.control.params.validationFn??>
                validationFn: ${field.control.params.validationFn},
            </#if>
            validationType: "${field.control.params.validationType!'keyup'}"
        }).setMessages(${messages});
		<#if readonly>
			LogicECM.module.Base.Util.readonlyControl('${args.htmlid}', '${field.configName}', true);
		</#if>
    }

    YAHOO.util.Event.onContentReady("${fieldHtmlId}", init);
})();
//]]></script>
</#if>
<div class="clear"></div>
