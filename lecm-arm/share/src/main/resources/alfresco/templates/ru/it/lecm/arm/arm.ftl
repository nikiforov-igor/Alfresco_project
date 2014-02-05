<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader "transitional">
    <#include "/org/alfresco/components/form/form.get.head.ftl">
    <@script type="text/javascript" src="${page.url.context}/scripts/lecm-arm/arm-const.js"></@script>
    <@script type="text/javascript" src="${page.url.context}/scripts/lecm-arm/arm-tree-menu.js"></@script>
    <@script type="text/javascript" src="${page.url.context}/scripts/lecm-documents/document-list-filters-manager.js"></@script>

    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-base/light-blue-bgr.css" />
    <@link rel="stylesheet" type="text/css" href="${url.context}/yui/treeview/assets/skins/sam/treeview.css"/>
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-documents/documents-list.css" />
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/components/document-metadata-form-edit.css" />
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/css/lecm-arm/lecm-arm-documents.css" />

    <#assign filter = ""/>
    <#assign formId = ""/>
    <#assign isDocListPage = false/>
    <#assign queryFilterId = ""/>

    <script type="text/javascript">//<![CDATA[
        function findValueByDotNotation(obj, propertyPath, defaultValue) {
            var value = defaultValue ? defaultValue : null;
            if (propertyPath && obj && obj != "") {
                var currObj = obj;
                var props = propertyPath.split(".");
                for (var i = 0; i < props.length; i++) {
                    currObj = currObj[props[i]];
                    if (typeof currObj == "undefined") {
                        return value;
                    }
                }
                return currObj;
            }
            return value;
        }

        <#if filter == "" && preferences??>
            var PREFERENCE = "${preferenceKey}";
            var preference = findValueByDotNotation(${preferences}, PREFERENCE);
            if (preference != null) {
                    window.location = window.location + <#if isDocListPage>"&"<#else>"?"</#if> + preference;
            } else {
                <#if defaultFilter?? && defaultKey??>
                        window.location = window.location + <#if isDocListPage>"&"<#else>"?"</#if> + "query=" + "${defaultFilter}" + "&formId=" + "${defaultKey}";
                </#if>
            }
        </#if>

        <#if queryFilterId?? && queryFilterId != "" && preferences??>
        </#if>

        // настройки из repo
        LogicECM.module.ARM.SETTINGS =
            <#if settings?? >
            ${settings}
            <#else>
            {}
            </#if>;

        LogicECM.module.ARM.SETTINGS.ARM_CODE = "${page.url.args.code!''}";

        LogicECM.module.ARM.FILTER = "";

        function initArmResizer() {
            var resizer = new LogicECM.module.Base.Resizer('ArmResizer');
            resizer.setOptions({
                initialWidth: 300
            });
        }

        YAHOO.util.Event.onDOMReady(initArmResizer);
    //]]></script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<div id="no_menu_page" class="sticky-wrapper">
<@bpage.basePage showHeader=true showTitle=true showToolbar=hasPermission showMenu=false>
    <#if hasPermission>
        <div class="yui-t1" id="arm-with-tree">
            <div id="yui-main-2">
                <div class="yui-b" style="margin-left: 0" id="alf-content">
                    <@region id="current-filters" scope="template" />
                    <@region id="documents-grid" scope="template" />
                </div>
            </div>
            <div id="alf-filters" class="yui-u first column1">
                <div id="arm-tree" class="ygtv-highlight"></div>
                <script type="text/javascript">//<![CDATA[
                (function () {
                    function initMenu() {
                        new LogicECM.module.ARM.Tree("arm-tree");
                    }

                    YAHOO.util.Event.onDOMReady(initMenu);
                })();
                //]]></script>
            </div>
        </div>
    <#else>
        <@region id="forbidden" scope="template"/>
    </#if>
    <@region id="dependencies" scope="template" />
</@bpage.basePage>
</div>