<#import "/ru/it/lecm/documents/controls/history/status-history.lib.ftl" as historyStatus/>
<#assign formId = "form-history-status">

<div class="form-field">
	<@historyStatus.showDialog formId="${formId}"/>
    <div class="viewmode-field">
        <span class="viewmode-label">${field.label?html}:</span>
        <span class="viewmode-value">
            <#if field.value == "">
                ${msg("form.control.novalue")}
            <#else>
                <a onclick="LogicECM.module.DocumentStatusHistory.showDialog('${formId}', '${form.arguments.itemId}');" href="javascript:void(0);" id="${fieldHtmlId}">${field.value}</a>
            </#if>
        </span>
    </div>
</div>