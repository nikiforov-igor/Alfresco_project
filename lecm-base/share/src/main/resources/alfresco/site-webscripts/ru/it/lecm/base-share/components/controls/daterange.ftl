<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />

<@renderDateRange set=set />

<#macro renderDateRange set>
	<div class="lecm-daterange-control">
		<#list set.children as item>
			<#if (item_index < 3)>
				<#if item_index == 0>
					<div class="datarange-start-date">
				<#elseif item_index == 1>
					<div class="datarange-end-date">
				<#elseif item_index == 2>
					<div class="datarange-unlimited">
				</#if>
				<@formLib.renderField field=form.fields[item.id] />
				</div>
			</#if>
		</#list>
	</div>
</#macro>
