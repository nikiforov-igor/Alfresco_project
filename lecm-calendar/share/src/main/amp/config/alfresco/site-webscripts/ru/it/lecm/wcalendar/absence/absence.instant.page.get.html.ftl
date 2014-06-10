<#assign id = args.htmlid/>

<script type="text/javascript">//<![CDATA[
	var instantAbsence = new LogicECM.module.WCalendar.Absence.InstantAbsencePage('${id}');
	instantAbsence.setMessages (${messages});
//]]>
</script>

<div id="${id}-content" class="instant-absence-content"></div>
