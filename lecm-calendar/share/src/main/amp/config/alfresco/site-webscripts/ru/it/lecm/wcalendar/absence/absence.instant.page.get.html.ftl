<#assign id = args.htmlid/>

<script type="text/javascript">//<![CDATA[

//]]>
</script>

<script type="text/javascript">//<![CDATA[
(function () {
	function init() {
		LogicECM.module.Base.Util.loadScripts([
			'/scripts/lecm-calendar/absence/instant-absence-page.js'
		], createObject);
	}

	function createObject() {
		var instantAbsence = new LogicECM.module.WCalendar.Absence.InstantAbsencePage('${id}');
		instantAbsence.setMessages (${messages});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div id="${id}-content" class="instant-absence-content"></div>
<div id="${id}-content-cancel" class="instant-absence-content hidden1">
	<span>${msg("message.absence.cancel-absence.info")}</span>
    <span id="${id}-cancelButton" class="yui-button yui-push-button">
       <span class="first-child">
          <button type="button" title="${msg("label.absence.cancel-absence")}">${msg("label.absence.cancel-absence")}</button>
       </span>
    </span>
</div>
