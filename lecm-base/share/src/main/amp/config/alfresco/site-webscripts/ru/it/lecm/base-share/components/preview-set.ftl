<#if item??>
	<#assign thisSet = item />
<#else>
	<#assign thisSet = set />
</#if >

<@renderSetWithColumns set=thisSet />

<#macro renderSetWithColumns set>
<div class="from-with-preview">
	<#list set.children as child>
		<#if (child_index % 2) == 0>
		<div class="yui-g"><div class="yui-u first">
		<#else>
		<div class="yui-u">
		</#if>
		<#if child.kind == "set">
			<@formLib.renderSet set=child />
		<#else>
			<@formLib.renderField field=form.fields[child.id] />
		</#if>
	</div>
		<#if ((child_index % 2) != 0) || !child_has_next></div></#if>
	</#list>
</div>

</#macro>