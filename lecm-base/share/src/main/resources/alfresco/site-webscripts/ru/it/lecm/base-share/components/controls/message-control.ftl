<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />
<#include "association-tree-picker-dialog.inc.ftl">

<#assign fieldValue=field.value!"">
<#assign controlId = fieldHtmlId + "-cntrl">

<div class="form-field">
    ${msg(field.control.params.msg)}
</div>