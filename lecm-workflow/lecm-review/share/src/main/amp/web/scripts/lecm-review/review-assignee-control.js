/* global Alfresco, LogicECM, YAHOO */
if (typeof LogicECM === 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Review = LogicECM.module.Review || {};

(function () {
	LogicECM.module.Review.AssigneeControl = function (htmlId) {
		var addEmployeeButtonElement = YAHOO.util.Dom.get(htmlId + '-add-employee'),
			assigneesListInputId = htmlId + '-list-node-input';

		LogicECM.module.Review.AssigneeControl.superclass.constructor.call(this, htmlId);

		if (addEmployeeButtonElement) {
			this.addEmployeeButton = new YAHOO.widget.Button(addEmployeeButtonElement);
			this.addEmployeeButton.on('click', this.onAddEmployeeButton, this, true);
		}

		this.assigneesListInput = YAHOO.util.Dom.get(assigneesListInputId);

		this.name = 'LogicECM.module.Review.AssigneeControl';

		YAHOO.Bubbling.fire('registerValidationHandler', {
			message: Alfresco.util.message('lecm.review.message.add.review.participant'),
			fieldId: addEmployeeButtonElement,
			handler: this.assigneesValidator,
			when: 'keyup'
		});

		YAHOO.Bubbling.on('datagridVisible', function() {
			YAHOO.Bubbling.fire('mandatoryControlValueUpdated', this);
			this.widgets.dataTable.subscribe("rowAddEvent", this.onRowChangeEvent, this, true);
			this.widgets.dataTable.subscribe("rowDeleteEvent", this.onRowChangeEvent, this, true);
		}, this);

		this.initControl();

		return this;
	};

	YAHOO.lang.extend(LogicECM.module.Review.AssigneeControl, LogicECM.module.Base.DataGrid);

	YAHOO.lang.augmentObject(LogicECM.module.Review.AssigneeControl.prototype, {
		assigneeListNode: null,
		assigneesListInput: null,
		renewDatagrid: function () {
			YAHOO.Bubbling.fire('activeGridChanged', {
				datagridMeta: {
					useFilterByOrg: false,
					datagridFormId: 'reviewAssigneeList',
					itemType: 'lecm-workflow:assignee',
					nodeRef: this.assigneeListNode,
					useChildQuery: true,
					sort: 'cm:created|true',
					actionsConfig: {
						fullDelete: true,
						trash: false
					}
				},
				bubblingLabel: this.id
			});
		},
		initControl: function () {
			Alfresco.util.Ajax.jsonRequest({
				method: 'POST',
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/getDefaultAssigneesList',
				dataObj: {
					workflowType: 'REVIEW',
					concurrency: 'PARALLEL'
				},
				successCallback: {
					fn: function (r) {
						this.assigneeListNode = r.json.defaultList;
						this.assigneesListInput.value = this.assigneeListNode;

						this.renewDatagrid();
					},
					scope: this
				},
				failureCallback: {
					fn: function () {
						Alfresco.util.PopupManager.displayMessage({
							text: Alfresco.util.message('lecm.review.message.get.review-list.ref.fail')
						});
					}
				}
			});

		},
		onRowChangeEvent: function() {
			YAHOO.Bubbling.fire('mandatoryControlValueUpdated', this);
		},
		onAddEmployeeButton: function () {
			var addEmployeesDialog = new Alfresco.module.SimpleDialog(this.id + '-createAssigneeItemDialog'),
				actionUrl = Alfresco.constants.PROXY_URI_RELATIVE + '/lecm/workflow/review/createAssigneeItemInQueue';

			addEmployeesDialog.setOptions({
				width: '35em',
				templateUrl: 'components/form',
				actionUrl: actionUrl,
				templateRequestParams: {
					itemKind: 'type',
					itemId: 'lecm-workflow:assignee',
					destination: this.assigneeListNode,
					formId: 'addEmployeesToReview',
					mode: 'create',
					submitType: 'json',
					ignoreNodes: this._getIgnoreNodes(),
					showCancelButton: true,
					showCaption: false
				},
				destroyOnHide: true,
				doBeforeDialogShow: {
					fn: function (p_form, simpleDialog) {
						simpleDialog.dialog.setHeader(Alfresco.util.message('lecm.review.title.add.review.participants'));
						simpleDialog.dialog.subscribe('destroy', function (event, args, params) {
							LogicECM.module.Base.Util.destroyForm(simpleDialog.id);
							LogicECM.module.Base.Util.formDestructor(event, args, params);
						}, {moduleId: simpleDialog.id}, this);
					},
					scope: this
				},
				onSuccess: {
					fn: function (r) {
						var persistedObjects = r.json,
							persistedObjectsLength = persistedObjects.length,
							i, persistedObject;

						for (i = 0; i < persistedObjectsLength; i++) {
							persistedObject = persistedObjects[i];
							YAHOO.Bubbling.fire('nodeCreated', {
								nodeRef: persistedObject.nodeRef,
								bubblingLabel: this.id
							});
							YAHOO.Bubbling.fire('dataItemCreated', {
								nodeRef: persistedObject.nodeRef,
								bubblingLabel: this.id
							});
						}

						Alfresco.util.PopupManager.displayMessage({
							text: this.msg('message.new-row.success')
						});
					},
					scope: this
				},
				failureMessage: this.msg('message.new-row.failure')
			});
			addEmployeesDialog.show();
		},
		assigneesValidator: function (field, args, event, form) {
			var controls = Alfresco.util.ComponentManager.find({name: 'LogicECM.module.Review.AssigneeControl'}),
				assigneeControl = controls && controls.length ? controls[0] : null,
				result = false,
				records;

			if (assigneeControl) {
				records = assigneeControl.widgets.dataTable.getRecordSet().getRecords();
				result = records && records.length > 0;
			}

			return result;
		},
		_getIgnoreNodes: function() {
			var i;

			var dataTable = this.widgets.dataTable;
			var recordSet = dataTable.getRecordSet();
			var recordSetLg = recordSet.getLength();

			var result = [];

			if (recordSetLg === 0) {
				return result;
			}

			for (i = 0; i < recordSetLg; i++) {
				result.push(recordSet.getRecord(i).getData('itemData')['assoc_lecm-workflow_assignee-employee-assoc'].value);
			}

			return result;
		}
	}, true);
})();
