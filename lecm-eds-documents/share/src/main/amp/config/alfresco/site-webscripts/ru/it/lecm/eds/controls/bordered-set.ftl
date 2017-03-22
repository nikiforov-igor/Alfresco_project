<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />

<#assign formId=args.htmlid?js_string?html + "-form"/>

<div class="bordered-set">
<#list set.children as item>
    <#if item.kind == "set">
        <@formLib.renderSet set = item />
    <#else>
        <@formLib.renderField field = form.fields[item.id]/>
    </#if>
</#list>
</div>
<script>
    (function() {
        LogicECM.module.Base.Util.loadCSS([
            'css/lecm-eds-documents/bordered-set.css'
        ]);
    })();
</script>
<div class="clear"></div>