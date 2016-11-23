<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />

<#assign formId=args.htmlid?js_string?html + "-form"/>

<div class="${formId}-panel ${set.id} hidden1">
    <#list set.children as item>
        <#if item.kind == "set">
            <@formLib.renderSet set = item />
        <#else>
            <@formLib.renderField field=form.fields[item.id]/>
        </#if>
    </#list>
</div>
<div class="clear"></div>