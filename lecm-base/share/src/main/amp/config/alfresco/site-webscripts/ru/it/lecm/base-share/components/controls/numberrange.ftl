<#assign viewFormat>${msg("form.control.date-picker.view.date.format")}</#assign>

<#assign controlId = fieldHtmlId + "-cntrl">
<#if field.control.params.onlyPositive?? && field.control.params.onlyPositive == "true"><#assign onlyPositive=true><#else><#assign onlyPositive=false></#if>

<#assign defaultValue>
    <#if (field.control.params.defaultFrom?? || field.control.params.defaultTo??)>
    ${field.control.params.defaultFrom!""}|${field.control.params.defaultTo!""}
    </#if>
</#assign>
<#if form.mode == "create" && defaultValue?string == "">
    <#if form.arguments[field.name + "-number-range"]?has_content>
        <#assign defaultValue=form.arguments[field.name + "-number-range"]>
    </#if>
</#if>

<div class="control numberrange-control viewmode">
    <div class="label-div">
        <label for="${fieldHtmlId}">${field.label?html}:
        <#if mandatory>
            <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
        </#if>
        </label>
    </div>
    <div class="container">
        <div class="buttons-div">
            <@formLib.renderFieldHelp field=field />
        </div>
        <div class="value-div">
            <input id="${fieldHtmlId}" type="hidden" name="${field.name}-number-range" value=""/>
            <div id="${controlId}">
                <div class="yui-g">
                    <div class="yui-u first">
                        <span>${msg("form.control.range.min")}:</span>
                    <#-- min value -->
                        <input id="${controlId}-min" name="-" type="text" class="number" <#if field.description??>title="${field.description}"</#if> tabindex="0" />
                    </div>
                    <div class="yui-u">
                        <span>${msg("form.control.range.max")}:</span>
                    <#-- max value -->
                        <input id="${controlId}-max" name="-" type="text" class="number" <#if field.description??>title="${field.description}"</#if> tabindex="1" />
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="clear"></div>

<script type="text/javascript">//<![CDATA[
(function()
{
    function init() {
        LogicECM.module.Base.Util.loadScripts([
            'scripts/lecm-base/components/lecm-numberrange.js'
        ], createNumberRange);
    }

    function createNumberRange(){
        var control = new LogicECM.NumberRange("${controlId}", "${fieldHtmlId}").setMessages(
        ${messages}
        );
        control.setOptions({
            onlyPositive: ${onlyPositive?string},
        <#if defaultValue?has_content>
            defaultValue: "${defaultValue?string}",
        </#if>
            fieldId: "${field.configName}-number-range",
            formId: "${args.htmlid}"
        });
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

