<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<#-- подключить все скрипты необходимые для диалоговых форм -->
<#include "/org/alfresco/components/form/form.dependencies.inc">

<#-- Далее перечисляются стандартные скрипты из Alfresco -->

<script type="text/javascript">//<![CDATA[
LogicECM.module = LogicECM.module || {};
LogicECM.module.Secretary = LogicECM.module.Secretary || {};

LogicECM.module.Secretary.Const = LogicECM.module.Secretary.Const || {
		"nodeRef": "", //nodeRef папки в которой хранятся данные с перечнем делегирования
		"itemType": "", //тип данных который отображается в таблице с перечнем делегирования
	};

LogicECM.module.Deputy = LogicECM.module.Deputy || {};
LogicECM.module.Deputy.Const = LogicECM.module.Deputy.Const || {};

(function () {

	var response = ${response};
	var deputySettings = ${deputySettings};

	LogicECM.module.Secretary.Const.nodeRef = response.nodeRef;
	LogicECM.module.Secretary.Const.itemType = response.itemType;

	LogicECM.module.Deputy.Const.plane = deputySettings.plane;
	LogicECM.module.Deputy.Const.path = deputySettings.path;
	LogicECM.module.Deputy.Const.itemType = deputySettings.itemsType;
	LogicECM.module.Deputy.Const.dictionaryDesc = deputySettings.dictionaryDesc;

})();
//]]>
</script>



<#assign showContent = nativeObject.isEngineer || nativeObject.isBoss/>

<@bpage.basePageSimple>
	<#if showContent>
		<@region id="content" scope="template"/>
	<#else/>
		<@region id="forbidden" scope="template"/>
	</#if>
</@bpage.basePageSimple>
