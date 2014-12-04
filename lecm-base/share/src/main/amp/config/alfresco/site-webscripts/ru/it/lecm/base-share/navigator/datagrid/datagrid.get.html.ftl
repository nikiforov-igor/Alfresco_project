<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>
<#assign bubblingLabel = "navigator-datagrid-bubbling">

<#assign isRoot = false>
<#if args.root?? && args.root == "true">
    <#assign isRoot = true>
</#if>

<@grid.datagrid id=id showViewForm=false>
<script type="text/javascript">//<![CDATA[
(function(){

	function createDatagrid() {
        var datagrid = new LogicECM.module.Base.DataGrid("${id}").setOptions({
            usePagination: false,
            showExtendSearchBlock: false,
            actions: [
                {
                    type: "datagrid-action-link-${bubblingLabel}",
                    id: "onActionViewDocument",
                    permission: "edit",
                    label: "${msg("actions.open-row")}"
                }
            ],
            datagridMeta: {
                itemType: "cm:content",
                useChildQuery: <#if isRoot >false<#else>true</#if>,
                datagridFormId: "file-navigator",
                <#if !isRoot && args.armSelectedNodeRef?? && args.armSelectedNodeRef != "">
                    nodeRef: "${args.armSelectedNodeRef}",
                </#if>
                actionsConfig: {
                    fullDelete: "true"
                }
                <#if isRoot>
                    ,
                    searchConfig: {
                        filter: "PATH: \"/app:company_home/*\""
                    }
                </#if>
            },
            bubblingLabel: "${bubblingLabel}",
            allowCreate: false,
            showActionColumn: true,
            showCheckboxColumn: false,
            attributeForShow:"cm:name"
        }).setMessages(${messages});
        datagrid.onActionViewDocument = function(item) {
            document.location.href = Alfresco.constants.URL_PAGECONTEXT + "document-details?nodeRef=" + item.nodeRef;
        }
        datagrid.draw();
	}

	function init() {
		LogicECM.module.Base.Util.loadResources([
            'scripts/lecm-base/components/advsearch.js',
            'scripts/lecm-base/components/lecm-datagrid.js'
		], [], createDatagrid);
	}

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>
</@grid.datagrid>
