<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#if form.mode == "edit">
    <#assign containerId = fieldHtmlId + "-container">
    <#assign hiddenFieldId = fieldHtmlId>
    <#assign hiddenFieldName = field.name>
    <#assign formId=args.htmlid?js_string>

    <#if field.control.params.optionSeparator??>
        <#assign optionSeparator=field.control.params.optionSeparator>
    </#if>
    <#if field.control.params.fieldSeparator??>
        <#assign fieldSeparator=field.control.params.fieldSeparator>
    <#else>
        <#assign fieldSeparator="|">
    </#if>

    <#if field.control.params.selectedValue?? && field.control.params.selectedValue != "">
        <#assign selectedValue=field.control.params.selectedValue?string>
    </#if>

    <script type='text/javascript'>//<![CDATA[
    (function () {
        function init() {
            LogicECM.module.Base.Util.loadResources([
                'scripts/lecm-errands/controls/errands-cancellation-activiti-radiobuttons.js'
            ], [
                'css/lecm-errands/errands-cancellation-activiti-radiobuttons.css'
            ], createControls);
        }

        function createControls() {
            new LogicECM.module.Errands.ErrandsCancellationActivitiTransitionRadiobuttons('${containerId}').setOptions({
                currentValue: '${field.control.params.options?js_string}',
                hiddenFieldName: '${hiddenFieldName}',
                hiddenFieldId: '${hiddenFieldId}',
                fieldSeparator: '${fieldSeparator}',
                formId: '${formId}'
                <#if field.control.params.fieldsByOption?? && field.control.params.fieldsByOption != "">,
                    fieldsByOption: "${field.control.params.fieldsByOption}".split("${optionSeparator}")
                </#if>
                <#if selectedValue??>,
                    selectedValue: '${selectedValue}'
                </#if>
                <#if field.control.params.changeValueFireAction??>,
                    changeValueFireAction: "${field.control.params.changeValueFireAction}",
                </#if>
            }).setMessages(${messages});
        }

        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]></script>

    <div class='control editmode cancellation-activiti-radiobuttons'>
        <div class="label-div">
            <label for="${hiddenFieldId}">
            ${field.label?html}:
            </label>
        </div>
        <div id='${containerId}' class="container">
            <div class="value-div">
                <input id='${hiddenFieldId}' type='hidden' name='${hiddenFieldName}'>
            </div>
        </div>
    </div>
</#if>
