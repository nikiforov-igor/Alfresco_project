<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />

<#if formUI == "true">
	<@formLib.renderFormsRuntime formId=formId />
</#if>

<@formLib.renderFormContainer formId=formId>
	<div class="yui-content">
		<#list form.structure as item>
			<#if item.kind == "set">
			<div class='border-style'>
				<#if item_index == 2 || item_index == 3 >
					<p>${item.label}</p>
				</#if>
				<div><@formLib.renderSet set=item /></div>
			</div>
			</#if>
		</#list>
	</div>
</@>

