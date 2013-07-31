<#if form.mode == "edit">

<script type="text/javascript">//<![CDATA[
(function() {
   new LogicECM.module.ActivitiTransitionRadiobuttons("${fieldHtmlId}").setOptions({
      currentValue: "${field.control.params.options?js_string}",
      hiddenFieldName: "${field.name}"
   }).setMessages(
      ${messages}
   );
})();
//]]></script>

<div class="form-field suggested-actions" id="${fieldHtmlId}">
   <div id="${fieldHtmlId}-buttons">
   </div>
</div>
</#if>
