<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<#include "/org/alfresco/components/form/form.dependencies.inc">


<script type="text/javascript">//<![CDATA[
LogicECM.module = LogicECM.module || {};
LogicECM.module.Deputy = LogicECM.module.Deputy || {};
LogicECM.module.Deputy.Const = LogicECM.module.Deputy.Const || {
	"settingsNodeRef": "", //nodeRef папки в которой хранятся данные с перечнем делегирования
};

(function() {
	var response = ${response};

	LogicECM.module.Deputy.Const.settingsNodeRef = response.settingsNodeRef;
})();
//]]>
</script>


<@bpage.basePageSimple>
	<@region id="content" scope="template"/>
</@bpage.basePageSimple>
