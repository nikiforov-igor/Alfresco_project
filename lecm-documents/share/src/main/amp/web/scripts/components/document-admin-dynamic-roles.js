/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};

LogicECM.module.DocumentAdmin = LogicECM.module.DocumentAdmin || {};

(function() {
	var Dom = YAHOO.util.Dom;

	LogicECM.module.DocumentAdmin.DynamicRoles = function(htmlId) {
		LogicECM.module.DocumentAdmin.DynamicRoles.superclass.constructor.call(this, 'LogicECM.module.DocumentAdmin.DynamicRoles', htmlId, ['container', 'json']);
		return this;
	};

	YAHOO.extend(LogicECM.module.DocumentAdmin.DynamicRoles, Alfresco.component.Base, {
		options: {
			documentNodeRef: null
		},

		roles: null,

		onReady: function () {
			this.loadRoles();
		},

		loadRoles: function() {
			var me = this;
			Alfresco.util.Ajax.jsonGet({
					url: Alfresco.constants.PROXY_URI + "lecm/statemachine/getDynamicRoles?nodeRef=" + encodeURIComponent(this.options.documentNodeRef),
					successCallback: {
						fn: function (response) {
							var oResults = response.json;
							if (oResults != null) {
								me.drawRoles(oResults);
							}
						},
						scope: this
					}
				});
		},

		drawRoles: function(roles) {
			var container = Dom.get(this.id);
			var results = "";
			if (roles != null && container != null) {
				for (var i = 0; i < roles.length; i++) {
					results += this.getRoleView(roles[i]);
				}
			}
			container.innerHTML = results
		},

		getRoleView: function(role) {
			var result = "<div>";
			result += role.name;
			var addEmployeeId = "dynamic-role-add-employee-" + role.id;

			result += '<a href="javascript:void(0);" class="dynamic-role-add-employee" id="' + addEmployeeId + '">add</a>';
			if (role.emloyees != null) {
				result += "<div class='dynamic-role-employees'>";
				for (var i = 0; i < role.emloyees.length; i++) {
					result += this.getEmployeeView(role, role.emloyees[i]);
				}
				result += "</div>";
			}
			result += "</div>";

			YAHOO.util.Event.onAvailable(addEmployeeId, this.attachAddEmployeeClickListener, role, this);

			return result;
		},

		getEmployeeView: function(role, employee) {
			var result = "<div>";
			result += LogicECM.module.Base.Util.getControlEmployeeView(employee.nodeRef, employee.name, true);
			var removeEmployeeId = "dynamic-role-remove-employee-" + employee.nodeRef.replace(/:|\//g, '_') + "_" + role.id;
			result += '<a href="javascript:void(0);" class="dynamic-role-remove-employee" id="' + removeEmployeeId + '">del</a>';
			YAHOO.util.Event.onAvailable(removeEmployeeId, this.attachRemoveEmployeeClickListener, {
				role: role,
				employee: employee
			} , this);
			result += "</div>";
			return result;
		},

		attachAddEmployeeClickListener: function (role) {
			YAHOO.util.Event.on("dynamic-role-add-employee-" + role.id, 'click', this.addEmployee, role, this);
		},

		attachRemoveEmployeeClickListener: function (params) {
			YAHOO.util.Event.on("dynamic-role-remove-employee-" + params.employee.nodeRef.replace(/:|\//g, '_') + "_" + params.role.id, 'click', this.removeEmployee, params, this);
		},

		addEmployee: function (event, role) {
			var me = this;
			new Alfresco.module.SimpleDialog("add-employee-to-dynamic-role" + Alfresco.util.generateDomId()).setOptions({
				width: "50em",
				templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
				templateRequestParams: {
					submissionUrl: "/lecm/document/action/addEmployeesToDynamicRole",
					itemKind: "type",
					itemId: "lecm-orgstr:business-role",
					formId: "addEmployeeToDynamicRole",
					mode: "create",
					submitType: "json",
					showCancelButton: true,
					roleId: role.id,
					document: me.options.documentNodeRef
				},
				actionUrl: null,
				destroyOnHide: true,
				doBeforeDialogShow: {
					fn: function (p_form, p_dialog) {
						var contId = p_dialog.id + "-form-container";
						var dialogName = me.msg("title.documents.admin.roles.dynamic.employee.add");
						Alfresco.util.populateHTML(
							[contId + "_h", dialogName]
						);

						p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
					}
				},
				onSuccess: {
					fn: function (response) {
						me.loadRoles();
					},
					scope: this
				}
			}).show();
		},

		removeEmployee: function (event, params) {
			alert("remove employee " + params.employee.name + " to role" + params.role.id);
		}
	});
})();
