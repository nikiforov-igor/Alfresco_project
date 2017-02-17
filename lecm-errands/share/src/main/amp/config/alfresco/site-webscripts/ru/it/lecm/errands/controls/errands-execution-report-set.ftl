<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />

<#if item??>
    <#assign thisSet = item />
<#else>
    <#assign thisSet = set />
</#if >

<#assign id=args.htmlid/>

<script type="text/javascript">//<![CDATA[
(function() {
    LogicECM.module.Base.Util.loadCSS([
        'css/lecm-errands/errands-execution-report-set.css'
    ]);
})();
//]]></script>

<div class="errands-execution-report-set">
    <div class="errands-execution-report-status">
        <@formLib.renderField field=form.fields[set.children[0].id]/>
    </div>
    <div class="errands-execution-report-fields">
        <@formLib.renderField field=form.fields[set.children[1].id]/>
        <@formLib.renderField field=form.fields[set.children[2].id]/>
        <@formLib.renderField field=form.fields[set.children[3].id]/>
        <@formLib.renderField field=form.fields[set.children[4].id]/>
    </div>
    <@formLib.renderField field=form.fields[set.children[5].id]/>
    <div class="clear"></div>
</div>
<div class="errands-execution-report-empty hidden1">
    <span>${msg("message.errands.execution-reports.empty")}</span>
</div>
<div class="errands-hidden-execution-report-processing hidden1">
    <textarea rows ="6" disabled>${msg("message.errands.execution-reports.hidden.inprocess")}</textarea>
</div>