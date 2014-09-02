<#--<@script type="text/javascript" src="${url.context}/res/scripts/lecm-calendar/absence/date-interval-validation.js"/>-->

<script type="text/javascript">//<![CDATA[
if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}
LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Absence = LogicECM.module.WCalendar.Absence || {};
LogicECM.module.WCalendar.Const = LogicECM.module.WCalendar.Const || {};
(function () {
	var absenceContainer = ${absenceContainer};
	LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER = LogicECM.module.WCalendar.Absence.ABSENCE_CONTAINER || absenceContainer;
	LogicECM.module.WCalendar.Absence.ABSENCE_PROFILE_LABEL = "absenceProfileDatagrid" + YAHOO.util.Dom.generateId();
})();
//]]></script>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<@bpage.basePageSimple>
	<@region id="absence-profile" scope="template"/>
</@bpage.basePageSimple>

