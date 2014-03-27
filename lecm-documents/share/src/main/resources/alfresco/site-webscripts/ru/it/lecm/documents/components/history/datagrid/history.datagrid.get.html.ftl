<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign gridId = args.htmlid/>
<#assign controlId = gridId + "-cntrl">
<#assign containerId = gridId + "-container">
<#assign nodeRef = args.nodeRef/>
<#assign showSecondaryCheckBox = false/>
<#assign dataSource = args.dataSource/>


<div class="form-field with-grid history-grid" id="bjHistory-${controlId}">

<#if args.showSecondaryCheckBox?? && args.showSecondaryCheckBox == "true">
    <#assign showSecondaryCheckBox = true>
</#if>

<#if showSecondaryCheckBox>
	<div class="show-archive-cb-div" style="text-align: right;">
	    <input type="checkbox" class="formsCheckBox" id="${containerId}-cbShowSecondary" onChange="YAHOO.Bubbling.fire('showSecondaryClicked', null)">
	    <label class="checkbox" for="${containerId}-cbShowSecondary">${msg("logicecm.base.show-secondary.label")}</label>
	</div>
</#if>

<#--uncomment to display "Show Inactive" checkbox-->
<#--<@grid.datagrid containerId true gridId+"form" showCheckBox>-->
<@grid.datagrid containerId true gridId+"form" false>
    <script type="text/javascript">//<![CDATA[
    (function () {
        YAHOO.util.Event.onDOMReady(function (){
            var datagrid = new LogicECM.module.BusinessJournal.DataGrid('${containerId}').setOptions({
                usePagination: true,
                disableDynamicPagination: true,
                pageSize: 10,
                showExtendSearchBlock: true,
                datagridMeta: {
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
                attributeForShow:"lecm-busjournal:bjRecord-date"
            }).setMessages(${messages});

            datagrid.draw();
        });

    })();
    //]]></script>

</@grid.datagrid>
</div>