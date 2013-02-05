<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#assign fieldCanDelegateAll = form.fields["prop_lecm-d8n_delegation-opts-can-delegate-all"]/>
<#assign isTrue=false>
<#if fieldCanDelegateAll.value??>
	<#if fieldCanDelegateAll.value?is_boolean>
		<#assign isTrue=fieldCanDelegateAll.value>
	<#elseif fieldCanDelegateAll.value?is_string && fieldCanDelegateAll.value == "true">
		<#assign isTrue=true>
	</#if>
</#if>
<#assign htmlIdCanDelegateAll = args.htmlid?js_string + "_" + fieldCanDelegateAll.id/>

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
	<#if isActive>
		 <span style="color: #7F0000; font-weight: bold;">Делегирование вступило в силу. Редактирование невозможно</span>
	<#else>
		 <span style="color: #007F00; font-weight: bold;">Делегирование еще неактивно.</span>
	</#if>
	</div>
	<#-- делегирующее лицо -->
	<@formLib.renderField field=form.fields["assoc_lecm-d8n_delegation-opts-owner-assoc"] />
	<#-- делегировать по бизнес функциям -->
	<div class="form-field">
		<input id="${htmlIdCanDelegateAll}" type="hidden" name="${fieldCanDelegateAll.name}" value="${isTrue?string}"/>
		<input id="radio-delegate-by-func"
			class="formsRadio"
			type="radio"
			name="delegate-group"
			value="delegate-by"
			onchange='javascript:var radioDelegateByFunc = YAHOO.util.Dom.get("radio-delegate-by-func"); var hidden = YAHOO.util.Dom.get("${htmlIdCanDelegateAll}"); var fieldsetDelegateByFunc = YAHOO.util.Dom.get("fieldset-delegate-by-func"); var fieldsetDelegateAllFunc = YAHOO.util.Dom.get("fieldset-delegate-all-func"); hidden.value = !radioDelegateByFunc.checked; fieldsetDelegateByFunc.disabled = !radioDelegateByFunc.checked; fieldsetDelegateAllFunc.disabled = radioDelegateByFunc.checked;'
			<#if !isTrue>checked="checked"</#if>
			<#if "view" == form.mode>disabled="disabled"</#if>
		>
		<label for="radio-delegate-by-func" class="radio">делегировать по бизнес функциям</label>
	</div>
	<#-- таблица с доверенностями -->
	<fieldset id="fieldset-delegate-by-func" form="${formId}" style="border: 0; margin-bottom: 0; padding: 0px 0px 0px 1px;" <#if isTrue>disabled="disabled"</#if>>
		<div class="form-field with-grid">
			<script type="text/javascript">//<![CDATA[
				var datagrid = new LogicECM.module.Delegation.Procuracy.Grid("${args.datagridId}").setOptions({
					bubblingLabel: "procuracy-datagrid",
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
							type: "action-link-procuracy-datagrid",
							id: "onActionEdit",
							permission: "edit",
							label: "редактировать доверенность"
						},
						{
							type: "action-link-procuracy-datagrid",
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
							itemType: "lecm-d8n:procuracy",
							nodeRef: "${args.itemId}"
						}
					});
				});
			//]]>
			</script>
			<@grid.datagrid args.datagridId/>
		</div>
	</fieldset>
	<#-- "царапина" -->
	<hr style="margin-top: 1.5em; background-color: #C5C7C6;">
	<#-- делегировать все функции -->
	<div class="form-field">
		<input id="radio-delegate-all-func"
			class="formsRadio"
			type="radio"
			name="delegate-group"
			value="delegate-all"
			onchange='javascript: var radioDelegateAllFunc = YAHOO.util.Dom.get("radio-delegate-all-func"); var hidden = YAHOO.util.Dom.get("${htmlIdCanDelegateAll}"); var fieldsetDelegateByFunc = YAHOO.util.Dom.get("fieldset-delegate-by-func"); var fieldsetDelegateAllFunc = YAHOO.util.Dom.get("fieldset-delegate-all-func"); hidden.value = radioDelegateAllFunc.checked; fieldsetDelegateByFunc.disabled = radioDelegateAllFunc.checked; fieldsetDelegateAllFunc.disabled = !radioDelegateAllFunc.checked;'
			<#if isTrue>checked="checked"</#if>
			<#if "view" == form.mode>disabled="disabled"</#if>
		>
		<label for="radio-delegate-all-func" class="radio">делегировать все функции</label>
	</div>
	<fieldset id="fieldset-delegate-all-func" form="${formId}" style="border: 0; margin-bottom: 0; padding: 0px 0px 0px 1px;" <#if !isTrue>disabled="disabled"</#if>>
		<@formLib.renderField field=form.fields["assoc_lecm-d8n_delegation-opts-trustee-assoc"] />
		<@formLib.renderField field=form.fields["prop_lecm-d8n_delegation-opts-can-transfer-rights"] />
	</fieldset>
</@>
