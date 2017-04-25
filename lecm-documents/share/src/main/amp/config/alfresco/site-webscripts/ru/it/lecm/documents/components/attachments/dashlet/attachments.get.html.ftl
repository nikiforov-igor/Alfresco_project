<#if categories??>
    <#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
    <#assign id = args.htmlid>
    <#assign containerId = id + "-container">

<div class="dashlet document bordered">
    <div class="title dashlet-title">
        <span>${msg("label.title")}</span>
        <span class="lecm-dashlet-actions">
	        <a id="${id}-action-expand" href="javascript:void(0);" class="expand attachments-expand"
               title="${msg("dashlet.expand.tooltip")}">&nbsp</a>
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
                function init() {
                    LogicECM.module.Base.Util.loadScripts([
                            'scripts/lecm-base/components/advsearch.js',
                            'scripts/lecm-base/components/lecm-datagrid.js',
                            'scripts/components/document-attachments-dashlet-datagrid.js'], create);
                }

                function create() {
                    var datagrid = null;
                    var select = null;

                    var readOnlyCategories = {
                        <#if categories??>
                            <#list categories as category>
                                "${category.nodeRef}": ${category.isReadOnly?string}<#if category_has_next>,</#if>
                            </#list>
                        </#if>};

                    var lockedAttacments = [
                        <#if lockedAttacments??>
                            <#list lockedAttacments as lockedAttacment>
                                "${lockedAttacment}"<#if lockedAttacment_has_next>,</#if>
                            </#list>
                        </#if>];

                    select = Dom.get("${id}-attachment-categories");
                    var selectValue = "";
                    if (select && select.value) {
                        selectValue = select.value;
                    }
                    YAHOO.util.Event.on("${id}-attachment-categories", "change", onCategoriesSelectChange, this, true);

                    datagrid = new LogicECM.DocumentAttachmentsDashlet.DataGrid('${containerId}').setOptions({
                        usePagination: false,
                        documentRef: "${nodeRef?string}",
                        baseDocAssocName: "${baseDocAssocName!""}",
                        showBaseDocAttachmentsBottom: ${(showBaseDocAttachmentsBottom!"false")?string},
                        showExtendSearchBlock: false,
                        actions: [
                            <#if hasViewAttachmentPerm>
                                {
                                    type: "datagrid-action-link-${containerId}",
                                    id: "onActionViewContent",
                                    permission: "edit",
                                    label: "${msg("actions.view-content")}"
                                }<#if hasAddNewVersionAttachmentPerm || hasDeleteAttachmentPerm || hasDeleteOwnAttachmentPerm>,</#if>
                            </#if>
                            <#if hasAddNewVersionAttachmentPerm>
                                {
                                    type: "datagrid-action-link-${containerId}",
                                    id: "onActionUploadNewVersion",
                                    permission: "edit",
                                    label: "${msg("actions.upload-new-version")}",
                                    evaluator: function (rowData) {
                                        return !readOnlyCategories[select.value] && (lockedAttacments.indexOf(rowData.nodeRef) == -1);
                                    }
                                }<#if hasDeleteAttachmentPerm || hasDeleteOwnAttachmentPerm>,</#if>
                            </#if>
                            <#if hasDeleteAttachmentPerm || hasDeleteOwnAttachmentPerm>
                                {
                                    type: "datagrid-action-link-${containerId}",
                                    id: "onActionDelete",
                                    permission: "delete",
                                    label: "${msg("actions.delete-row")}",
                                    confirmFunction: function () {
                                        YAHOO.Bubbling.fire("fileDeleted", {});
                                    },
                                    evaluator: function (rowData) {
                                        return !readOnlyCategories[select.value] && rowData.createdBy.value == "${user.name}";
                                    }
                                }
                            </#if>],
                        datagridMeta: {
                            itemType: "cm:content",
                            useFilterByOrg: false,
                            datagridFormId: "attachments-dashlet-table",
                            createFormId: "",
                            nodeRef: selectValue,
                            actionsConfig: {
                                fullDelete: true
                            }
                        },
                        dataSource: "lecm/documents/attachments/datagrid",
                        bubblingLabel: "${containerId}",

                        allowCreate: false,
                        showActionColumn: true,
                        showCheckboxColumn: false
                    }).setMessages(${messages});

                    datagrid.draw();

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
                                    bubblingLabel: datagrid.options.bubblingLabel
                                });
                    }
                }

                YAHOO.util.Event.onDOMReady(init);

            })();
            //]]></script>
        </@grid.datagrid>
    </div>
</div>
</#if>
