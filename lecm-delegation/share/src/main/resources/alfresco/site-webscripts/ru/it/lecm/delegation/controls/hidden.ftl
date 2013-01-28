<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#assign isTrue=false>
<#if field.value??>
	<#if field.value?is_boolean>
		<#assign isTrue=field.value>
	<#elseif field.value?is_string && field.value == "true">
		<#assign isTrue=true>
	</#if>
</#if>


<#if form.mode == "edit" || form.mode == "create">
<div class="form-field">
	<input id="${fieldHtmlId}" type="hidden" name="${field.name}" value="<#if isTrue>true<#else>false</#if>"/>
	<input id="radioDelegateByFunc"
		class="formsRadio"
		type="radio"
		name="delegate-group"
		value="delegate-by"
		onchange='javascript:YAHOO.util.Dom.get("${fieldHtmlId}").value=!YAHOO.util.Dom.get("radioDelegateByFunc").checked;'
		<#if !isTrue>checked="checked"</#if>
	>
	<label for="radioDelegateByFunc" class="radio">делегировать по бизнес функциям</label>
</div>
<div class="form-field with-grid">
	<@grid.datagrid args.datagridId/>
</div>
<div class="form-field">
	<hr style="margin-top: 1.5em;">
</div>
<div class="form-field">
	<input id="radioDelegateAllFunc"
		class="formsRadio"
		type="radio"
		name="delegate-group"
		value="delegate-all"
		onchange='javascript:YAHOO.util.Dom.get("${fieldHtmlId}").value=YAHOO.util.Dom.get("radioDelegateAllFunc").checked;'
		<#if isTrue>checked="checked"</#if>
	>
	<label for="radioDelegateAllFunc" class="radio">делегировать все функции</label>
</div>
</#if>
