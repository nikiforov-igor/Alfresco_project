if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.OrgStructure = LogicECM.module.OrgStructure || {};

LogicECM.module.OrgStructure.BusinessRoles = LogicECM.module.OrgStructure.BusinessRoles || {};

(function () {

	var attributeForShow = "cm:name";

	LogicECM.module.OrgStructure.BusinessRoles.DataGrid = function (containerId) {
		return LogicECM.module.OrgStructure.BusinessRoles.DataGrid.superclass.constructor.call(this, containerId);
	};

	/**
	 * Extend from LogicECM.module.Base.DataGrid
	 */
	YAHOO.lang.extend (LogicECM.module.OrgStructure.BusinessRoles.DataGrid, LogicECM.module.Base.DataGrid);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
	YAHOO.lang.augmentObject (LogicECM.module.OrgStructure.BusinessRoles.DataGrid.prototype, {

		getCellFormatter: function DataGrid_getCellFormatter () {
			var scope = this;

			return function DataGrid_renderCellDataType (elCell, oRecord, oColumn, oData) {
				var html = "";
				var content;

				if (!oRecord) {
					oRecord = this.getRecord (elCell);
				}
				if (!oColumn) {
					oColumn = this.getColumn (elCell.parentNode.cellIndex);
				}

				if (oRecord && oColumn) {
					if (!oData) {
						oData = oRecord.getData ("itemData")[oColumn.field];
					}
					if (oData) {
						var datalistColumn = scope.datagridColumns[oColumn.key];
						if (datalistColumn) {
							oData = YAHOO.lang.isArray (oData) ? oData : [oData];
							var plane = true;

							for (var i = 0, ii = oData.length, data; i < ii; i++) {
								data = oData[i];

								switch (datalistColumn.dataType.toLowerCase ()) {
									case "cm:person":
										html += '<span class="person">'
											 + Alfresco.util.userProfileLink (data.metadata, data.displayValue)
											 + '</span>';
										break;

									case "datetime":
										content = Alfresco.util.formatDate (Alfresco.util.fromISO8601 (data.value), scope.msg ("date-format.default"));
										if (datalistColumn.name == attributeForShow) {
											content = "<a href='javascript:void(0);' onclick=\"viewAttributes(\'"
													+ oRecord.getData ("nodeRef")
													+ "\')\">"
													+ content
													+ "</a>";
										}
										html += content;
										break;

									case "date":
										content = Alfresco.util.formatDate (Alfresco.util.fromISO8601 (data.value), scope.msg ("date-format.defaultDateOnly"));
										if (datalistColumn.name == attributeForShow) {
											content = "<a href='javascript:void(0);' onclick=\"viewAttributes(\'"
													+ oRecord.getData ("nodeRef")
													+ "\')\">"
													+ content
													+ "</a>";
										}
										html += content;
										break;

									case "text":
										content = Alfresco.util.encodeHTML (data.displayValue);
										if (datalistColumn.name == attributeForShow) {
											html += "<a href='javascript:void(0);' onclick=\"viewAttributes(\'"
												 + oRecord.getData("nodeRef")
												 + "\')\">"
												 + content
												 + "</a>";
										} else {
											html += Alfresco.util.activateLinks (content);
										}
										break;

									default:
										if (datalistColumn.type == "association") {
											html += '<a><img src="'
												 + Alfresco.constants.URL_RESCONTEXT
												 + 'components/images/filetypes/'
												 + Alfresco.util.getFileIcon (data.displayValue, (data.metadata == "container" ? 'cm:folder' : null), 16)
												 + '" width="16" alt="'
												 + Alfresco.util.encodeHTML (data.displayValue)
												 + '" title="'
												 + Alfresco.util.encodeHTML (data.displayValue)
												 + '" /> '
												 + Alfresco.util.encodeHTML (data.displayValue)
												 + '</a>'
										} else {
											html += Alfresco.util.activateLinks (Alfresco.util.encodeHTML (data.displayValue));
										}
										break;
								}
								if (i < ii - 1) {
									html += "<br />";
								}
							}
						}
					}
				}
				elCell.innerHTML = html;
			};
		},

		/**
		 * Edit Data Item pop-up
		 *
		 * @method onActionEdit
		 * @param item {object} Object literal representing one data item
		 */
		onActionEdit:function DataGrid_onActionEdit(item) {
			var me = this;
			// Intercept before dialog show
			var doBeforeDialogShow = function DataGrid_onActionEdit_doBeforeDialogShow(p_form, p_dialog) {
				Alfresco.util.populateHTML(
					[ p_dialog.id + "-dialogTitle", this.msg("label.edit-row.title") ]
				);
			};

			var template = "components/form?itemKind={itemKind}&itemId={itemId}&mode={mode}&submitType={submitType}&showCancelButton=true&formId={formId}";
			var url = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + template, {
				itemKind:"node",
				itemId:item.nodeRef,
				mode:"edit",
				submitType:"json",
				formId: "configureBusinessRole"
			});

			// Using Forms Service, so always create new instance
			var editDetails = new Alfresco.module.SimpleDialog(this.id + "-editDetails");
			editDetails.setOptions({
				width:"50em",
				templateUrl:url,
				actionUrl:null,
				destroyOnHide:true,
				doBeforeDialogShow:{
					fn:doBeforeDialogShow,
					scope:this
				},
				onSuccess:{
					scope:this,
					fn:function DataGrid_onActionEdit_success(response) {
						// Reload the node's metadata
						Alfresco.util.Ajax.jsonPost({
							url:Alfresco.constants.PROXY_URI + "lecm/base/item/node/" + new Alfresco.util.NodeRef(item.nodeRef).uri,
							dataObj:this._buildDataGridParams(),
							successCallback:{
								scope:this,
								fn:function DataGrid_onActionEdit_refreshSuccess(response) {
									// Fire "itemUpdated" event
									Bubbling.fire("dataItemUpdated", {
										item:response.json.item,
										bubblingLabel:me.options.bubblingLabel
									});
									// Display success message
									Alfresco.util.PopupManager.displayMessage({
										text:this.msg("message.details.success")
									});
								}
							},
							failureCallback:{
								scope:this,
								fn:function DataGrid_onActionEdit_refreshFailure(response) {
									Alfresco.util.PopupManager.displayMessage({
										text:this.msg("message.details.failure")
									});
								}
							}
						});
					}
				},
				onFailure:{
					scope:this,
					fn:function DataGrid_onActionEdit_failure(response) {
						Alfresco.util.PopupManager.displayMessage({
							text:this.msg("message.details.failure")
						});
					}
				}
			}).show();
		}
	}, true);

})();
