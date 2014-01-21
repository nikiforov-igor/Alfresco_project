<#list set.children as item>
   <#if item.kind != "set">
      <div class="full-width">
	      <@formLib.renderField field=form.fields[item.id]/>
      </div>
   </#if>
</#list>