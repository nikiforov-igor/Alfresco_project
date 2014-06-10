<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<@templateHeader "transitional">
<@script type="text/javascript" src="${url.context}/res/jquery/jquery-1.6.2.js"/>
<#--загрузка base-utils.js вынесена в base-share-config-custom.xml-->
<#--<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/base-utils.js"></@script>-->
<#-- TODO: Подтягивание скриптяшы вынесено в all/datagrid.get.html.ftl -->
<#-- ибо событие на отрисовку стреляет раньше, чем датагрид на него завяжется -->
<#-- 	<@markup id="js">
		<@script type="text/javascript" src="${url.context}/res/scripts/lecm-dictionary/dictionary-all.js"></@script>
	</@> -->
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage>
<#if isEngineer>
	<@region id="datagrid" scope="template" />
<#else>
    <@region id="forbidden" scope="template"/>
</#if>
</@bpage.basePage>