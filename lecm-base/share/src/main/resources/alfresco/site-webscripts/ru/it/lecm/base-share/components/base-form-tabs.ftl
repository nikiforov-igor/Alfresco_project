<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />

<#if formUI == "true">
    <@formLib.renderFormsRuntime formId=formId />
</#if>

<@formLib.renderFormContainer formId=formId>
<div id="${formId}-tabs" class="yui-navset">
    <ul class="yui-nav">
        <#list form.structure as item>
            <#if item.kind == "set">
                <li <#if item_index == 0>class="selected"</#if>>
                    <a href="#tab${item_index + 1}">
                        <em>${item.label}</em>
                    </a>
                </li>
            </#if>
        </#list>
    </ul>
    <div class="yui-content">
        <#list form.structure as item>
            <#if item.kind == "set">
                <div><p><@formLib.renderSet set=item /></p></div>
            </#if>
        </#list>
    </div>
</div>
</@>
