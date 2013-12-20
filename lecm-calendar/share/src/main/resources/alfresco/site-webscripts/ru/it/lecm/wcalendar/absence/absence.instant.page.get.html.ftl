<#assign id = args.htmlid/>

<script type="text/javascript">//<![CDATA[
	var instantAbsence = new LogicECM.module.WCalendar.Absence.InstantAbsencePage('${id}');
	instantAbsence.setMessages (${messages});
//]]>
</script>

<style>
	.instant-absence-content {
		width: 640px;
		border: 0 !important;
	}

	.instant-absence-content .form-buttons {
		border: 0 !important;
	}
</style>

<div id="${id}-content" class="yui-panel instant-absence-content"></div>
