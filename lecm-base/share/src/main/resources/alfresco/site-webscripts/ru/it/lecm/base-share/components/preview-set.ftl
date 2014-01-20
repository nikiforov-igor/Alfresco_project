<#if item??>
	<#assign thisSet = item />
<#else>
	<#assign thisSet = set />
</#if >

<@renderSetWithColumns set=thisSet />

<#macro renderSetWithColumns set>
<div class="from-with-preview">
	<#list set.children as item>
		<#if (item_index % 2) == 0>
		<div class="yui-g"><div class="yui-u first">
		<#else>
		<div class="yui-u">
		</#if>
		<#if item.kind == "set">
			<@formLib.renderSet set=item />
		<#else>
			<@formLib.renderField field=form.fields[item.id] />
		</#if>
	</div>
		<#if ((item_index % 2) != 0) || !item_has_next></div></#if>
	</#list>
</div>

</#macro>