<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />
<#include "association-tree-picker-dialog.inc.ftl">

<#assign fieldValue=field.value!"">
<#assign controlId = fieldHtmlId + "-cntrl">
<#if field.control.params.cssClasses??>
    <#assign cssClasses = field.control.params.cssClasses?string>
<#else>
    <#assign cssClasses = "text-on-mid">
</#if>
<div class="form-field ${cssClasses}">
    ${msg(field.control.params.msg)}
</div>
<script type="text/javascript">
(function() {
    LogicECM.module.Base.Util.loadCSS([
        'css/lecm-base/components/controls/message-control.css'
    ]);
})();
</script>