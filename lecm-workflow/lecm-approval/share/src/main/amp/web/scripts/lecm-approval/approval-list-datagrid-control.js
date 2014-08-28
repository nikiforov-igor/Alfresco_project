if (typeof LogicECM == 'undefined' || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Approval = LogicECM.module.Approval || {};

(function() {

	LogicECM.module.Approval.ApprovalListDataGridControl = function(containerId, documentNodeRef) {
		this.documentNodeRef = documentNodeRef;

		this.getApprovalData();
		YAHOO.Bubbling.on('activeTabChange', this.renewDatagrid, this);

		return LogicECM.module.Approval.ApprovalListDataGridControl.superclass.constructor.call(this, containerId);
	};

	YAHOO.lang.extend(LogicECM.module.Approval.ApprovalListDataGridControl, LogicECM.module.Base.DataGrid);

	YAHOO.lang.augmentObject(LogicECM.module.Approval.ApprovalListDataGridControl.prototype, {
		stageType: null,
		stageItemType: null,
		currentIterationNode: null,
		doubleClickLock: false,
		getApprovalData: function(callback, callbackArg) {
			Alfresco.util.Ajax.request({
				method: 'GET',
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/routes/getRouteDataForDocument',
				dataObj: {
					documentNodeRef: this.documentNodeRef
				},
				successCallback: {
					scope: this,
					fn: function(response) {
						if (response) {
							this.stageType = response.json.stageType;
							this.stageItemType = response.json.stageItemType;
							this.currentIterationNode = response.json.currentIterationNode ? response.json.currentIterationNode : null;

							LogicECM.module.Routes = LogicECM.module.Routes || {};
							LogicECM.module.Routes.Const = LogicECM.module.Routes.Const || {};
							LogicECM.module.Routes.Const.ROUTES_CONTAINER = LogicECM.module.Routes.Const.ROUTES_CONTAINER || {};
							LogicECM.module.Routes.Const.ROUTES_CONTAINER.stageItemType = this.stageItemType;

							if (YAHOO.lang.isFunction(callback)) {
								callback.call(this, callbackArg);
							}
						}
					}
				},
				failureMessage: 'message.failure',
				execScripts: true,
				scope: this
			});
		},
		renewDatagrid: function(event, args) {
			function isDescendant(parent, child) {
				var node = child.parentNode;
				while (node !== null) {
					if (node === parent) {
						return true;
					}
					node = node.parentNode;
				}
				return false;
			}

			var currentTabDiv;
			if (event && event === 'activeTabChange' && args) {
				currentTabDiv = args[1].newValue.get('contentEl');
				if (!isDescendant(currentTabDiv, document.getElementById(this.id))) {
					return;
				}
			}

			if (!(this.stageType && this.stageItemType)) {
				this.getApprovalData(this.fireGridChanged);
			} else {
				this.fireGridChanged();
			}

		},
		fireGridChanged: function() {
			YAHOO.Bubbling.fire('activeGridChanged', {
				datagridMeta: {
					itemType: this.stageType,
					nodeRef: this.currentIterationNode,
					useChildQuery: true,
					searchConfig: {
						filter: ''
					}
				},
				bubblingLabel: this.id
			});
		},
		onCollapse: function(record) {
			var expandedRow = YAHOO.util.Dom.get(this.getExpandedRecordId(record));
			LogicECM.module.Base.Util.destroyForm(this.getExpandedFormId(record));
			expandedRow.parentNode.removeChild(expandedRow);
		}

//		getExpandedFormId: function(record) {
//			var nodeRef = record.getData('nodeRef');
//			return nodeRef.replace(/:|\//g, '_') + '-dtgrd';
//		}
	}, true);


})();
