<#import "/ru/it/lecm/documents/controls/history/status-history.lib.ftl" as historyStatus/>
<#assign formId = "form-history-status">

<div class="control status-historyl viewmode">
	<div class="label-div">
		<label>${field.label?html}:</label>
	</div>
	<div class="container">
		<div class="value-div">
			<#if field.value == "">
				${msg("form.control.novalue")}
			<#else>
				<a onclick="LogicECM.module.DocumentStatusHistory.showDialog('${formId}', '${form.arguments.itemId}');" href="javascript:void(0);" id="${fieldHtmlId}">${field.value}</a>
			</#if>
		</div>
	</div>
	<@historyStatus.showDialog formId="${formId}"/>
</div>
<div class="clear"></div>
