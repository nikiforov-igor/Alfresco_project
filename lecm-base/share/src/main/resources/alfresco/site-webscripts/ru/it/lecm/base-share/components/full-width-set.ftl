<#list set.children as item>
   <#if item.kind != "set">
      <div class="full-width">
	      <@formLib.renderField field=form.fields[item.id]/>
      </div>
   </#if>
</#list>
<p style="clear: both; visible:hidden; line-height: 0px; padding: 0; margin: 0;">&nbsp;</p>
