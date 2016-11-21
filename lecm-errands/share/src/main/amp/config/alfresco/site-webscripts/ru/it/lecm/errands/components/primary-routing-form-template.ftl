<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />

<script type="text/javascript">//<![CDATA[
(function() {
    LogicECM.module.Base.Util.loadCSS([
        'css/lecm-errands/primary-routing-form.css'
    ]);

})();
//]]></script>

<#if formUI == "true">
    <@formLib.renderFormsRuntime formId=formId />
</#if>
<@formLib.renderFormContainer formId=formId>
    <#list form.structure as item>
        <#if item.kind == "set">
            <#if item_index == 0>
            <div class="yui-g"><div class="yui-u first">
            <#else>
            <div class="yui-u">
            </#if>
            <@formLib.renderSet set=item />
        </div>
        <#else>
            <@formLib.renderField field=form.fields[item.id] />
        </#if>
    </#list>
</@>
