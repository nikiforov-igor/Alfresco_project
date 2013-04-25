<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />

<#assign htmlid = args.htmlid?js_string/>
<#assign startDateHtmlId = ""/>
<#assign endDateHtmlId = ""/>
<#assign unlimitHtmlId = ""/>
<@renderDateRange set=set />

<#macro renderDateRange set>
	<div id="${htmlid}" class="lecm-daterange-control">
		<#list set.children as item>
			<#if (item_index < 3)>
				<#if item_index == 0>
					<#assign startDateHtmlId = (htmlid + "_" + item.id)/>
					<div class="datarange-start-date">
				<#elseif item_index == 1>
					<#assign endDateHtmlId = (htmlid + "_" + item.id)/>
					<div class="datarange-end-date">
				<#elseif item_index == 2>
					<#assign unlimitHtmlId = (htmlid + "_" + item.id)/>
					<div class="datarange-unlimited">
				</#if>
				<@formLib.renderField field=form.fields[item.id] />
				</div>
			</#if>
		</#list>
	</div>
</#macro>

<script type="text/javascript">
	new LogicECM.DateRange("${htmlid}").setOptions({
		startDateHtmlId: "${startDateHtmlId}",
		endDateHtmlId: "${endDateHtmlId}",
		unlimitedHtmlId: "${unlimitHtmlId}"
	}).setMessages(${messages});
</script>
