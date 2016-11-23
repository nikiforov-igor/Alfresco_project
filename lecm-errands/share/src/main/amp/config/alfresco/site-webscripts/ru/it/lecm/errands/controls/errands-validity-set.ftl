<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />

<#assign id=args.htmlid/>
<#assign thisSetFields = []/>
<#assign thisSetSubsets = []/>

<script type="text/javascript">//<![CDATA[
(function () {
    LogicECM.module.Base.Util.loadCSS([
        'css/lecm-errands/errands-validity-set.css'
    ]);
})();
//]]></script>

<#list set.children as item>
    <#if item.kind == "set">
        <#assign thisSetSubsets = thisSetSubsets + [item] />
    <#else>
        <#assign thisSetFields = thisSetFields + [item] />
    </#if>
</#list>

<div class="errands-validity-set">
    <div class="errands-validity-set-radio">
        <@formLib.renderField field=form.fields[thisSetFields[0].id]/>
    </div>
    <div class="errands-validity-set-dates">
        <@formLib.renderSet set = thisSetSubsets[0]/>
    </div>
    <div class="errands-validity-set-during">
        <@formLib.renderSet set = thisSetSubsets[1]/>
    </div>
    <div class="errands-validity-set-reiteration-count">
        <@formLib.renderSet set = thisSetSubsets[2]/>
    </div>
    <div class="errands-validity-set-period-endless">
        <@formLib.renderField field=form.fields[thisSetFields[1].id]/>
    </div>
    <div class="clear"></div>
</div>