if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Deputy = LogicECM.module.Deputy || {};

LogicECM.module.Deputy.Const = LogicECM.module.Deputy.Const || {};


(function () {
	"use strict";
	LogicECM.module.Deputy.SubjectsGrid = function (containerId) {
		var grid = LogicECM.module.Deputy.SubjectsGrid.superclass.constructor.call(this, containerId);
		YAHOO.Bubbling.on('dataItemCreated', grid.rebuildExpandedRows.bind(grid));
		return grid;
	};

	/**
	 * Extend from LogicECM.module.Base.DataGrid
	 */
	YAHOO.lang.extend(LogicECM.module.Deputy.SubjectsGrid, LogicECM.module.Base.DataGrid);

	/**
	 * Augment prototype with main class implementation, ensuring overwrite is enabled
	 */
	YAHOO.lang.augmentObject(LogicECM.module.Deputy.SubjectsGrid.prototype, {
		onActionCreate: function DataGrid_onActionCreate(meta, callback, successMessage) {
			if (this.editDialogOpening)
				return;
			this.editDialogOpening = true;

			var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
			var templateRequestParams = {
				itemKind: "type",
				itemId: 'lecm-deputy:deputy',
				destination: this.datagridMeta.nodeRef,
				mode: "create",
				formId: LogicECM.module.Deputy.Const.plane ? "plane-create-deputy" : "tree-create-deputy",
				submitType: "json",
				showCancelButton: true,
				showCaption: false
			};

			// Using Forms Service, so always create new instance
			var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails");
			var nodeRef = this.datagridMeta.nodeRef;
			createDetails.setOptions(
					{
						width: "50em",
						templateUrl: templateUrl,
						templateRequestParams: templateRequestParams,
						actionUrl: Alfresco.constants.PROXY_URI + "lecm/deputy/" + Alfresco.util.NodeRef(nodeRef).uri + "/add",
						destroyOnHide: true,
						currentEmployeeRef: this.options.currentEmployeeRef,
						doBeforeDialogShow: {
							fn: function DataGrid_onActionEdit_doBeforeDialogShow(p_form, p_dialog) {
								p_dialog.dialog.setHeader(this.msg(this.options.createFormTitleMsg));
								var contId = p_dialog.id + '-form-container';
								if (meta.itemType) {
									YAHOO.util.Dom.addClass(contId, meta.itemType.replace(":", "_") + "_edit");
								}
								this.editDialogOpening = false;
								p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
							},
							scope: this
						},
						onSuccess: {
							fn: function DataGrid_onActionCreate_success(response) {
								YAHOO.Bubbling.fire("nodeCreated",
										{
											nodeRef: response.json.persistedObject,
											bubblingLabel: this.options.bubblingLabel
										});
								YAHOO.Bubbling.fire("dataItemCreated", // обновить данные в гриде
										{
											nodeRef: response.json.persistedObject,
											bubblingLabel: this.options.bubblingLabel
										});
								Alfresco.util.PopupManager.displayMessage(
										{
											text: this.msg("message.save.success")
										});
								this.editDialogOpening = false;
							},
							scope: this
						},
						onFailure: {
							fn: function DataGrid_onActionCreate_failure(response) {
								LogicECM.module.Base.Util.displayErrorMessageWithDetails(me.msg("logicecm.base.error"), me.msg("message.save.failure"), response.json.message);
								me.editDialogOpening = false;
								this.widgets.cancelButton.set("disabled", false);
							},
							scope: createDetails
						}
					}).show();

		},
		onActionEdit: function (item) {
			if (this.editDialogOpening) {
				return;
			}
			this.editDialogOpening = true;
			var me = this;

			var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
			var templateRequestParams = {
				itemKind: 'node',
				itemId: item.nodeRef,
				mode: 'edit',
				formId: LogicECM.module.Deputy.Const.plane ? "plane-edit-deputy" : "tree-edit-deputy",
				submitType: 'json',
				showCancelButton: true,
				showCaption: false
			};

			// Using Forms Service, so always create new instance
			var editDetails = new Alfresco.module.SimpleDialog(this.id + "-editDetails");
			editDetails.setOptions(
					{
						width: this.options.editFormWidth,
						templateUrl: templateUrl,
						templateRequestParams: templateRequestParams,
						destroyOnHide: true,
						nodeRef: item.nodeRef,
						currentEmployeeRef: this.options.currentEmployeeRef,
						doBeforeDialogShow: {
							fn: function (p_form, p_dialog) {
								var contId = p_dialog.id + "-form-container";
								if (item.type && item.type != "") {
									YAHOO.util.Dom.addClass(contId, item.type.replace(":", "_") + "_edit");
								}
								p_dialog.dialog.setHeader(this.msg(this.options.editFormTitleMsg));
								this.editDialogOpening = false;

								p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
							},
							scope: this
						},
						onSuccess: {
							fn: function DataGrid_onActionEdit_success(response) {
								// Reload the node's metadata
								YAHOO.Bubbling.fire("datagridRefresh",
										{
											bubblingLabel: me.options.bubblingLabel
										});
								Alfresco.util.PopupManager.displayMessage({
									text: this.msg("message.details.success")
								});
								this.editDialogOpening = false;
							},
							scope: this
						},
						onFailure: {
							fn: function DataGrid_onActionEdit_failure(response) {
								Alfresco.util.PopupManager.displayMessage(
										{
											text: this.msg("message.details.failure")
										});
								this.editDialogOpening = false;
							},
							scope: this
						}
					}).show();
		},
		onActionDelete: function(p_items, owner, actionsConfig, fnDeleteComplete) {
			var items = YAHOO.lang.isArray(p_items) ? p_items : [p_items];

			Alfresco.util.PopupManager.displayPrompt(
                {
                    title:this.msg("message.confirm.delete.title"),
                    text: this.msg("message.confirm.delete.description"),
                    buttons:[
                        {
                            text:this.msg("button.delete"),
							handler: {
								obj: {
									context: this,
									items: items
								},
								fn: removeAssociations
							}
                        },
                        {
                            text:this.msg("button.cancel"),
                            handler:function DataGridActions__onActionDelete_cancel() {
                                this.destroy();
                            },
                            isDefault:true
                        }
                    ]
                });

			function removeAssociations(event, obj) {
				this.destroy();
				var grid = obj.context;


				var items = obj.items;
				//TODO: Предполагается, что удаляем по одному, если что - переделать
				var records = grid.widgets.dataTable.getRecordSet().getRecords();
				records.forEach(function(el) {
					if(el.getData('nodeRef') == items[0].nodeRef) {
						grid.onCollapse(el);
					}
				});

				var deputyNodeRef = items[0].nodeRef;
				$.ajax({
					url: Alfresco.constants.PROXY_URI + 'lecm/deputy/delete?nodeRef=' + deputyNodeRef,
					async: false,
					success: function (data, textStatus, jqXHR) {
						YAHOO.Bubbling.fire("dataItemsDeleted",
						{
							items: items,
							bubblingLabel: grid.options.bubblingLabel
						});
						YAHOO.Bubbling.fire("datagridRefresh", {
							bubblingLabel: grid.options.bubblingLabel
						});
					}
				});

			}



		},

		rebuildExpandedRows: function(layer, args) {
			var label = args[1].bubblingLabel;

			if(label && label == this.options.bubblingLabel) {

				var upFn = function updateHanlder() {
					var records = this.widgets.dataTable.getRecordSet().getRecords()
					records.forEach(function(el){
						var recordEl = document.getElementById(el.getId());
						var expandedEl = document.getElementById(el.getId() + '-expanded');
						YAHOO.util.Dom.insertAfter(expandedEl, recordEl);
					});
				}

				this.afterDataGridUpdate.push(upFn);


			}
		}
	}, true);
})();
