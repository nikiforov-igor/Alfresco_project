<#assign isTrue=false>
<#if field.value??>
	<#if field.value?is_boolean>
		<#assign isTrue=field.value>
	<#elseif field.value?is_string && field.value == "true">
		<#assign isTrue=true>
	</#if>
</#if>

<#if field.control.params.isTrue??>
	<#if field.control.params.isTrue?is_boolean>
		<#assign isTrue=field.control.params.isTrue>
	<#elseif field.control.params.isTrue?is_string && field.control.params.isTrue == "true">
		<#assign isTrue=true>
	</#if>
</#if>

<script type="text/javascript">//<![CDATA[
function Absence_CheckboxChanged(skipFiring) {

	var myID = "${fieldHtmlId}";

	var unlimitedCheckbox = YAHOO.util.Dom.get(myID);
	unlimitedCheckbox.value = unlimitedCheckbox.checked;

	var IDElements = myID.split("_");
	IDElements.splice(-1, 1);
	var commonID = IDElements.join("_");

	var endInputDate = YAHOO.util.Dom.get(commonID + "_end-cntrl-date");
	var endInputIcon = YAHOO.util.Dom.get(commonID + "_end-cntrl-icon");
	var endInputHidden = YAHOO.util.Dom.get(commonID + "_end");

	if (unlimitedCheckbox.checked) {
		var today = new Date();
		today.setHours(23, 59, 59, 0);

		endInputDate.setAttribute("readonly", true);
		endInputDate.removeAttribute("value");
		endInputHidden.setAttribute("value", Alfresco.util.toISO8601(today));
		endInputIcon.style.display = "none";

		YAHOO.util.UserAction.keyup(endInputHidden);
	} else {
		endInputDate.removeAttribute("readonly");
		endInputDate.removeAttribute("value");
		endInputHidden.removeAttribute("value");
		endInputIcon.style.display = "block";

		YAHOO.util.UserAction.keyup(endInputHidden);
	}
	
	if (!skipFiring) {
		YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
	}
}
<#if isTrue>
(function() {
	var myID = "${fieldHtmlId}";
	var IDElements = myID.split("_");
	IDElements.splice(-3, 3);
	var commonID = IDElements.join("_");
	var formID = commonID + "-form"
	YAHOO.util.Event.onContentReady(formID, Absence_CheckboxChanged, true);
})();
</#if>
//]]></script>

<div class="form-field">
   <#if form.mode == "view">
      <div class="viewmode-field">
         <span class="viewmode-label">${field.label?html}:</span>
         <span class="viewmode-value"><#if isTrue>${msg("form.control.checkbox.yes")}<#else>${msg("form.control.checkbox.no")}</#if></span>
      </div>
   <#else>
		<label for="${fieldHtmlId}">&nbsp;</label>
      <input class="formsCheckBox" id="${fieldHtmlId}" type="checkbox" tabindex="0"  name="${field.name}" <#if field.description??>title="${field.description}"</#if>
             <#if isTrue> value="true" checked="checked"</#if> 
             <#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if> 
             <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
             <#if field.control.params.style??>style="${field.control.params.style}"</#if> 
             onchange='Absence_CheckboxChanged()' />
      <label for="${fieldHtmlId}" class="checkbox">${field.label?html}</label>
      <@formLib.renderFieldHelp field=field />
   </#if>
</div>
<div class="form-field" id="error-container"> </div>
