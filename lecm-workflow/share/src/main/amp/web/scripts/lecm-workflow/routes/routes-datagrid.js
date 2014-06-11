if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Routes = LogicECM.module.Routes || {};

(function() {

	var __datagridId__ = null;
	var __defferedCenterDialog__ = null;

	LogicECM.module.Routes.DataGrid = function(containerId) {
		LogicECM.module.Routes.DataGrid.superclass.constructor.call(this, containerId);
		return this;
	};

	YAHOO.lang.extend(LogicECM.module.Routes.DataGrid, LogicECM.module.Base.DataGrid);

	YAHOO.lang.augmentObject(LogicECM.module.Routes.DataGrid.prototype, {
		_onEditRouteFormDestroyed: function(event, args) {
			YAHOO.Bubbling.unsubscribe('assigneesListDatagridReady', this._onAssigneesListDatagridReady, this);
			YAHOO.Bubbling.unsubscribe('formContainerDestroyed', this._onEditRouteFormDestroyed, this);
			__defferedCenterDialog__.expire();
		},

		_onAssigneesListDatagridReady: function(event, args) {
			var bubblingLabel = args[1].bubblingLabel;
			if (!__datagridId__) {
				__datagridId__ = bubblingLabel;
				__defferedCenterDialog__.fulfil('datagrid1');
			} else if(__datagridId__ != bubblingLabel) {
				__datagridId__ = null;
				__defferedCenterDialog__.fulfil('datagrid2');
			} else {
				__datagridId__ = null;
			}
		},

		_centerDialog: function() {
			var editRouteForm = this.editRouteForm;
			var centerDialogInternal = function() {
				if (editRouteForm) {
					editRouteForm.dialog.center();
				}
			};
			setTimeout(centerDialogInternal, 50);
		},

		onActionEditCommon: function(item) {
			this.onActionEdit(item, 'editCommonRoute');
		},

		onActionEditPrivate: function(item) {
			this.onActionEdit(item, 'editPrivateRoute');
		},

		onActionEdit: function(item, formId) {
			// Для предотвращения открытия нескольких карточек (при многократном быстром нажатии на кнопку редактирования)
			if (this.editDialogOpening) {
				return;
			}
			this.editDialogOpening = true;

			YAHOO.Bubbling.on('assigneesListDatagridReady', this._onAssigneesListDatagridReady, this);
			YAHOO.Bubbling.on('formContainerDestroyed', this._onEditRouteFormDestroyed, this);
			__defferedCenterDialog__ = new Alfresco.util.Deferred(['datagrid1', 'datagrid2'], {
				fn: this._centerDialog,
				scope: this
			});

			var templateRequestParams = {
				itemKind: "node",
				itemId: item.nodeRef,
				mode: "edit",
				submitType: "json",
				showCancelButton: true
			};
			if (formId) {
				templateRequestParams.formId = formId;
			} else if (this.options.editForm) {
				templateRequestParams.formId = this.options.editForm;
			}
			var editRouteForm = new Alfresco.module.SimpleDialog(this.id + "-editDetails");
			editRouteForm.setOptions({
				width: this.options.editFormWidth,
				templateUrl: Alfresco.constants.URL_SERVICECONTEXT + 'lecm/components/form',
				templateRequestParams: templateRequestParams,
				destroyOnHide: true,
				actionUrl:null,
				doBeforeDialogShow: {
					fn: function(p_form, p_dialog) {
						p_dialog.dialog.setHeader(this.msg(this.options.editFormTitleMsg));
						this.editDialogOpening = false;
					},
					scope: this
				},
				onSuccess: {
					fn: function(response) {
						// Reload the node's metadata
						YAHOO.Bubbling.fire("datagridRefresh",{
							bubblingLabel: this.options.bubblingLabel
						});
						Alfresco.util.PopupManager.displayMessage({
							text: this.msg("message.details.success")
						});
						this.editDialogOpening = false;
					},
					scope: this
				},
				onFailure: {
					fn: function(response) {
						Alfresco.util.PopupManager.displayMessage({
							text: this.msg("message.details.failure")
						});
						this.editDialogOpening = false;
					},
					scope: this
				}
			});
			editRouteForm.show();
			this.editRouteForm = editRouteForm;
		}
	}, true);
})();
