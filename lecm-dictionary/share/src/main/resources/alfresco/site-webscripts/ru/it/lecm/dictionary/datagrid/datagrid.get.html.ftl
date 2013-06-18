<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>
<#assign bubblingLabel = "dictionaries-datagrid">

<@grid.datagrid id=id showViewForm=true>
<script type="text/javascript">//<![CDATA[
function createDatagrid(attributeForShow) {
    LogicECM.module.Dictionary.DataGrid.prototype.onDelete =
            function DataGridActions_onDelete(p_items, owner, actionsConfig, fnDeleteComplete, fnPrompt) {
                var me = this,
                        items = YAHOO.lang.isArray(p_items) ? p_items : [p_items];

                var itemNames = [];
                var propToShow = "prop_cm_name";
                for (var j = 0, jj = this.datagridColumns.length; j < jj; j++) {
                    var column = this.datagridColumns[j];
                    if (me.options.attributeForShow != null && column.name == me.options.attributeForShow) {
                        propToShow = column.formsName;
                        break;
                    }
                }
                for (var k = 0; k < items.length; k++) {
                    if (items[k] && items[k].itemData && items[k].itemData[propToShow]) {
                        itemNames.push("'" + items[k].itemData[propToShow].displayValue + "'");
                    }
                }

                var itemsString = itemNames.join(", ");
                var fnActionDeleteConfirm = function DataGridActions__onActionDelete_confirm(items) {
                    var nodeRefs = [];
                    for (var i = 0, ii = items.length; i < ii; i++) {
                        nodeRefs.push(items[i].nodeRef);
                    }
                    var query = "";
                    if (actionsConfig) {
                        var fullDelete = actionsConfig.fullDelete;
                        if (fullDelete != null) {
                            query = query + "full=" + fullDelete;
                        }
                        var trash = actionsConfig.trash;
                        if (fullDelete != null && trash != null) {
                            query = query + "&trash=" + trash;
                        }
                    }
                    this.modules.actions.genericAction(
                            {
                                success: {
                                    event: {
                                        name: "dataItemsDeleted",
                                        obj: {
                                            items: items,
                                            bubblingLabel: me.options.bubblingLabel
                                        }
                                    },
                                    message: this.msg((actionsConfig && actionsConfig.successMessage) ? actionsConfig.successMessage : "message.delete.success", items.length),
                                    callback: {
                                        fn: fnDeleteComplete
                                    }
                                },
                                failure: {
                                    message: this.msg("message.delete.failure")
                                },
                                webscript: {
                                    method: Alfresco.util.Ajax.DELETE,
                                    name: "delete",
                                    queryString: query
                                },
                                config: {
                                    requestContentType: Alfresco.util.Ajax.JSON,
                                    dataObj: {
                                        nodeRefs: nodeRefs
                                    }
                                }
                            });
                };

                if (!fnPrompt) {
                    var nodeRefs = [];
                    for (var i = 0, ii = items.length; i < ii; i++) {
                        nodeRefs.push(items[i].nodeRef);
                    }
                    Alfresco.util.Ajax.jsonPost(
                            {
                                url: Alfresco.constants.PROXY_URI + "lecm/dictionary/action/isDependents",
                                dataObj: {
                                    nodeRefs: nodeRefs
                                },
                                successCallback: {
                                    fn: function (response) {
                                        var message = "";
                                        if (response.json.isDependents) {
                                            message = this.msg("message.warning") + "\n";
                                        }
                                        message = (items.length > 1) ? message + this.msg("message.confirm.delete.group.description", items.length) : message + this.msg("message.confirm.delete.description", itemsString),
                                                fnPrompt = function onDelete_Prompt(fnAfterPrompt) {
                                                    Alfresco.util.PopupManager.displayPrompt(
                                                            {
                                                                title: this.msg("message.confirm.delete.title", items.length),
                                                                text: message,
                                                                buttons: [
                                                                    {
                                                                        text: this.msg("button.delete"),
                                                                        handler: function DataGridActions__onActionDelete_delete() {
                                                                            this.destroy();
                                                                            me.selectItems("selectNone");
                                                                            fnAfterPrompt.call(me, items);
                                                                        }
                                                                    },
                                                                    {
                                                                        text: this.msg("button.cancel"),
                                                                        handler: function DataGridActions__onActionDelete_cancel() {
                                                                            this.destroy();
                                                                        },
                                                                        isDefault: true
                                                                    }
                                                                ]
                                                            });
                                                }
                                        fnPrompt.call(this, fnActionDeleteConfirm);
                                    },
                                    scope: this
                                },
                                failureCallback: {
                                    fn: function (response) {
                                        Alfresco.util.PopupManager.displayMessage(
                                                {
                                                    text: this.msg("message.error.contains.links")
                                                });
                                    },
                                    scope: this
                                }
                            });
                }
            };
	new LogicECM.module.Dictionary.DataGrid('${id}', attributeForShow).setOptions(
			{
                bubblingLabel:"${bubblingLabel}",
				usePagination:true,
				showExtendSearchBlock:false,
                actions: [
                    {
                        type:"datagrid-action-link-${bubblingLabel}",
                        id:"onActionEdit",
                        permission:"edit",
                        label:"${msg("actions.edit")}"
                    },
                    {
                        type:"datagrid-action-link-${bubblingLabel}",
                        id:"onActionVersion",
                        permission:"edit",
                        label:"${msg("actions.version")}"
                    },
                    {
                        type:"datagrid-action-link-${bubblingLabel}",
                        id:"onActionDelete",
                        permission:"delete",
                        label:"${msg("actions.delete-row")}"
                    }
                ]
			}).setMessages(${messages});
}

function loadDictionary() {
	var sUrl = Alfresco.constants.PROXY_URI + "/lecm/dictionary/api/getDictionary?dicName=" + encodeURIComponent("${page.url.args.dic!''}");

	var callback = {
		success:function (oResponse) {
			var oResults = eval("(" + oResponse.responseText + ")");
			if (oResults != null) {
				createDatagrid(oResults.attributeForShow);
			}
		},
		failure:function (oResponse) {
			alert("Справочник не был загружен. Попробуйте обновить страницу.");
		},
		argument:{
		}
	};
	YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
}

function init() {
	loadDictionary();
}

YAHOO.util.Event.onDOMReady(init);
//]]></script>
</@grid.datagrid>
