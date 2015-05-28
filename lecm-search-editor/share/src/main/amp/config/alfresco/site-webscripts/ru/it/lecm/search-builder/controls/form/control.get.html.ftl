<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />

<@standalone>
    <@markup id="html">
        <@uniqueIdDiv>
            <#assign formId=args.htmlid?js_string?html + "-form">
            <#list form.structure as item>
                <#if item.kind != "set">
                    <@formLib.renderField field=form.fields[item.id] />
                </#if>
            </#list>
        </@>
    </@>
</@>
