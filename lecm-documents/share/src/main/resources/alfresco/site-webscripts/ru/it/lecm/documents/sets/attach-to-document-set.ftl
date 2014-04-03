<#list set.children as item>
	<#if item.kind != "set">
	<div class="attach-to-document">
		<@formLib.renderField field=form.fields[item.id]/>
	</div>
	</#if>
</#list>