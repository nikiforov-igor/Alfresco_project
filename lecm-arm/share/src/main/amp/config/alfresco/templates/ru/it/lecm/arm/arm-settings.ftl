<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader "transitional">
	<@script type="text/javascript" src="${url.context}/res/jquery/jquery-1.6.2.js"/>
    <@script type="text/javascript" src="/scripts/lecm-arm/controls/arm-settings-fields-control.js"/>
    <@script type="text/javascript" src="/scripts/lecm-arm/constraints/arm-statuses-child-rule-validator.js"/>
<#--загрузка base-utils.js вынесена в base-share-config-custom.xml-->
    <#--<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/base-utils.js"></@script>-->
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-validation.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js"/>

    <@link rel="stylesheet" type="text/css" href="${url.context}/res/yui/treeview/assets/skins/sam/treeview.css"/>
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-arm/lecm-arm.css" />
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/components/base-menu/base-menu.css" />
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-arm/lecm-arm-menu.css" />
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/data-lists/toolbar.css" />
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-dictionary/dictionary-toolbar.css" />
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-dictionary/dictionary-tree.css" />
    <#include "/org/alfresco/components/form/form.dependencies.inc">
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<#import "/ru/it/lecm/base-share/components/2-panels-with-splitter.ftl" as panels/>

<@bpage.basePage showToolbar=false>
	<div class="yui-t1" id="lecm-dictionary">
            <@panels.twoPanels>
                <@region id="toolbar" scope="template" />
                <@region id="main-form" scope="template" />
            </@panels.twoPanels>
	</div>
        <@region id="dependencies" scope="template" />
</@bpage.basePage>