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

<div>hello from custom form</div>
<#if formUI == "true">
	<@formLib.renderFormsRuntime formId=formId />
</#if>
<@formLib.renderFormContainer formId=formId>
	<#-- статус -->
	<@formLib.renderField field=form.fields["prop_lecm-d8n_delegation-opts-status"] />
	<#-- делегирующее лицо -->
	<@formLib.renderField field=form.fields["assoc_lecm-d8n_delegation-opts-owner-assoc"] />
	<#-- делегировать по бизнес функциям -->
	<div class="form-field">
		<input id="${htmlIdCanDelegateAll}" type="hidden" name="${fieldCanDelegateAll.name}" value="<#if isTrue>true<#else>false</#if>"/>
		<input id="radio-delegate-by-func"
			class="formsRadio"
			type="radio"
			name="delegate-group"
			value="delegate-by"
			onchange='javascript:YAHOO.util.Dom.get("${htmlIdCanDelegateAll}").value=!YAHOO.util.Dom.get("radio-delegate-by-func").checked;'
			<#if !isTrue>checked="checked"</#if>
		>
		<label for="radio-delegate-by-func" class="radio">делегировать по бизнес функциям</label>
	</div>
	<#-- таблица с доверенностями -->
	<fieldset id="fieldset-delegate-by-func" form="${formId}" style="border: 0; margin-bottom: 0; padding: 0">
		<script type="text/javascript">//<![CDATA[
			var datagridEl = new YAHOO.util.Element("${args.datagridId}-body");
			datagridEl.on('contentReady', function () {
				var datagrid = new LogicECM.module.Delegation.Procuracy.Grid("${args.datagridId}");
				datagrid.setOptions({
					bubblingLabel: "procuracy-datagrid",
					usePagination:false,
					showExtendSearchBlock:false,
					showCheckboxColumn: false,
					dataSource: "lecm/delegation/get/procuracies",
					searchShowInactive: true,
					editForm: "editProcuracy",
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
				});

				YAHOO.Bubbling.fire ("activeGridChanged", {
					datagridMeta:{
						itemType: "lecm-d8n:procuracy",
						nodeRef: "${args.itemId}"
					}
				});
			});
		//]]>
		</script>
		<div class="form-field with-grid">
			<@grid.datagrid args.datagridId/>
		</div>
	</fieldset>
	<#-- "царапина" -->
	<hr style="margin-top: 1.5em; background-color: #AAAAAA;">
	<#-- делегировать все функции -->
	<div class="form-field">
		<input id="radio-delegate-all-func"
			class="formsRadio"
			type="radio"
			name="delegate-group"
			value="delegate-all"
			onchange='javascript:YAHOO.util.Dom.get("${htmlIdCanDelegateAll}").value=YAHOO.util.Dom.get("radio-delegate-all-func").checked;'
			<#if isTrue>checked="checked"</#if>
		>
		<label for="radio-delegate-all-func" class="radio">делегировать все функции</label>
	</div>
	<fieldset id="fieldset-delegate-all-func" form="${formId}" style="border: 0; margin-bottom: 0; padding: 0">
		<@formLib.renderField field=form.fields["assoc_lecm-d8n_delegation-opts-trustee-assoc"] />
		<@formLib.renderField field=form.fields["prop_lecm-d8n_delegation-opts-can-transfer-rights"] />
	</fieldset>
</@>
