/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
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

				objectDescription: null
			},

			id: null,

			controlId: null,

			subscribeButton: null,

			unsubscribeButton: null,

			root: null,

			currentEmployeeSubscriptionRef: null,

			currentEmployee: null,

			onReady: function()
			{
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
				var callback = {
					success:function (oResponse) {
						var oResults = eval("(" + oResponse.responseText + ")");
						if (oResults != null) {
							for (var nodeIndex in oResults) {
								oResponse.argument.context.root = {
									nodeRef:oResults[nodeIndex].nodeRef,
									itemType:oResults[nodeIndex].itemType,
									namePattern:oResults[nodeIndex].namePattern,
									page:oResults[nodeIndex].page,
									fullDelete:oResults[nodeIndex].fullDelete
								};
							}
						}
					},
					failure:function (oResponse) {
						YAHOO.log("Failed to process XHR transaction.", "info", "example");
					},
					argument:{
						context:this
					},
					timeout:10000
				};
				YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
			},

			loadSubscriptionForEmployee: function() {
				if (this.options.objectNodeRef != null && this.currentEmployee != null && this.currentEmployee.nodeRef != null) {
					var me = this;
					var sUrl = Alfresco.constants.PROXY_URI + "/lecm/subscriptions/api/getEmployeeSubscriptionToObject?employeeRef=" +
						this.currentEmployee.nodeRef + "&objectRef=" + this.options.objectNodeRef;
					var callback = {
						success:function (oResponse) {
							var oResults = eval("(" + oResponse.responseText + ")");
							if (oResults && oResults.nodeRef) {
								me.currentEmployeeSubscriptionRef = oResults.nodeRef;
							} else {
								YAHOO.log("Failed to process XHR transaction.", "info", "example");
							}
							me.updateFormButtons();
						},
						failure:function (oResponse) {
							YAHOO.log("Failed to process XHR transaction.", "info", "example");
						}
					};
					YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
				}
			},

			updateFormButtons: function() {
				Dom.setStyle(this.subscribeButton.get("id"), "display", (this.currentEmployee == null || this.currentEmployeeSubscriptionRef != null) ? "none" : "");
				Dom.setStyle(this.unsubscribeButton.get("id"), "display", (this.currentEmployee == null || this.currentEmployeeSubscriptionRef == null) ? "none" : "");
			},

			onSubscribe: function(e, p_obj) {
				var me = this;
				// Intercept before dialog show
				var doBeforeDialogShow = function(p_form, p_dialog) {
					Alfresco.util.populateHTML(
						[ p_dialog.id + "-form-container_h", this.msg("label.subscribe.title") ]
					);
				};

				var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
					{
						itemKind:"type",
						itemId:"lecm-subscr:subscription-to-object",
						destination: this.root.nodeRef,
						mode:"create",
						formId: this.id + "-create-form",
						submitType:"json"
					});

				// Using Forms Service, so always create new instance
				var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails");
				createDetails.setOptions(
					{
						width:"20em",
						templateUrl:templateUrl,
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
							},
							scope:this
						},
						onFailure:{
							fn:function (response) {
								Alfresco.util.PopupManager.displayMessage(
									{
										text:this.msg("message.save.subscribe.failure")
									});
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
									if (this.currentEmployee != null && this.currentEmployee.name != null) {
										subscriptionName += " " + this.currentEmployee.name;
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
				var me = this;
				var fnActionUnsibscribeConfirm = function DataGridActions__onActionDelete_confirm(items) {
					var sUrl = Alfresco.constants.PROXY_URI + "/lecm/subscriptions/api/unsubscribeObject?nodeRef=" + me.currentEmployeeSubscriptionRef;
					var callback = {
						success:function (oResponse) {
							var oResults = eval("(" + oResponse.responseText + ")");
							if (oResults && oResults.success) {
								me.currentEmployeeSubscriptionRef = null;
								Alfresco.util.PopupManager.displayMessage(
									{
										text: me.msg("message.unsibscribe.success")
									});
							} else {
								Alfresco.util.PopupManager.displayMessage(
									{
										text: me.msg("message.unsibscribe.failure")
									});
							}
							me.updateFormButtons();
						},
						failure:function (oResponse) {
							Alfresco.util.PopupManager.displayMessage(
								{
									text: me.msg("message.unsibscribe.failure")
								});
						}
					};
					YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
				};

				Alfresco.util.PopupManager.displayPrompt(
					{
						title:me.msg("message.confirm.unsubscribe.title"),
						text: me.msg("message.confirm.unsubscribe.description"),
						buttons:[
							{
								text:me.msg("button.unsubscribe"),
								handler:function () {
									this.destroy();
									fnActionUnsibscribeConfirm.call();
								}
							},
							{
								text: me.msg("button.cancel"),
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