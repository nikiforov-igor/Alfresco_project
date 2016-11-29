<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>


<#assign id = args.htmlid>
<#assign jsid = args.htmlid?js_string>

<@markup id="css">
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/yui/treeview/assets/skins/sam/treeview.css" />
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-orgstructure/orgstructure-arm-tree.css" />
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/main-styles.css" />
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/components/toolbar.css" />
</@>
<@markup id="js">
    <@script type="text/javascript" src="${url.context}/res/components/form/form.js"/>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-form-tabs.js"/>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-toolbar.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-orgstructure/orgstructure-toolbar.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-orgstructure/orgstructure-arm-tree.js"></@script>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-orgstructure/orgstructure-dashlet-arm-tree.js"></@script>
</@>

<#include "/org/alfresco/components/component.head.inc">
<script type="text/javascript">//<![CDATA[
(function () {
    function createObjects() {
        new Alfresco.widget.DashletResizer("${jsid}", "${instance.object.id}");
        new Alfresco.widget.DashletTitleBarActions("${jsid}").setOptions(
                {
                    actions: [
                        {
                            cssClass: "dictionary",
                            linkOnClick: window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_PAGECONTEXT + "orgstructure-dictionary",
                            tooltip: "${msg("dashlet.dictionary.tooltip")?js_string}"
                        },
                        {
                            cssClass: "help",
                            bubbleOnClick: {
                                message: "${msg("dashlet.help")?js_string}"
                            },
                            tooltip: "${msg("dashlet.help.tooltip")?js_string}"
                        }
                    ]
                });

        var orgStructure = new LogicECM.module.OrgStructure.DashletArmTree("${jsid}");
        orgStructure.setOptions({
            minSTermLength: 3,
            bubblingLabel: "dashlet-arm-tree"
        });
        orgStructure.setMessages(${messages});
    }

    YAHOO.util.Event.onDOMReady(createObjects);
})();
//]]></script>
<div class="dashlet orgstructure-dictionary">
    <div class="title">${msg("header")}</div>
    <div class="body scrollableList" id="${id}-paginator" <#if args.height??>style="height: ${args.height}px;"</#if>>
        <@comp.baseToolbar jsid false true false/>
        <#-- Empty results list template -->
        <div id="${id}-default">
            <div class="default-text"><span>${msg("dashlet.default.text")}</span></div>
        </div>
        <div id="${id}-empty">
            <div class="empty"><span>${msg("dashlet.empty.text")}</span></div>
        </div>
        <div id="${id}-orgstructure-tree" class="orgstructure-tree"></div>
    </div>
</div>






