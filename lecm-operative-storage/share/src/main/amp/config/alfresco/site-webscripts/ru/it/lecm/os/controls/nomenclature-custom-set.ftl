<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />

<#if item??>
    <#assign thisSet = item />
<#else>
    <#assign thisSet = set />
</#if>
<div class="<#if form.mode == "view">nomenclature-view-set</#if>">
<#list thisSet.children as item>
    <#if item.kind == "set">
        <@formLib.renderSet set=item />
    <#else>
        <@formLib.renderField field=form.fields[item.id] />
    </#if>
</#list>
</div>