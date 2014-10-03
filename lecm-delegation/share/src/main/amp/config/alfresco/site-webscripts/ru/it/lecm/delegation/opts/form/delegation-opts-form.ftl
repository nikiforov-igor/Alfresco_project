<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#--<#assign fieldCanDelegateAll = form.fields["prop_lecm-d8n_delegation-opts-can-delegate-all"]/>-->
<#--<#assign htmlIdCanDelegateAll = args.htmlid?js_string + "_" + fieldCanDelegateAll.id/>-->
<#--<#assign canDelegate=false/>-->
<#--<#if fieldCanDelegateAll.value??>-->
	<#--<#if fieldCanDelegateAll.value?is_boolean>-->
		<#--<#assign canDelegate=fieldCanDelegateAll.value>-->
	<#--<#elseif fieldCanDelegateAll.value?is_string && "true" == fieldCanDelegateAll.value>-->
		<#--<#assign canDelegate=true>-->
	<#--</#if>-->
<#--</#if>-->

<#--<#assign fieldCanTransferRights = form.fields["prop_lecm-d8n_delegation-opts-can-transfer-rights"]/>-->
<#--<#assign htmlIdCanTransferRights = args.htmlid?js_string + "_" + fieldCanTransferRights.id/>-->
<#--<#assign canTransfer=false/>-->
<#--<#if fieldCanTransferRights.value??>-->
	<#--<#if fieldCanTransferRights.value?is_boolean>-->
		<#--<#assign canTransfer=fieldCanTransferRights.value>-->
	<#--<#elseif fieldCanTransferRights.value?is_string && "true" == fieldCanTransferRights.value>-->
		<#--<#assign canTransfer=true>-->
	<#--</#if>-->
<#--</#if>-->

<#if formUI == "true">
	<@formLib.renderFormsRuntime formId=formId />
</#if>
<@formLib.renderFormContainer formId=formId>
	<#-- статус -->
	<#assign fieldActive = form.fields["prop_lecm-dic_active"]/>
	<#assign isActive = false/>
	<#if fieldActive.value??>
		<#if fieldActive.value?is_boolean>
			<#assign isActive = fieldActive.value/>
		<#elseif fieldActive.value?is_string && fieldActive.value == "true">
			<#assign isActive = true/>
		</#if>
	</#if>
	<div class="form-field">
	<#if isActive><span class="actve-delegation-error">Делегирование вступило в силу. Редактирование невозможно</span></#if>
	</div>
	<#-- делегирующее лицо -->
	<@formLib.renderField field=form.fields["assoc_lecm-d8n_delegation-opts-owner-assoc"] />

	<#-- таблица с доверенностями -->
	<fieldset
            id="fieldset-delegate-by-func"
            form="${formId}"
            <#--<#if canDelegate>disabled="disabled"</#if>-->
            >
		<div class="form-field with-grid">
			<script type="text/javascript">//<![CDATA[
				(function(){
					"use strict";
					var datagrid = new LogicECM.module.Delegation.Procuracy.Grid("${args.datagridId}").setOptions({
						bubblingLabel: "${args.datagridId}",
						usePagination:false,
						showExtendSearchBlock:false,
						showCheckboxColumn: false,
						dataSource: "lecm/delegation/get/procuracies",
						searchShowInactive: true,
						editForm: "editProcuracy",
					<#if "view" != form.mode>
						showActionColumn: true,
						actions: [
							{
								type: "datagrid-action-link-${args.datagridId}",
								id: "onActionEdit",
								permission: "edit",
								label: "редактировать доверенность"
							},
//							{
//								type: "datagrid-action-link-procuracy-datagrid",
//								id: "onActionTransferRights",
//								permission: "edit",
//								label: "передавать права руководителя"
//							},
							{
								type: "datagrid-action-link-${args.datagridId}",
								id: "onActionDelete",
								permission: "delete",
								label: "удалить доверенность"
							}
						]
					<#else/>
						showActionColumn: false
					</#if>
					});
					YAHOO.util.Event.onContentReady (datagrid.id, function () {
						YAHOO.Bubbling.fire ("activeGridChanged", {
							datagridMeta:{
								useChildQuery: true,
								itemType: "lecm-d8n:procuracy",
								nodeRef: "${args.itemId}"
							},
							bubblingLabel: datagrid.id
						});
					});
				})();
			//]]>
			</script>
			<@grid.datagrid args.datagridId false/>
		</div>
	</fieldset>
	<#-- "царапина" -->
	<hr/>
	<#-- делегировать все функции -->
	<#--<div class="form-field">-->
		<#--<input id="radio-delegate-all-func"-->
			<#--class="formsRadio"-->
			<#--type="radio"-->
			<#--name="delegate-group"-->
			<#--value="delegate-all"-->
			<#--onchange='javascript: var radioDelegateAllFunc = YAHOO.util.Dom.get("radio-delegate-all-func"); var hidden = YAHOO.util.Dom.get("${htmlIdCanDelegateAll}"); var fieldsetDelegateByFunc = YAHOO.util.Dom.get("fieldset-delegate-by-func"); var fieldsetDelegateAllFunc = YAHOO.util.Dom.get("fieldset-delegate-all-func"); hidden.value = radioDelegateAllFunc.checked; fieldsetDelegateByFunc.disabled = radioDelegateAllFunc.checked; fieldsetDelegateAllFunc.disabled = !radioDelegateAllFunc.checked; YAHOO.Bubbling.fire("mandatoryControlValueUpdated");'-->
			<#--<#if canDelegate>checked="checked"</#if>-->
			<#--<#if "view" == form.mode>disabled="disabled"</#if>-->
		<#-->-->
		<#--<label for="radio-delegate-all-func" class="radio">делегировать все функции</label>-->
	<#--</div>-->
	<fieldset id="fieldset-delegate-all-func" form="${formId}">
		<@formLib.renderField field=form.fields["assoc_lecm-d8n_delegation-opts-trustee-assoc"] />
		<div id="error-message-container"></div>
		<#--<div class="form-field">-->
			<#--<label for="checkbox-can-transfer">&nbsp;</label>-->
			<#--<input id="${htmlIdCanTransferRights}" type="hidden" name="${fieldCanTransferRights.name}" value="${canTransfer?string}"/>-->
			<#--<input id="checkbox-can-transfer"-->
				   <#--class="formsCheckBox"-->
				   <#--type="checkbox"-->
				   <#--name="-"-->
				   <#--onchange='javascript:YAHOO.util.Dom.get("${htmlIdCanTransferRights}").value=YAHOO.util.Dom.get("checkbox-can-transfer").checked;'-->
				   <#--<#if canTransfer>checked="checked"</#if>-->
				<#--<#if "view" == form.mode>disabled="disabled"</#if>-->
			<#-->-->
			<#--<label for="checkbox-can-transfer" class="checkbox">${fieldCanTransferRights.label?html}</label>-->
		<#--</div>-->
	</fieldset>
</@>
