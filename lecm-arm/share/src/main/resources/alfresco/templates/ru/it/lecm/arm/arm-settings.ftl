<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader "transitional">
	<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-arm/lecm-arm.css" />
	<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-base/components/base-menu/base-menu.css" />
	<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-arm/lecm-arm-menu.css" />
	<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/data-lists/toolbar.css" />
	<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-dictionary/dictionary-toolbar.css" />
	<@link rel="stylesheet" type="text/css" href="${url.context}/yui/treeview/assets/skins/sam/treeview.css"/>
	<@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-dictionary/dictionary-tree.css" />
	<#include "/org/alfresco/components/form/form.get.head.ftl">

	<script type="text/javascript">//<![CDATA[
		function init() {
			new LogicECM.module.Base.Resizer('DictionaryResizer');
		}

		YAHOO.util.Event.onDOMReady(init);
	//]]></script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage showToolbar=false>
	<div class="yui-t1" id="lecm-dictionary">
	    <div id="yui-main-2">
	        <div class="yui-b" id="alf-content">
				<@region id="toolbar" scope="template" />
	            <@region id="main-form" scope="template" />
	        </div>
	    </div>
	    <div id="alf-filters">
			<@region id="tree" scope="template"/>
	    </div>
	</div>
</@bpage.basePage>