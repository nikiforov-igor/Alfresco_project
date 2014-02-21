<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader "transitional">
	<@link rel="stylesheet" type="text/css" href="${url.context}/yui/treeview/assets/skins/sam/treeview.css"/>
    <#include "/org/alfresco/components/form/form.get.head.ftl">
    <@script type="text/javascript" src="${page.url.context}/scripts/lecm-arm/arm-filters.js"></@script>
    <@script type="text/javascript" src="${page.url.context}/scripts/lecm-documents/document-list-filters-manager.js"></@script>

    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-documents/documents-list.css" />
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/components/document-metadata-form-edit.css" />
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-arm/lecm-arm-documents.css" />

    <#assign filter = ""/>
    <#assign formId = ""/>
    <#assign isDocListPage = false/>
    <#assign queryFilterId = ""/>

    <script type="text/javascript">//<![CDATA[
        // настройки из repo
        if (typeof LogicECM == "undefined" || !LogicECM) {
            var LogicECM = {};
        }

        LogicECM.module = LogicECM.module || {};

        LogicECM.module.ARM = LogicECM.module.ARM || {};

        LogicECM.module.ARM.SETTINGS =
            <#if settings?? >
            ${settings}
            <#else>
            {}
            </#if>;

        LogicECM.module.ARM.SETTINGS.ARM_CODE = "${page.url.args.code!''}";

        (function(){
            function initArmResizer() {
                var resizer = new LogicECM.module.Base.Resizer('ArmResizer');
                resizer.setOptions({
                    initialWidth: 300
                });
            }

            YAHOO.util.Event.onDOMReady(initArmResizer);
        })();
    //]]></script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<div id="no_menu_page" class="sticky-wrapper">
<@bpage.basePage showHeader=true showTitle=true showToolbar=hasPermission showMenu=false>
        <div class="yui-t1" id="arm-with-tree">
            <div id="yui-main-2">
                <div class="yui-b" style="margin-left: 0" id="alf-content">
                    <@region id="current-filters" scope="template" />
                    <@region id="documents-grid" scope="template" />
                </div>
            </div>
            <div id="alf-filters" class="yui-u first column1 tree">
                <@region id="documents-tree" scope="template" />
            </div>
        </div>
    <@region id="dependencies" scope="template" />
</@bpage.basePage>
</div>