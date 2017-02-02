<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />

<script type="text/javascript">//<![CDATA[
(function() {
    LogicECM.module.Base.Util.loadCSS([
        'css/lecm-errands/errands-coexecutor-report-expand-set.css'
    ]);
})();
//]]></script>

<#assign formId=args.htmlid/>

<#if item??>
    <#assign thisSet = item />
<#else>
    <#assign thisSet = set />
</#if >
<div id="${formId}-coexecutor-report-expand-set" class="coexecutor-report-expand-set">
        <div id="${formId}-coexecutor-report-expand-fields" class="coexecutor-report-expand-fields">
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
        <div id="${formId}-coexecutor-report-expand-actions" class="coexecutor-report-expand-actions">
            <div id="${formId}-acceptActionBlock" class="acceptActionBlock">
            <@formLib.renderField field=form.fields[set.children[3].id]/>
            </div>
            <div id="${formId}-declineActionBlock" class="declineActionBlock">
            <@formLib.renderField field=form.fields[set.children[4].id]/>
            </div>
            <div id="${formId}-transferActionBlock" class="transferActionBlock">
            <@formLib.renderField field=form.fields[set.children[5].id]/>
            </div>
            <div id="${formId}-editActionBlock" class="editActionBlock">
            <@formLib.renderField field=form.fields[set.children[6].id]/>
            </div>
        </div>
</div>
<div class="clear"></div>