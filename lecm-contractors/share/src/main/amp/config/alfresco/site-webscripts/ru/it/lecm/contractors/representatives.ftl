<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign datagridId = fieldHtmlId + "-representatives"/>
<#assign bubblingLabel = "representatives-datagrid"/>
<#assign contractorRef=args.itemId/>

<#assign showActions = form.mode != "view"/>

<#if form.mode != "view">
<div id="${fieldHtmlId}">
    <div id="${fieldHtmlId}-btnCreateNewRepresentative"></div>
</div>
</#if>

<div id="${fieldHtmlId}-wrapper" class="form-field with-grid">
<script type="text/javascript">//<![CDATA[
(function() {
    "use strict";

LogicECM.module.Base.Util.loadScripts([
    'scripts/lecm-base/components/advsearch.js',
	'scripts/lecm-base/components/lecm-datagrid.js'
	], function() {

	function RepresentativesGrid(containerId) {
		return RepresentativesGrid.superclass.constructor.call(this, containerId);
	}

	YAHOO.lang.extend(RepresentativesGrid, LogicECM.module.Base.DataGrid);

	YAHOO.lang.augmentObject(RepresentativesGrid.prototype, {
		onPrimaryChange: function(p_items, owner, actionsConfig, fnDeleteComplete, fnPrompt) {

			var currentNodeRef = p_items.nodeRef, // {String}
					dataObj = { "representativeToAssignAsPrimary": currentNodeRef };

			Alfresco.util.Ajax.request({
				method: "POST",
				url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/contractors/representatives/reassign",
				dataObj: dataObj,
				requestContentType: "application/json",
				responseContentType: "application/json",
				successCallback: {
					fn: function(response) {
						YAHOO.Bubbling.fire("datagridRefresh", {
							bubblingLabel: "${bubblingLabel}"//"representatives-datagrid"
						});
					},
					scope: this
				},
				failureCallback: {
					fn: function() {
						Alfresco.util.PopupManager.displayMessage({
							text: Alfresco.component.Base.prototype.msg("message.reassign-representative.failure")
						});
					}
				}
			});

		}
	}, true);

	var __itemType__ = "lecm-contractor:link-representative-and-contractor";

	function _initializeDatagrid() {
		YAHOO.util.Event.onDOMReady(function() {
			var datagrid = new RepresentativesGrid("${datagridId}").setOptions({

				bubblingLabel: "${bubblingLabel}",//"representatives-datagrid",
				usePagination: false,
				showExtendSearchBlock: false,
				showCheckboxColumn: false,
				searchShowInactive: false,
				forceSubscribing: true,
				showActionColumn: ${showActions?string},

			<#if showActions>
				actions: [
					{
						type: "datagrid-action-link-representatives-datagrid",
						id: "onPrimaryChange",
						permission: "edit",
						label: "${msg("action.set_primary")}",
						evaluator: function (rowData) {
							var itemData = rowData.itemData;
							return !itemData["prop_lecm-contractor_link-to-representative-association-is-primary"].value;
						}
					},
					{
						type: "datagrid-action-link-representatives-datagrid",
						id: "onActionDelete",
						permission: "delete",
						label: "${msg("action.delete_addressee")}"//"${msg("actions.delete-row")}"
					}
				],
			</#if>

				datagridMeta: {
					useFilterByOrg: false,
					useChildQuery: true,
					itemType: __itemType__,
					nodeRef: "${contractorRef}",
					actionsConfig: {
						fullDelete: true,
						trash: false
					}
				}
			});

			datagrid.draw();
		});
	};

	function _showAddRepresentativeDialog(response) {
		// Создание формы добавления адресанта.
		var isPrimaryCheckboxChecked,
			addRepresentativeForm = new Alfresco.module.SimpleDialog("${fieldHtmlId}-add-representative-form"),
			templateRequestParams = {
				itemKind: "type",
				itemId: __itemType__,
				//formId: "addNewRepresentative",
				destination: "${contractorRef}",
				mode: "create",
				submitType: "json",
				ignoreNodes: response.json.join(),
				showCancelButton: true,
				showCaption: false
			};

		addRepresentativeForm.setOptions({
			width: "50em",
			templateUrl: "components/form",
			templateRequestParams: templateRequestParams,
			destroyOnHide: true,
			doBeforeDialogShow: {
				fn: function (p_form, p_dialog) {
					p_dialog.dialog.setHeader("${msg('tab.representatives.addRepresentativeButton.label')}");
				},
				scope: this
			},
			doBeforeFormSubmit: {
				fn: function() {
					isPrimaryCheckboxChecked = YAHOO.util.Dom.get("${fieldHtmlId}-add-representative-form_prop_lecm-contractor_link-to-representative-association-is-primary-entry").checked;
				},
				scope: this
			},
			onSuccess: {
				fn: function(response) {
					if (isPrimaryCheckboxChecked) {
						Alfresco.util.Ajax.request({
							method: "POST",
							url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/contractors/representatives/reassign",
							dataObj: { "representativeToAssignAsPrimary": response.json.persistedObject },
							requestContentType: "application/json",
							responseContentType: "application/json",
							successCallback: {
								fn: function(response) {
									YAHOO.Bubbling.fire("datagridRefresh", {
										bubblingLabel: "${bubblingLabel}"//"representatives-datagrid"
									});
								},
								scope: this
							},
							failureCallback: {
								fn: function() {
									Alfresco.util.PopupManager.displayMessage({
										text: Alfresco.component.Base.prototype.msg("message.reassign-representative.failure")
									});
								}
							}
						});
					}

					YAHOO.Bubbling.fire("dataItemCreated", {
						nodeRef: response.json.persistedObject,
						bubblingLabel: "${bubblingLabel}"//"representatives-datagrid"
					});

					Alfresco.util.PopupManager.displayMessage({
						text: Alfresco.component.Base.prototype.msg("message.add-representative.success")
					});
				},
				scope: this
			},
			onFailure: {
				fn: function() {
					Alfresco.util.PopupManager.displayMessage({
						text: Alfresco.component.Base.prototype.msg("message.add-representative.failure")
					});
				},
				scope: this
			}
		});

		addRepresentativeForm.show();
	};

	function _showAddRepresentativeForm() {
		// Спасаем "тонущие" всплывающие сообщения.
		Alfresco.util.PopupManager.zIndex = 9000;

		// Дергаем сервис, который получает список адресантов связанных с контрагентом.
		Alfresco.util.Ajax.request({
			method: "GET",
			url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/contractors/representatives/busy",
			responseContentType: "application/json",
			dataObj: {
				nodeRef: "${contractorRef}"
			},
			successCallback: {
				fn: _showAddRepresentativeDialog,
				scope: this
			},
			failureCallback: {
				fn: function() {
					Alfresco.util.PopupManager.displayMessage({
						text: "${msg("msg.get_addressee_list_failure")}"
					});
				}
			}
		});
	};

	function _initializeAddRepresentativeButton() {
		var putButtonIn = YAHOO.util.Dom.get("${fieldHtmlId}");

		if (putButtonIn !== null) { // А если не нашли, то мы во view-режиме.
			var button = Alfresco.util.createYUIButton(putButtonIn, "btnCreateNewRepresentative", _showAddRepresentativeForm, {
				label: Alfresco.component.Base.prototype.msg("tab.representatives.addRepresentativeButton.label")
			});

			button.setStyle("margin", "0 0 5px 1px");
		}
	};

	YAHOO.util.Event.onAvailable("${fieldHtmlId}-wrapper", function() {
		_initializeDatagrid();
		_initializeAddRepresentativeButton();
	});
});

})();
//]]>
</script>
<@grid.datagrid datagridId false/>
</div>
