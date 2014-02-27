<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign controlId = fieldHtmlId + "-cntrl">
<script type="text/javascript">
		function getOptions() {
			var optsString = '';
			
			var certs = CryptoApplet.getCerts();
			var selectBox = document.getElementById('${fieldHtmlId}');
			for (var i = 0; i < certs.length; i++) {
				var container = certs[i].getContainer();
				var CN = certs[i].getOwner();
				var opt = new Option(container + ': ' + CN, container);
				if(container == '${field.value?string}'){
					opt.selected = true;
				}
				selectBox.add(opt);
			}
		}
		YAHOO.util.Event.onAvailable('${controlId}', getOptions);
</script>
<#assign fieldValue=field.value>
<div id="${controlId}" class="form-field">
 <label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
 <select id="${fieldHtmlId}" name="${field.name}" tabindex="0"
       <#if field.description??>title="${field.description}"</#if>
       <#if field.control.params.size??>size="${field.control.params.size}"</#if> 
       <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
       <#if field.control.params.style??>style="${field.control.params.style}"</#if>>
 </select>
 <@formLib.renderFieldHelp field=field />
</div>