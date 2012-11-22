<#import "/org/alfresco/include/alfresco-template.ftl" as aft />
<#-- Макрос, рисующий шаблон странички с region-ами header, title, menu, footer, toolbar, content
Разметка для content задается nested контентом
Параметры:
showHeader - рисовать блок header
showTitle - рисовать блок title
По умолчанию - все блоки рисуются
-->
<#macro basePage showHeader=true showTitle=true showToolbar=true>
	<@aft.templateBody>
	<div id="alf-hd">
		<#if showHeader>
			<@region id="header" scope="global"/>
		</#if>
		<#if showTitle>
		    <@region id="title" scope="template"/>
		</#if>
	</div>
	<div id="bd" class="yui-skin-lecmTheme">
        <div class="yui-b flat-button" id="lecm-menu">
            <@region id="menu" scope="template"/>
        </div>
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
	<@aft.templateFooter>
	<div id="alf-ft"></div>
	</@>
</#macro>