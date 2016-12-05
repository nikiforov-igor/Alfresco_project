<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />
<#assign id=args.htmlid/>

<script type="text/javascript">//<![CDATA[
(function() {
    LogicECM.module.Base.Util.loadCSS([
        'css/lecm-errands/wf-due-date-set.css'
    ]);
})();
//]]></script>

<div class="errands-wf-duedate-set">
    <div class="errands-wf-duedate-set-radio">
    <@formLib.renderField field=form.fields[set.children[0].id]/>
    </div>
    <div class="errands-wf-duedate-set-date">
    <@formLib.renderField field=form.fields[set.children[1].id]/>
    </div>
    <div class="errands-wf-duedate-set-limitless">
    <@formLib.renderField field=form.fields[set.children[2].id]/>
    </div>
    <div class="clear"></div>
</div>