<#include "/org/alfresco/include/alfresco-template.ftl" />
<#-- Макрос, рисующий шаблон странички с region-ами header, title, menu, footer, toolbar, content
Разметка для content задается nested контентом
Параметры:
showHeader - рисовать блок header
showTitle - рисовать блок title
По умолчанию - все блоки рисуются
-->
<#macro basePage showHeader=true showTitle=true showToolbar=true showMenu=true>
	<@templateBody>
		<@markup id="alf-hd">
			<div id="alf-hd">
				<#if showHeader>
					<@region id="share-header" scope="global" chromeless="true"/>
				</#if>
			</div>
		</@>
		<@markup id="bd">
			<@region id="html-upload" scope="global" chromeless="true" />
			<@region id="flash-upload" scope="global" chromeless="true" />
			<@region id="file-upload" scope="global" chromeless="true" />
			<@region id="dnd-upload" scope="global" chromeless="true"/>
			<@region id="lecm-dnd-upload" scope="global" chromeless="true"/>
			<div id="bd" class="yui-skin-lecmTheme">
				<#if showMenu>
					<div class="yui-b flat-button" id="lecm-menu">
						<@region id="menu" scope="template"/>
					</div>
				</#if>
		</@>
				<div class="yui-t1" id="lecm-page">
					<div id="yui-main">
						<div class="" id="lecm-content">
							<#if showToolbar>
								<@region id="toolbar" scope="template"/>
							</#if>
							<div id="lecm-content-main">
								<#nested>
							</div>
							<div id="lecm-content-ft"></div>
						</div>
					</div>
				</div>
			</div>
	</@>
	<@templateFooter>
		<@markup id="alf-ft">
			<div id="alf-ft">
				<@region id="footer" scope="global" />
			</div>
		</@>
	</@>
</#macro>