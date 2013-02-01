<#assign isTrue=false>
<#if field.value??>
 <#if field.value?is_boolean>
    <#assign isTrue=field.value>
 <#elseif field.value?is_string && field.value == "true">
    <#assign isTrue=true>
 </#if>
</#if>

<script type="text/javascript">//<![CDATA[
function Absence_CheckboxChanged() {

	var myID = "${fieldHtmlId}";

	var unlimitedCheckbox = YAHOO.util.Dom.get(myID);
	unlimitedCheckbox.value = unlimitedCheckbox.checked;

	var IDElements = myID.split("_");
	IDElements.splice(-1, 1);
	var commonID = IDElements.join("_");

	var endInputDate = YAHOO.util.Dom.get(commonID + "_end-cntrl-date");
	//var endInputTime = YAHOO.util.Dom.get(commonID + "_end-cntrl-time");
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

	YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
}
//]]></script>

<div class="form-field">
   <#if form.mode == "view">
      <div class="viewmode-field">
         <span class="viewmode-label">${field.label?html}:</span>
         <span class="viewmode-value"><#if isTrue>${msg("form.control.checkbox.yes")}<#else>${msg("form.control.checkbox.no")}</#if></span>
      </div>
   <#else>
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