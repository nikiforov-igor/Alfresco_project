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
				objectNodeRef: null
			},

			id: null,

			controlId: null,

			subscribeButton: null,

			root: null,

			currentEmployee: null,

			onReady: function()
			{
				this.loadSubscriptionsRoot();
				this.loadCurrentEmployee();
				this.subscribeButton =  new YAHOO.widget.Button(
					this.controlId + "-subscribe-button",
					{
						onclick: {
							fn: this.onSubscribe,
							obj: null,
							scope: this
						}
					}
				);
			},

			loadCurrentEmployee: function() {
				var me = this;
				var sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getCurrentEmployee";
				var callback = {
					success:function (oResponse) {
						var oResults = eval("(" + oResponse.responseText + ")");
						if (oResults && oResults.nodeRef) {
							me.currentEmployee = oResults;
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

			onSubscribe: function(e, p_obj) {
				// Intercept before dialog show
				var doBeforeDialogShow = function(p_form, p_dialog) {
					Alfresco.util.populateHTML(
						[ p_dialog.id + "-form-container_h", this.msg("label.subscribe.title") ]
					);
				};

				var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
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
						width:"50em",
						templateUrl:templateUrl,
						actionUrl:null,
						destroyOnHide:true,
						doBeforeDialogShow:{
							fn:doBeforeDialogShow,
							scope:this
						},
						onSuccess:{
							fn:function (response) {
//									YAHOO.Bubbling.fire("dataItemCreated", // обновить данные в гриде
//										{
//											nodeRef:response.json.persistedObject,
//											bubblingLabel:this.options.bubblingLabel
//										});
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
							},
							scope:this
						}
					}).show();
			}
		});
})();