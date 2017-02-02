<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />

<script type="text/javascript">//<![CDATA[
(function() {
    LogicECM.module.Base.Util.loadCSS([
        'css/lecm-errands/errands-coexecutor-reports-set.css'
    ]);
})();

//]]></script>
<#if item??>
    <#assign thisSet = item />
<#else>
    <#assign thisSet = set />
</#if >
<div class="coexecutor-report-expand-set">
        <div class="coexecutor-report-expand-fields">
            <#list set.children as item>
                <#if (item_index <= 2) >
                    <#if item.kind == "set">
                        <#if item.template??>
                            <#include "${item.template}" />
                        <#else>
                            <@formLib.renderSet set=item />
                        </#if>
                    <#else>
                        <@formLib.renderField field=form.fields[item.id] />
                    </#if>
                </#if>
            </#list>
        </div>
        <div class="coexecutor-report-expand-actions">
            <#list set.children as item>
                <#if (item_index > 2) >
                    <#if item.kind == "set">
                        <#if item.template??>
                            <#include "${item.template}" />
                        <#else>
                            <@formLib.renderSet set=item />
                        </#if>
                    <#else>
                        <@formLib.renderField field=form.fields[item.id] />
                    </#if>
                </#if>
            </#list>
        </div>
</div>
<div class="clear"></div>