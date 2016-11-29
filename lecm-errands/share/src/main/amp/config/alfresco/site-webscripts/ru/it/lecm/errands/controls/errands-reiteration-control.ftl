<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign formId=args.htmlid?js_string + "-form">
<#assign params = field.control.params>
<#assign defaultValue = "">

<#if form.arguments[field.name]?has_content>
    <#assign defaultValue = form.arguments[field.name]>
</#if>

<#assign value = field.value>
<#if value == "" && defaultValue != "">
    <#assign value = defaultValue>
</#if>

<script type="text/javascript">//<![CDATA[
(function() {
    function init() {
        LogicECM.module.Base.Util.loadResources([
                    'scripts/lecm-base/components/lecm-reiteration-control-ext.js',
                    'scripts/lecm-errands/controls/errands-reiteration-control-ext.js'
                ],
                [
                    'css/lecm-base/components/reiteration-control-ext.css',
                    'css/lecm-errands/errands-reiteration-control-ext.css'
                ],createControl);
    }
    function createControl(){
        var reiteration = new LogicECM.module.Errands.ReiterationExt("${fieldHtmlId}");
        reiteration.setOptions({
            <#if field.control.params.defaultType??>
                defaultType: "${field.control.params.defaultType}".toUpperCase(),
            </#if>
            <#if field.control.params.defaultDays??>
                defaultDays: "${field.control.params.defaultDays}".split(","),
            </#if>
            fieldId: "${field.configName}",
            formId: "${args.htmlid}"
        });
        reiteration.setMessages(
        ${messages}
        );
    }
    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>


<div id="${fieldHtmlId}-parent" class="control reiteration editmode">
    <div class="label-div">
        <label for="${fieldHtmlId}-displayValue">
        ${field.label?html}:
        <#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if>
        </label>
    </div>
<#if disabled >
    <div class="container">
        <div class="value-div">
            <span id="${fieldHtmlId}-displayValue" class="mandatory-highlightable"></span>
        </div>
    </div>
<#else>
    <div class="container">
        <div class="value-div">
            <span class="mandatory-highlightable"><a id="${fieldHtmlId}-displayValue" href="javascript:void(0)"></a></span>
        </div>
    </div>
</#if>
    <input id="${fieldHtmlId}" type="hidden" name="${field.name}" value="${value?html}" <#if disabled >disabled="true"</#if>/>
</div>
<div class="clear"></div>
<select id="${fieldHtmlId}-type" name="${field.name}-type" tabindex="0" style="display:none">
    <option selected name="DAILY" value="DAILY">${msg("label.reiteration-control.options.daily")}</option>
    <option name="WEEKLY" value="WEEKLY">${msg("label.reiteration-control.options.weekly")}</option>
    <option name="MONTHLY" value="MONTHLY">${msg("label.reiteration-control.options.monthly")}</option>
    <option name="QUARTERLY" value="QUARTERLY">${msg("label.reiteration-control.options.quarterly")}</option>
    <option name="ANNUALLY" value="ANNUALLY">${msg("label.reiteration-control.options.annually")}</option>
</select>