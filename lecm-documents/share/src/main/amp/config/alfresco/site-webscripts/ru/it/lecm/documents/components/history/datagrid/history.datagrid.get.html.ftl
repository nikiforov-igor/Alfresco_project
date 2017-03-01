<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/lecm-datagrid.js"/>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-business-journal/business-journal-datagrid.js"/>

<#assign gridId = args.htmlid/>
<#assign controlId = gridId + "-cntrl">
<#assign containerId = gridId + "-container">
<#assign nodeRef = args.nodeRef/>
<#assign showSecondaryCheckBox = false/>
<#assign hideCollapseButton = true/>
<#assign dataSource = args.dataSource/>

<script type="text/javascript">
    function hideButton() {
        if(location.hash != "#expanded") {
            YAHOO.util.Dom.setStyle(this, 'display', 'none');
        }
    }
    YAHOO.util.Event.onAvailable("${containerId}-action-collapse", hideButton);
</script>

<#if args.hideCollapseButton?? && args.hideCollapseButton == "false">
    <#assign hideCollapseButton = false>
</#if>

<#if !hideCollapseButton>
    <div class="panel-header">
        <div class="panel-title">${msg("label.title")}</div>
        <div class="lecm-dashlet-actions">
            <a id="${containerId}-action-collapse" class="collapse" title="${msg("btn.collapse")}"></a>
        </div>
    </div>
</#if>


<div class="form-field with-grid history-grid" id="bjHistory-${controlId}">

<#if args.showSecondaryCheckBox?? && args.showSecondaryCheckBox == "true">
    <#assign showSecondaryCheckBox = true>
</#if>

<#if showSecondaryCheckBox>
	<div class="show-archive-cb-div">
	    <input type="checkbox" class="formsCheckBox" id="${containerId}-cbShowSecondary" onChange="YAHOO.Bubbling.fire('showSecondaryClicked', null)">
	    <label class="checkbox" for="${containerId}-cbShowSecondary">${msg("logicecm.base.show-secondary.label")}</label>
	</div>
</#if>

<#--uncomment to display "Show Inactive" checkbox-->
<#--<@grid.datagrid containerId true gridId+"form" showCheckBox>-->
<@grid.datagrid id=containerId showViewForm=true viewFormId="bj-view-node-form" showArchiveCheckBox=false>
    <script type="text/javascript">//<![CDATA[
    (function () {
    	function createDatagrid() {
            var datagrid = new LogicECM.module.BusinessJournal.DataGrid('${containerId}').setOptions({
                usePagination: true,
                disableDynamicPagination: true,
                pageSize: 10,
                showExtendSearchBlock: true,
                datagridMeta: {
                    useFilterByOrg: false,
                    itemType: "lecm-busjournal:bjRecord",
                    datagridFormId: "bjHistory",
                    createFormId: "",
                    nodeRef: "${nodeRef}",
                    sort:"lecm-busjournal:bjRecord-date|false",
                    actionsConfig: {
                        fullDelete: "false"
                    }
                },
                dataSource:"${dataSource}",
                allowCreate: false,
                showActionColumn: false,
                showCheckboxColumn: false,
                bubblingLabel: "${bubblingLabel!"bj-history-records"}",
                attributeForShow:"lecm-busjournal:bjRecord-date",
	            overrideSortingWith: false,
	            useCookieForSort: false
            }).setMessages(${messages});

            datagrid.draw();
        }
    	function init() {
            LogicECM.module.Base.Util.loadScripts([
                'scripts/lecm-base/components/advsearch.js',
                'scripts/lecm-base/components/lecm-datagrid.js',
                'scripts/lecm-business-journal/business-journal-datagrid.js'
			], createDatagrid);
        }

        function initDiagramButton() {
            function showDiagram() {
                Alfresco.Lightbox.show({ src: Alfresco.constants.PROXY_URI + 'lecm/statemachine/editor/diagram?docNodeRef=${nodeRef}&type=current&noCache=' + new Date().getTime()});
            }

            var btn = new YAHOO.widget.Button("${containerId}-show-diagram");
            btn.on('click', showDiagram);

        }
        YAHOO.util.Event.onDOMReady(init);
        YAHOO.util.Event.onAvailable("${containerId}-show-diagram", initDiagramButton);
    })();
    //]]></script>

</@grid.datagrid>
<button type="button" id="${containerId}-show-diagram">${msg("btn.show_on_dagram")}</button>
</div>