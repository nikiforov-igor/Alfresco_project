// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};


/**
 * LogicECM Dictionary module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Orgstructure
 */
LogicECM.module.Orgstructure = LogicECM.module.Orgstructure || {};

(function () {

    LogicECM.module.Orgstructure.WorkForceDataGrid = function (containerId) {
        return LogicECM.module.Orgstructure.WorkForceDataGrid.superclass.constructor.call(this, containerId);
    };

    /**
     * Extend from LogicECM.module.Base.WorkForceDataGrid
     */
    YAHOO.lang.extend(LogicECM.module.Orgstructure.WorkForceDataGrid, LogicECM.module.Base.DataGrid);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.Orgstructure.WorkForceDataGrid.prototype, {
        onActionEmployeeAdd: function DataGridActions_onActionEmployeeAdd(p_item, owner, actionsConfig, fnCallback) {
            var me = this;
            var metaData = {
                itemType: "lecm-orgstr:employee-link",
                nodeRef: p_item.nodeRef
            };

            var onAddCallback = function (employeeRef) {
                // Reload the node's metadata
                YAHOO.Bubbling.fire("datagridRefresh",
                    {
                        bubblingLabel: me.options.bubblingLabel
                    });
                Alfresco.util.PopupManager.displayMessage(
                    {
                        text: this.msg("message.employee.add.success")
                    });
            }.bind(me);

            this.showCreateDialog(metaData, onAddCallback);

        },
        onActionEmployeeDelete: function DataGridActions_onActionEmployeeDelete(p_item, owner, actionsConfig, fnDeleteComplete) {
            var me = this;

	        var sUrlWorkGroup = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getWorkGroupProperties?nodeRef=" + this.datagridMeta.nodeRef;
	        var callbackWorkGroup = {
		        success: function (oResponse) {
			        var oResultWorkGroup = eval("(" + oResponse.responseText + ")");
			        if (oResultWorkGroup) {
						var workgroupShortName = oResultWorkGroup.shortName;

				        var staffRow = p_item;
				        // Получаем для трудового ресурса (участника раб. группы) ссылку на сотрудника
				        var sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getStaffEmployeeLink?nodeRef=" + staffRow.nodeRef;
				        var callback = {
					        success: function (oResponse) {
						        var oResult = eval("(" + oResponse.responseText + ")");
						        if (oResult) {
							        var onPrompt = function (fnAfterPrompt) {
								        Alfresco.util.PopupManager.displayPrompt(
									        {
										        title: this.msg("message.employee.role.delete.title"),
										        text: this.msg("message.employee.role.delete.prompt",
											        staffRow.itemData["assoc_lecm-orgstr_element-member-employee-assoc"].displayValue,
											        workgroupShortName),
										        buttons: [
											        {
												        text: this.msg("button.employee.remove"),
												        handler: function DataGridActions__onActionDelete_delete() {
													        this.destroy();
													        fnAfterPrompt.call(me, [oResult]);
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
							        };
							        var fnDeleteComplete = function () {
								        // Reload the node's metadata
								        Alfresco.util.Ajax.jsonPost(
									        {
										        url: Alfresco.constants.PROXY_URI + "lecm/base/item/node/" + new Alfresco.util.NodeRef(p_item.nodeRef).uri,
										        dataObj: this._buildDataGridParams(),
										        successCallback: {
											        fn: function DataGrid_onActionEdit_refreshSuccess(response) {
												        // Fire "itemUpdated" event
												        YAHOO.Bubbling.fire("dataItemUpdated",
													        {
														        item: response.json.item,
														        bubblingLabel: me.options.bubblingLabel
													        });
											        },
											        scope: this
										        },
										        failureCallback: {
											        fn: function DataGrid_onActionEdit_refreshFailure(response) {
												        Alfresco.util.PopupManager.displayMessage(
													        {
														        text: this.msg("message.details.failure")
													        });
											        },
											        scope: this
										        }
									        });
							        }.bind(me);

							        me.onDelete([oResult], owner, {fullDelete: true, trash: false, successMessage: "message.employee.role.delete.success"}, fnDeleteComplete, onPrompt);
						        } else {
							        Alfresco.util.PopupManager.displayMessage(
								        {
									        text: this.msg("message.employee.role.delete.failure")
								        });
						        }
					        },
					        failure: function (oResponse) {
						        Alfresco.util.PopupManager.displayMessage(
							        {
								        text: this.msg("message.employee.role.delete.failure")
							        });
					        }
				        };
				        YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
			        }
		        },
		        failure: function (oResponse) {
			        Alfresco.util.PopupManager.displayMessage(
				        {
					        text: this.msg("message.employee.role.delete.failure")
				        });
		        }
	        };
	        YAHOO.util.Connect.asyncRequest('GET', sUrlWorkGroup, callbackWorkGroup);
        }
    }, true);
})();
