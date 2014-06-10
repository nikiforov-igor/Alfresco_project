<div class="form-field field-status">
    <div class="read-only-status">
        <#if field.mandatory && !(field.value?is_number) && field.value == "">
            <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
        </#if>
        ${field.value}
    </div>
</div>