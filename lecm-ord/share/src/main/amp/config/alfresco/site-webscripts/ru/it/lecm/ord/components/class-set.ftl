<#if item??>
	<#assign thisSet = item />
<#else>
	<#assign thisSet = set />
</#if >

<@renderClassSet set=thisSet />

<#macro renderClassSet set>
<div class="${set.id}">
	<#list set.children as child>
		<#if child.kind == "set">
			<@formLib.renderSet set=child />
		<#else>
			<@formLib.renderField field=form.fields[child.id] />
		</#if>
	</#list>
</div>

</#macro>