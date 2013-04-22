<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign aDateTime = .now>
<#assign controlId = fieldHtmlId + "-cntrl-" + aDateTime?iso_utc>


<div class="form-field">
    <label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
    <div id="${controlId}"></div>
    <select id="${fieldHtmlId}" name="${field.name}" tabindex="0" size="7" style="width: 100%; height: 80px;">
    </select>
<@formLib.renderFieldHelp field=field />
    <script type="text/javascript">//<![CDATA[
    new LogicECM.module.Contracts.BasedOnDocumentSelection("${fieldHtmlId}").setOptions({
        controlId: "${controlId}"
    }).setMessages(${messages});
    //]]></script>
</div>