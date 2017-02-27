<#assign hideValue = false>
<#if field.control.params.hideValue??>
    <#assign hideValue = true>
</#if>

<#assign mandatory = false>
<#if field.control.params.mandatory??>
    <#if field.control.params.mandatory == "true">
        <#assign mandatory = true>
    </#if>
<#elseif field.mandatory??>
    <#assign mandatory = field.mandatory>
</#if>

<#assign disabled=field.disabled>
<#if field.control.params.forceEditable?? && field.control.params.forceEditable == "true">
    <#assign disabled=false>
</#if>

<#assign hideDateFormat=true>
<#if field.control.params.hideDateFormat??>
    <#assign hideDateFormat=field.control.params.hideDateFormat == "true">
</#if>

<#assign viewFormat>${msg("form.control.date-picker.view.date.format")}</#assign>
<#assign defaultDate>
	<#if (field.control.params.defaultFrom?? || field.control.params.defaultTo??)>
		${field.control.params.defaultFrom!""}|${field.control.params.defaultTo!""}
	</#if>
</#assign>
<#if form.mode == "create" && defaultDate?string == "">
	<#if form.arguments[field.name + "-date-range"]?has_content>
		<#assign defaultDate=form.arguments[field.name + "-date-range"]>
	</#if>
</#if>

<#assign controlId = fieldHtmlId + "-cntrl">

<script type="text/javascript">//<![CDATA[
(function () {
    function init() {
        LogicECM.module.Base.Util.loadScripts([
            'scripts/lecm-base/components/lecm-daterange-control.js'
        ], createDateRangeControl);
    }

    function createDateRangeControl() {
        var dsf = new LogicECM.DateRangeControl("${controlId}", "${fieldHtmlId}").setOptions(
                {
                fillDates:<#if field.control.params.fillDates??>${field.control.params.fillDates?string}<#else>false</#if>,
                <#if field.control.params.toDateDefault??>
                    toDateDefault: "${field.control.params.toDateDefault?string}",
                </#if>
                <#if field.control.params.fromDateDefault??>
                    fromDateDefault: "${field.control.params.fromDateDefault?string}"
                </#if>
                }
        ).setMessages(${messages});
    }
    YAHOO.util.Event.onDOMReady(init);

})();
//]]></script>

<div class="control daterange-control date viewmode">
    <div class="label-div">
        <label for="${fieldHtmlId}">
        ${field.label?html}:
            <#if mandatory>
                <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
            </#if>
        </label>
    </div>
    <div id="${controlId}" class="container two-columns">
        <input id="${fieldHtmlId}" type="hidden" name="${field.name}-date-range" value="${defaultDate}"/>
        <div class="column">
            <div class="label-div">
                <label>${msg("form.control.date-range.from")}:</label>
            </div>
            <div class="container">
                <div class="buttons-div">
                    <a id="${controlId}-icon-from" class="calendar-icon"></a>
                </div>
                <div id="${controlId}-from" class="datepicker"></div>
                <div class="value-div">
                    <input id="${controlId}-date-from" name="-" type="text" class="mandatory-highlightable"
                           <#if field.description??>title="${field.description}"</#if>
                           <#if disabled>disabled="true" <#else>tabindex="0"</#if>/>
                    <div>
                        <span class="date-format <#if hideDateFormat>hidden1</#if>">${msg("lecm.form.control.date-picker.display.date.format")}</span>
                    </div>
                </div>
            </div>
        </div>
        <div class="column last">
            <div class="label-div">
                <div>${msg("form.control.date-range.to")}:</div>
            </div>
            <div class="container">
                <div class="buttons-div">
                    <a id="${controlId}-icon-to" class="calendar-icon"></a>
                    <@formLib.renderFieldHelp field=field />
                </div>
                <div id="${controlId}-to" class="datepicker"></div>
                <div class="value-div">
                    <input id="${controlId}-date-to" name="-" type="text" class="mandatory-highlightable"
                           <#if field.description??>title="${field.description}"</#if><#if disabled>disabled="true"
                           <#else>tabindex="0"</#if>/>
                    <div>
                        <span class="date-format <#if hideDateFormat>hidden1</#if>">${msg("lecm.form.control.date-picker.display.date.format")}</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="clear"></div>