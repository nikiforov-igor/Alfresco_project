<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<#if disabled>

<div class="form-field">
    <div class="viewmode-field">
        <span class="viewmode-label">${field.label?html}:</span>
        <span id="" class="viewmode-value"></span>
    </div>
</div>

<#else>

<div class="form-field">
    <label>${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>

    <label for="${fieldHtmlId}-interactionBySpecop">Используя спецоператора</label>
    <input id="${fieldHtmlId}-interactionBySpecop" type="radio" name="interactionTypeRadioGroup" value="SPECOP">

    <label for="${fieldHtmlId}-interactionByEmail">Email</label>
    <input id="${fieldHtmlId}-interactionByEmail" type="radio" name="interactionTypeRadioGroup" value="EMAIL">
</div>

</#if>

<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value}"/>