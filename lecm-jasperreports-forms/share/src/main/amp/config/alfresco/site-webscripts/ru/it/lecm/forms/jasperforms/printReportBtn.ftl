<#assign controlId = fieldHtmlId + "-cntrl">

<#if field.control.params.reportId??>
    <#assign reportId = field.control.params.reportId>

    <#if field.control.params.buttonLabel??>
        <#assign buttonLabel = field.control.params.buttonLabel>
    <#else>
        <#assign buttonLabel = msg("button.print")>
    </#if>

<div class="form-field">
    <#escape x as x?js_string>
        <div id="${controlId}" class="yui-skin-sam">
            <button id="${controlId}-print-button" type="button"
                    onclick="LogicECM.module.Base.Util.printReport('${form.arguments.itemId}', '${reportId}')">
            ${buttonLabel}
            </button>
        </div>
    </#escape>
</div>
</#if>
