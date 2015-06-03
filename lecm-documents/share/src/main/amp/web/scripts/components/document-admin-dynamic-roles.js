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
			roles: null
		},

		onReady: function () {
			this.drawRoles()
		},

		drawRoles: function() {
			var container = Dom.get(this.id);

			if (this.options.roles != null && container != null) {
				for (var i = 0; i < this.options.roles.length; i++) {
					container.innerHTML += this.getRoleView(this.options.roles[i]);
				}
			}
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
			alert("add employee to " + role.id);
		},

		removeEmployee: function (event, params) {
			alert("remove employee " + params.employee.name + " to role" + params.role.id);
		}
	});
})();
