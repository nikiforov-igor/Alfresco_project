<script type="text/javascript">//<![CDATA[
LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Absence = LogicECM.module.WCalendar.Absence || {};
LogicECM.module.WCalendar.Const = LogicECM.module.WCalendar.Const || {};
(function () {
	var absenceContainer = ${absenceContainer};
	LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER = LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER || absenceContainer;
})();
//]]></script>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePageSimple>
	<@region id="absence-instant" scope="template"/>
</@bpage.basePageSimple>