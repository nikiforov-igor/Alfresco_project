<div class="control status-control without-label">
    <#if field.mandatory && !(field.value?is_number) && field.value == "">
        <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
    </#if>
    ${field.value}
</div>