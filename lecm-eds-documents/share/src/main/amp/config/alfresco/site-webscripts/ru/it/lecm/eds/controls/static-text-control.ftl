<div class="control textfield viewmode <#if field.control.params.containerStyleClass??>${field.control.params.containerStyleClass}</#if>">
    <div class="label-div">
        <label for="${fieldHtmlId}">${field.label?html}:</label>
    </div>
    <div class="container">
        <input type="hidden" name="${field.name}" id="${fieldHtmlId}" value=""/>
        <div class="buttons-div"><@formLib.renderFieldHelp field=field /></div>
        <div class="value-div">
            <#if field.control.params.text??>
                ${field.control.params.text?html}
            <#elseif field.control.params.textCode??>
                ${msg(field.control.params.textCode)}
            </#if>   
        </div>
    </div>
</div>
<div class="clear"></div>