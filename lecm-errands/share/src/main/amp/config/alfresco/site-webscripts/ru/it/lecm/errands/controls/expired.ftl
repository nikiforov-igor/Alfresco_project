<div class="control expired-control without-label">
<#if field.value??>
    <#if (field.value?is_boolean && field.value == true) || (field.value?is_string && field.value == "true")>
        ${msg("label.errands.expired")}
    </#if>
</#if>
</div>
