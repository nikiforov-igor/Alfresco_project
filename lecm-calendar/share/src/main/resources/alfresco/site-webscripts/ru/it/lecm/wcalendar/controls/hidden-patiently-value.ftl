<#assign fieldValue = field.control.params.value>
<#if form.mode == "edit" || form.mode == "create">
   <input type="hidden" name="${field.name}"
          <#if field.control.params.value?is_number>value="${fieldValue?c}"<#else>value="${fieldValue?html}"</#if> />
</#if>