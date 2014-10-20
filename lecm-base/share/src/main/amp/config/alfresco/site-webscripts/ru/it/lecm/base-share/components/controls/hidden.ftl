<#-- Renders a hidden form field for edit and create modes only -->
<#assign fieldValue = "">
<#if field.control.params.contextProperty??>
    <#if context.properties[field.control.params.contextProperty]??>
        <#assign fieldValue = context.properties[field.control.params.contextProperty]>
    <#elseif args[field.control.params.contextProperty]??>
        <#assign fieldValue = args[field.control.params.contextProperty]>
    </#if>
<#elseif context.properties[field.name]??>
    <#assign fieldValue = context.properties[field.name]>
<#else>
    <#assign fieldValue = field.value>
</#if>

<#if field.control.params.selectedItemsFormArgs??>
    <#assign selectedItemsFormArgs = field.control.params.selectedItemsFormArgs?split(",")>
    <#list selectedItemsFormArgs as selectedItemsFormArg>
        <#if form.arguments[selectedItemsFormArg]??>
            <#if (fieldValue?length > 0)>
                <#assign fieldValue = fieldValue + ","/>
            </#if>
            <#assign fieldValue = fieldValue + form.arguments[selectedItemsFormArg]/>
        </#if>
    </#list>
<#elseif form.arguments[field.name]?has_content>
    <#assign fieldValue = form.arguments[field.name]/>
</#if>

<#if form.mode == "edit" || form.mode == "create">
<input type="hidden" name="${field.name}" id="${fieldHtmlId}-added"
       <#if field.value?is_number>value="${fieldValue?c}"<#else>value="${fieldValue?html}"</#if> />
</#if>