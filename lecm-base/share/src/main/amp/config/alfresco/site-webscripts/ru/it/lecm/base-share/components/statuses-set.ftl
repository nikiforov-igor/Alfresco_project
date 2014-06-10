<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />

<#if item??>
    <#assign thisSet = item />
<#else>
    <#assign thisSet = set />
</#if >

<div class="statuses-set">
    <#list thisSet.children as item>
        <@formLib.renderField field=form.fields[item.id] />
    </#list>
</div>