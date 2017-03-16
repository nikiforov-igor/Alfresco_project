<#assign formId=args.htmlid?js_string + "-form">

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
(function() {

	function init() {
        LogicECM.module.Base.Util.loadScripts([], addListeners, ["event-simulate"]);
	}

	function addListeners() {
		YAHOO.util.Event.addListener("${fieldHtmlId}", "click", LogicECM.module.WCalendar.Absence.CheckboxChanged);
		YAHOO.util.Event.onContentReady("${formId}", LogicECM.module.WCalendar.Absence.CheckboxChanged, true);
	}

	LogicECM.module.WCalendar.Absence.CheckboxChanged = function Absence_CheckboxChanged(skipFiring) {
	    Alfresco.util.YUILoaderHelper.require(["event-simulate"], function(){
	    	function focusRecharge(element) {
                var activeElement;
	    		if (element && typeof element.focus === 'function') {
                    activeElement = document.activeElement;
                    element.focus();
                    activeElement.focus();
				}
			}

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
			//today.setHours(23, 59, 59, 0);

				endInputDate.setAttribute("readonly", true);
				endInputDate.removeAttribute("value");
				endInputHidden.setAttribute("value", Alfresco.util.toISO8601(today));
				endInputIcon.style.display = "none";

				YAHOO.util.UserAction.keyup(endInputHidden);
			} else {
				endInputDate.removeAttribute("readonly");
                focusRecharge(endInputDate);
				endInputDate.value = "";
				endInputHidden.removeAttribute("value");
				endInputIcon.style.display = "inline-block";

				YAHOO.util.UserAction.keyup(endInputHidden);
			}

			if (!skipFiring) {
				YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
			}
	    }, this);
	}

	LogicECM.module.WCalendar.Absence.ChangeFormView = function Absence_ChangeFormView() {
		var formatInfosArray = YAHOO.util.Dom.getElementsByClassName('format-info', 'div');
		for (var i = 0; i < formatInfosArray.length; i++) {
			var formatInfo = formatInfosArray[i];
			formatInfo.style.display = "none";
		}
	}
	YAHOO.util.Event.onContentReady("${formId}", LogicECM.module.WCalendar.Absence.ChangeFormView, true);
	//TODO YAHOO.util.Event.addListener("${fieldHtmlId}", "click", LogicECM.module.WCalendar.Absence.CheckboxChanged);

	<#if isTrue>
	//TODO YAHOO.util.Event.onContentReady("${formId}", LogicECM.module.WCalendar.Absence.CheckboxChanged, true);
	</#if>
	YAHOO.util.Event.onDOMReady(init);
})();
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
             <#if field.control.params.style??>style="${field.control.params.style}"</#if> />
      <label for="${fieldHtmlId}" class="checkbox">${field.label?html}</label>
      <@formLib.renderFieldHelp field=field />
   </#if>
</div>
<div class="form-field" id="error-container"> </div>
