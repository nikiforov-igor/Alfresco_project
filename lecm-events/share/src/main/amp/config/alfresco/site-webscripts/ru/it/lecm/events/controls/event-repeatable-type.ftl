<#assign defaultValue=field.value>
<#if form.mode == "create" && defaultValue?string == "">
    <#if form.arguments[field.name]?has_content>
        <#assign defaultValue=form.arguments[field.name]>
    </#if>
</#if>

<div class="control editmode">
    <div class="label-div">
        <label for="${fieldHtmlId}">
        ${field.label?html}:
        <#if field.mandatory>
            <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
        </#if>
        </label>
    </div>
    <div class="container">
        <div class="buttons-div"><@formLib.renderFieldHelp field=field /></div>
        <div class="value-div">
            <input type="radio" name="${field.name}" value="THIS_ONLY" checked>${msg("form.event.repeatabable.type.this.only")}</input><br/>
            <input type="radio" name="${field.name}" value="ALL">${msg("form.event.repeatabable.type.all")}</input><br/>
            <input type="radio" name="${field.name}" value="ALL_NEXT">${msg("form.event.repeatabable.type.all.next")}</input><br/>
            <input type="radio" name="${field.name}" value="ALL_PREV">${msg("form.event.repeatabable.type.all.prev")}</input>
        </div>
    </div>
</div>
<div class="clear"></div>
