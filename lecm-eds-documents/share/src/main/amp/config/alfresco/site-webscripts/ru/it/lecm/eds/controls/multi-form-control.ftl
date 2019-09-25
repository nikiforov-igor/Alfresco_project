<#assign params = field.control.params>
<#assign disabled = form.mode == "view" || (field.disabled && !(params.forceEditable?? && params.forceEditable == "true"))>
<#assign hideLegend = false/>
<#if params.hideLegend??>
    <#assign hideLegend = params.hideLegend == "true"/>
</#if>

<script type="text/javascript">//<![CDATA[
(function () {
    var runtimeForm;

    function init() {
        LogicECM.module.Base.Util.loadResources([
            'scripts/lecm-eds-documents/multi-form-control.js'
        ], [
            <#if params.additionalCss??>
                <#list params.additionalCss?split(",") as css>
                    '${css}',
                </#list>
            </#if>
            'css/lecm-eds-documents/multi-form-control.css'
        ], createControl);
    }

    function createControl() {
        new LogicECM.module.eds.MultiFormControl("${fieldHtmlId}").setOptions({
            disabled: ${disabled?string},
         <#if field.value?has_content>
             currentValue: ${field.value},
         </#if>
        <#if params.availableRemoveDefault??>
            availableRemoveDefault: ${params.availableRemoveDefault},
        </#if>
        <#if params.documentFromId??>
            documentFromId: "${params.documentFromId}",
        </#if>
        <#if params.defaultValueFromId??>
            defaultValueFromId: "${params.defaultValueFromId}",
        </#if>
        <#if params.documentType??>
            documentType: "${params.documentType}",
        </#if>
        <#if params.defaultValueDataSource??>
            defaultValueDataSource: "${params.defaultValueDataSource}",
        </#if>
        <#if params.fixSimpleDialogId??>
            fixSimpleDialogId: "${params.fixSimpleDialogId}",
        </#if>
        <#if params.argsConfig??>
            argsConfig: ${params.argsConfig},
        </#if>
        <#if params.submitFireEvent??>
            submitFireEvent: "${params.submitFireEvent}",
        </#if>
        <#if field.configName??>
            fieldId: "${field.configName}",
        </#if>
        <#if args.htmlid??>
            formId: "${args.htmlid}",
        </#if>

        <#if form.arguments??>
            args: {
                <#list form.arguments?keys as key>
                    <#if form.arguments[key]??>
                        "${key}": "${form.arguments[key]?string}"<#if key_has_next>,</#if>
                    </#if>
                </#list>},
        </#if>
            rootForm: runtimeForm
        });
        YAHOO.Bubbling.unsubscribe("beforeFormRuntimeInit", onBeforeFormRuntimeInit);
    }

    function onBeforeFormRuntimeInit(layer, args) {
        if (args[1].runtime.formId == "${args.htmlid?html}-form") {
            runtimeForm = args[1].runtime;

            YAHOO.util.Event.onDOMReady(init);
        }
    }

    YAHOO.Bubbling.on("beforeFormRuntimeInit", onBeforeFormRuntimeInit);
})();
//]]></script>

<div class="control multi-form">
    <input type="hidden" name="${field.name}" id="${fieldHtmlId}" value=""/>
    <input type="hidden" name="${field.name}-count" id="${fieldHtmlId}-count" value=""/>
    <#if !hideLegend>
    <fieldset class="fieldset">
        <legend>${field.label?html}</legend>
        </#if>
        <ul id="${fieldHtmlId}-multi-form-documents-list" class="multi-form-documents-list"></ul>

        <#if !disabled>
            <span id="${fieldHtmlId}-addButton" class="yui-button yui-push-button">
                <span class="first-child">
                    <button type="button" title="${msg("actions.add")}">${msg("actions.add")}</button>
                </span>
            </span>
        </#if>
        <#if !hideLegend>
    </fieldset>
    </#if>
</div>
