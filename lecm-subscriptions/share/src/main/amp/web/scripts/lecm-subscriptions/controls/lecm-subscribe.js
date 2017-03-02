/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};


/**
 * LogicECM Subscriptions module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Subscriptions.Subscriptions
 */
LogicECM.module.Subscriptions = LogicECM.module.Subscriptions || {};

(function()
{
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event;


	LogicECM.module.Subscriptions.SubscribeControl = function (fieldHtmlId)
	{
		LogicECM.module.Subscriptions.SubscribeControl.superclass.constructor.call(this, "LogicECM.module.Subscriptions.SubscribeControl", fieldHtmlId, [ "container", "datasource"]);
		this.id = fieldHtmlId;
		this.controlId = fieldHtmlId + "-cntrl";
		return this;
	};

	YAHOO.extend(LogicECM.module.Subscriptions.SubscribeControl, Alfresco.component.Base,
		{
			options: {
				objectNodeRef: null,

				objectDescription: null,

				availableRoles: null
			},

			id: null,

			controlId: null,

			subscribeButton: null,

			unsubscribeButton: null,

			root: null,

			currentEmployeeSubscriptionRef: null,

			currentEmployee: null,

            doubleClickLock: false,

			onReady: function() {
				if (this.options.availableRoles != null) {
					var me = this;
					Alfresco.util.Ajax.request(
						{
							url: Alfresco.constants.PROXY_URI + "lecm/orgstructure/isCurrentEmployeeHasBusinessOneRole",
							dataObj: {
								rolesId: this.options.availableRoles
							},
							successCallback: {
								fn: function (response) {
									if (response.json) {
										me.init();
									}
								}
							},
							failureMessage: "message.failure"
						});
				} else {
					this.init();
				}
			},

			init: function() {
				this.loadSubscriptionsRoot();
				this.loadObjectDescription();
				this.loadCurrentEmployee();

				this.subscribeButton = Alfresco.util.createYUIButton(this, this.controlId + "-subscribe-button", this.onSubscribe.bind(this), {}, Dom.get(this.controlId + "-subscribe-button"));
				this.unsubscribeButton = Alfresco.util.createYUIButton(this, this.controlId + "-unsubscribe-button", this.onUnsubscribe.bind(this), {}, Dom.get(this.controlId + "-unsubscribe-button"));

				this.updateFormButtons();
			},

			loadCurrentEmployee: function() {
				var me = this;
				var sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getCurrentEmployee";
				var callback = {
					success:function (oResponse) {
						var oResults = eval("(" + oResponse.responseText + ")");
						if (oResults && oResults.nodeRef) {
							me.currentEmployee = oResults;
							me.loadSubscriptionForEmployee();
						} else {
							YAHOO.log("Failed to process XHR transaction.", "info", "example");
						}
					},
					failure:function (oResponse) {
						YAHOO.log("Failed to process XHR transaction.", "info", "example");
					}
				};
				YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
			},

			loadObjectDescription: function() {
				var me = this;
				var sUrl = Alfresco.constants.PROXY_URI + "/lecm/business-journal/api/objectDescription?objectRef=" + this.options.objectNodeRef;
				var callback = {
					success:function (oResponse) {
						var oResults = eval("(" + oResponse.responseText + ")");
						if (oResults && oResults.objectDescription) {
							me.options.objectDescription = oResults.objectDescription;
						} else {
							YAHOO.log("Failed to process XHR transaction.", "info", "example");
						}
					},
					failure:function (oResponse) {
						YAHOO.log("Failed to process XHR transaction.", "info", "example");
					}
				};
				YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
			},

			loadSubscriptionsRoot: function() {
				var sUrl = Alfresco.constants.PROXY_URI + "lecm/subscriptions/roots";
				Alfresco.util.Ajax.jsonGet({
					url: sUrl,
					successCallback: {
						fn: function (response) {
							var oResults = response.json;
							if (oResults) {
								for (var nodeIndex in oResults) {
									this.root = {
										nodeRef:oResults[nodeIndex].nodeRef,
										itemType:oResults[nodeIndex].itemType,
										page:oResults[nodeIndex].page,
										fullDelete:oResults[nodeIndex].fullDelete
									};
								}
							}
						},
						scope: this
					},
					failureCallback: {
						fn: function (response) {
							YAHOO.log("Failed to process XHR transaction.", "info", "example");
						},
						scope: this
					}
				});
			},

			loadSubscriptionForEmployee: function() {
				if (this.options.objectNodeRef && this.currentEmployee && this.currentEmployee.nodeRef) {
					Alfresco.util.Ajax.jsonGet({
						url: Alfresco.constants.PROXY_URI + "/lecm/subscriptions/api/getEmployeeSubscriptionToObject",
						dataObj: {
							employeeRef: this.currentEmployee.nodeRef,
							objectRef: this.options.objectNodeRef
						},
						successCallback: {
							scope: this,
							fn: function (response) {
								var oResults = response.json;
								if (oResults && oResults.nodeRef) {
									this.currentEmployeeSubscriptionRef = oResults.nodeRef;
								} else {
									YAHOO.log("Failed to process XHR transaction.", "info", "example");
								}
								this.updateFormButtons();
							}
						},
						failureCallback: {
							scope: this,
							fn: function (response) {
								YAHOO.log("Failed to process XHR transaction.", "info", "example");
							}
						}
					});
				}
			},

			updateFormButtons: function() {
				Dom.setStyle(this.subscribeButton.get("id"), "display", (this.currentEmployee == null || this.currentEmployeeSubscriptionRef != null) ? "none" : "inline-block");
				Dom.setStyle(this.unsubscribeButton.get("id"), "display", (this.currentEmployee == null || this.currentEmployeeSubscriptionRef == null) ? "none" : "inline-block");
			},

			onSubscribe: function(e, p_obj) {
                if (this.doubleClickLock) return;
                this.doubleClickLock = true;
				var me = this;
				// Intercept before dialog show
				var doBeforeDialogShow = function(p_form, p_dialog) {
					Alfresco.util.populateHTML(
						[ p_dialog.id + "-form-container_h", this.msg("label.subscribe.title") ]
					);
                    this.doubleClickLock = false;
					p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
				};

				var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
				var templateRequestParams = {
						itemKind:"type",
						itemId:"lecm-subscr:subscription-to-object",
						destination: this.root.nodeRef,
						mode:"create",
						formId: this.id + "-create-form",
					submitType:"json",
					showCancelButton: true
				};

				// Using Forms Service, so always create new instance
				var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails");
				createDetails.setOptions(
					{
						width:"20em",
						templateUrl:templateUrl,
						templateRequestParams: templateRequestParams,
						actionUrl:null,
						destroyOnHide:true,
						doBeforeDialogShow:{
							fn:doBeforeDialogShow,
							scope:this
						},
						onSuccess:{
							fn:function (response) {
								me.currentEmployeeSubscriptionRef = response.json.persistedObject;
								me.updateFormButtons();
								Alfresco.util.PopupManager.displayMessage(
									{
										text:this.msg("message.save.subscribe.success")
									});
                                this.doubleClickLock = false;
							},
							scope:this
						},
						onFailure:{
							fn:function (response) {
								Alfresco.util.PopupManager.displayMessage(
									{
										text:this.msg("message.save.subscribe.failure")
									});
                                this.doubleClickLock = false;
							},
							scope:this
						},
						doBeforeFormSubmit:{
							fn:function (form) { // подставляем текущий объект
								var objectAddedInput = form['assoc_lecm-subscr_subscription-object-assoc_added'];
								if (objectAddedInput != null) {
									objectAddedInput.value = this.options.objectNodeRef;
								}
								if (this.currentEmployee != null && this.currentEmployee.nodeRef != null) {
									var employeeAddedInput = form['assoc_lecm-subscr_destination-employee-assoc_added'];
									if (employeeAddedInput != null) {
										employeeAddedInput.value = this.currentEmployee.nodeRef;
									}
								}

								var name = form['prop_cm_name'];
								if (name != null) {
									var subscriptionName = this.options.objectDescription.split(":").join("-");
									if (this.currentEmployee != null && this.currentEmployee.shortName != null) {
										subscriptionName += " " + this.currentEmployee.shortName;
									}
									var date = new Date();
									subscriptionName += " " + Alfresco.util.formatDate(date, "dd.mm.yyyy HH-MM-ss");
									name.value = this.getValidFileName(subscriptionName);
								}
							},
							scope:this
						}
					}).show();
			},

			getValidFileName: function(str) {
				var result = str;
				result = result.split(":").join("-");
				result = result.split("/").join("");
				result = result.split("\\").join("");
				result = result.split("|").join("");
				result = result.split("?").join("");
				result = result.split("<").join("");
				result = result.split(">").join("");
				result = result.split("*").join("");
				result = result.split('"').join("");
				result = result.split("'").join("");
				result = result.split("[").join("");
				result = result.split("]").join("");
				return result;
			},

			onUnsubscribe: function(e, p_obj) {
				var fnActionUnsibscribeConfirm = function DataGridActions__onActionDelete_confirm(items) {
					Alfresco.util.Ajax.jsonGet({
						url: Alfresco.constants.PROXY_URI + "/lecm/subscriptions/api/unsubscribeObject",
						dataObj: {
							nodeRef: this.currentEmployeeSubscriptionRef
						},
						successCallback: {
							scope: this,
							fn: function (response) {
								var oResults = response.json;
								if (oResults && oResults.success) {
									this.currentEmployeeSubscriptionRef = null;
									Alfresco.util.PopupManager.displayMessage({
										text: this.msg("message.unsibscribe.success")
									});
								} else {
									Alfresco.util.PopupManager.displayMessage({
										text: this.msg("message.unsibscribe.failure")
									});
								}
								this.updateFormButtons();
							}
						},
						failureMessage: this.msg("message.unsibscribe.failure")
					});
				};

				Alfresco.util.PopupManager.displayPrompt({
					title: this.msg("message.confirm.unsubscribe.title"),
					text: this.msg("message.confirm.unsubscribe.description"),
					buttons:[
						{
							text: this.msg("button.unsubscribe"),
							handler:function () {
								this.destroy();
								fnActionUnsibscribeConfirm.call();
							}
						},
						{
							text: this.msg("button.cancel"),
							handler:function () {
								this.destroy();
							},
							isDefault:true
						}
					]
				});
			}
		});
})();