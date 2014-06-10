<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />

<#if item??>
    <#assign thisSet = item />
<#else>
    <#assign thisSet = set />
</#if >

<@renderSetWithColumns set=thisSet />

<#macro renderSetWithColumns set>
    <#list set.children as item>
        <#if (item_index % 2) == 0>
            <div class="yui-g two-column"><div class="yui-u first">
        <#else>
            <div class="yui-u">
        </#if>
        <#if item.kind == "set">
            <#if item.template??>
                <#include "${item.template}" />
            <#else>
                <@formLib.renderSet set=item />
            </#if>
        <#else>
            <@formLib.renderField field=form.fields[item.id] />
        </#if>
        </div>
        <#if ((item_index % 2) != 0) || !item_has_next></div></#if>
    </#list>
    <div class="clear"></div>
</#macro>