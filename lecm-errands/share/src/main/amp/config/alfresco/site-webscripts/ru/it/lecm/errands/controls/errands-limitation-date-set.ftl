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
        'css/lecm-errands/errands-limitation-date-set.css'
    ]);
})();
//]]></script>

<div class="errands-limitation-date-set">
    <div class="errands-limitation-date-set-radio">
        <@formLib.renderField field=form.fields[set.children[0].id]/>
    </div>
    <div class="errands-limitation-date-set-days">
        <@formLib.renderField field=form.fields[set.children[2].id]/>
    </div>
    <div class="errands-limitation-date-set-type">
        <@formLib.renderField field=form.fields[set.children[3].id]/>
    </div>

    <div class="errands-limitation-date-set-date">
        <@formLib.renderField field=form.fields[set.children[4].id]/>
    </div>
    <div class="errands-limitation-date-set-limitless">
    <@formLib.renderField field=form.fields[set.children[1].id]/>
    </div>
    <div class="clear"></div>
</div>