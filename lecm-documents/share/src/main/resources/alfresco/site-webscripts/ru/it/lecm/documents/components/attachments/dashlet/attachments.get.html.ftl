<#if categories??>
    <#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
    <#assign id = args.htmlid>
    <#assign containerId = id + "-container">

<div class="dashlet document bordered">
    <div class="title dashlet-title">
        <span>${msg("label.title")}</span>
	    <span class="lecm-dashlet-actions">
	        <a id="${id}-action-expand" href="javascript:void(0);" onclick="documentAttachmentsComponent.onExpand()" class="expand" title="${msg("dashlet.expand.tooltip")}">&nbsp</a>
	    </span>
	    <span class="lecm-dashlet-actions-right">
	        <select id="${id}-attachment-categories" class="attachment-categories-select">
                <#if categories??>
                    <#list categories as category>
                        <option value="${category.nodeRef}">${category.name}</option>
                    </#list>
                </#if>
            </select>
	    </span>
    </div>
    <div class="body scrollableList dashlet-body" id="${id}_results">
        <@grid.datagrid containerId false>
            <script type="text/javascript">//<![CDATA[
            (function () {
                var datagrid = null;
                var select = null;

	            var readOnlyCategories = {
		            <#if categories??>
			            <#list categories as category>
				            "${category.nodeRef}": ${category.isReadOnly?string}<#if category_has_next>,</#if>
			            </#list>
		            </#if>
	            }

                YAHOO.util.Event.onDOMReady(function (){
                    select = Dom.get("${id}-attachment-categories");
                    var selectValue = "";
                    if (select != null && select.value != null) {
                        selectValue = select.value;
                    }
                    YAHOO.util.Event.on("${id}-attachment-categories", "change", onCategoriesSelectChange, this, true);

                    datagrid = new LogicECM.DocumentAttachments.DataGrid('${containerId}').setOptions({
                        usePagination: false,
                        showExtendSearchBlock: false,
                        actions: [
	                        <#if hasViewAttachmentPerm>
	                            {
	                                type: "datagrid-action-link-${containerId}",
	                                id: "onActionViewContent",
	                                permission: "edit",
	                                label: "${msg("actions.view-content")}"
	                            }<#if hasAddNewVersionAttachmentPerm || hasDeleteAttachmentPerm>,</#if>
	                        </#if>
	                        <#if hasAddNewVersionAttachmentPerm>
	                            {
	                                type: "datagrid-action-link-${containerId}",
	                                id: "onActionUploadNewVersion",
	                                permission: "edit",
	                                label: "${msg("actions.upload-new-version")}",
		                            evaluator: function (rowData) {
			                            return !readOnlyCategories[select.value];
		                            }
	                            }<#if hasDeleteAttachmentPerm>,</#if>
	                        </#if>
	                        <#if hasDeleteAttachmentPerm>
	                            {
	                                type: "datagrid-action-link-${containerId}",
	                                id: "onActionDelete",
	                                permission: "delete",
	                                label: "${msg("actions.delete-row")}",
	                                confirmFunction: function () {
	                                    YAHOO.Bubbling.fire("fileDeleted", {});
	                                },
		                            evaluator: function (rowData) {
			                            return !readOnlyCategories[select.value];
		                            }
	                            }
	                        </#if>
                        ],
                        datagridMeta: {
                            itemType: "cm:content",
                            datagridFormId: "attachments-dashlet-table",
                            createFormId: "",
                            nodeRef: selectValue,
                            actionsConfig: {
                                fullDelete: true
                            }
                        },
                        dataSource:"lecm/search",
                        bubblingLabel: "${containerId}",

                        allowCreate: false,
                        showActionColumn: true,
                        showCheckboxColumn: false
                    }).setMessages(${messages});

                    datagrid.draw();
                });

                function onCategoriesSelectChange() {
                    var selectValue = "";
                    if (select != null && select.value != null) {
                        selectValue = select.value;
                    }
                    var meta = datagrid.datagridMeta;
                    meta.nodeRef = selectValue;

                    YAHOO.Bubbling.fire("activeGridChanged",
                            {
                                datagridMeta: meta,
                                bubblingLabel:datagrid.options.bubblingLabel
                            });
                }

            })();
            //]]></script>
        </@grid.datagrid>
    </div>
</div>
</#if>