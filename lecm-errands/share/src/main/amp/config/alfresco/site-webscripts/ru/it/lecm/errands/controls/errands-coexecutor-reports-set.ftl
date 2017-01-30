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
    <div class="yui-g two-column">
        <div class="yui-u first">
        <#list set.children as item>
            <#if item.kind == "set">
                <#if item.template??>
                    <#include "${item.template}" />
                <#else>
                    <@formLib.renderSet set=item />
                </#if>
            <#else>
                <@formLib.renderField field=form.fields[item.id] />
            </#if>
            <#if !item_has_next></div></#if>
        </#list>
        <div class="yui-u">
            <div class="coexutor-report-actions-div">
                <span id="approveReportButton" class="yui-button yui-push-button">
                    <span class="first-child">
                        <button type="button">${msg("label.coexecutor.report.buttons.approve")}</button>
                    </span>
                </span>
                <span id="declineReportButton" class="yui-button yui-push-button">
                    <span class="first-child">
                        <button type="button">${msg("label.coexecutor.report.buttons.decline")}</button>
                    </span>
                </span>
                <span id="transferReportButton" class="yui-button yui-push-button">
                    <span class="first-child">
                        <button type="button">${msg("label.coexecutor.report.buttons.transfer")}</button>
                    </span>
                </span>
            </div>
        </div>
    </div>
</div>
<div class="clear"></div>
