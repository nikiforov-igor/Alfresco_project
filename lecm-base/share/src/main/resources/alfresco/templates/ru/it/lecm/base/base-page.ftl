<#import "/org/alfresco/include/alfresco-template.ftl" as aft />
<#-- Макрос, рисующий шаблон странички с region-ами header, title, menu, footer, toolbar, content
Разметка для content задается nested контентом
-->
<#macro basePage>
	<@aft.templateBody>
	<div id="alf-hd">
		<@region id="header" scope="global"/>
    <@region id="title" scope="template"/>
	</div>
	<div id="bd">
		<div class="yui-t1" id="lecm-page">
			<div id="yui-main">
				<div class="yui-b" id="lecm-content">
					<@region id="toolbar" scope="template"/>
					<#nested>
				</div>
			</div>
			<div class="yui-b" id="lecm-menu">
				<@region id="menu" scope="template"/>
			</div>
		</div>
	</div>
	</@>
	<@aft.templateFooter>
	<div id="alf-ft">
		<@region id="footer" scope="global"/>
	</div>
	</@>
</#macro>