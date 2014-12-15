<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/components/form/form.dependencies.inc">

<@templateHeader "transitional">
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/data-lists/toolbar.css" group="lecm-controls-editor"/>
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-controls-editor/controls-editor-toolbar.css" group="lecm-controls-editor"/>

	<@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js" group="lecm-controls-editor"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-toolbar.js" group="lecm-controls-editor"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/advsearch.js" group="lecm-controls-editor"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-datagrid.js" group="lecm-controls-editor"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-controls-editor/controls-editor-toolbar.js" group="lecm-controls-editor"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-controls-editor/controls-editor-datagrid.js" group="lecm-controls-editor"/>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage>
	<@region id="datagrid" scope="template" />
<#--
	<@region id="datagrid" scope="template" />
	<@region id="html-upload" scope="template"/>
	<@region id="flash-upload" scope="template"/>
	<@region id="file-upload" scope="template"/>
	<@region id="dnd-upload" scope="template"/>
-->
</@bpage.basePage>
