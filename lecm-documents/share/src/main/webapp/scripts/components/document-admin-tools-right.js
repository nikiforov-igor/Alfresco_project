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
 * LogicECM transfer of rights to the document module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Transfer.TransferRight
 */
LogicECM.module.Transfer = LogicECM.module.Transfer || {};

(function()
{
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;


    LogicECM.module.Transfer.TransferRight = function (fieldHtmlId)
    {
        LogicECM.module.Transfer.TransferRight.superclass.constructor.call(this, "LogicECM.module.Transfer.TransferRight", fieldHtmlId, [ "container", "datasource"]);
        this.id = fieldHtmlId;
        this.controlId = fieldHtmlId + "-cntrl";
        return this;
    };

    YAHOO.extend(LogicECM.module.Transfer.TransferRight, Alfresco.component.Base,
        {
            options: {
                documentRef: null,
                bublingLabel: "",
                creatorRef: ""
            },

            id: null,

            controlId: null,
            transfers: {},

            currentEmployee: null,

            onReady: function()
            {
				this.transfers.adminToolsButton = new YAHOO.widget.Button(
														this.controlId + "-admin-tools-right-button",
														{
															type: "menu",
															menu: [
																	{
																		text: this.msg("menu.button.transfer.document"),
																		value: 1,
																		onclick: {
																			fn: this.getIgnoredNode,
																			scope: this
																		}
																	},
																	{
																		text: this.msg("menu.button.show.service.information"),
																		value: 2,
																		onclick: {
																			fn: this.showServiceDocInfo,
																			scope: this
																		}
																	},

																  ],
															disabled: false
														}
												  );
            },

			showServiceDocInfo: function()
			{
				var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "/lecm/document/information/showServiceInformation?nodeRef="+this.options.documentRef;
				Alfresco.util.PopupManager.displayWebscript(
					{
						title: this.msg("title.service.information"),
						method: "GET",
						url: templateUrl,
						properties: {}
					}
				);

			},

            getIgnoredNode: function()
            {
                Alfresco.util.Ajax.request(
				{
					method: "GET",
					url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/document/api/getProperties",
					dataObj: {
						nodeRef: this.options.documentRef
					},
					successCallback: {
						fn:function(response){
							this.options.creatorRef = response.json[0]["creator-ref"];
							this.findFormEmployee();
						},
						scope: this
					},
					failureMessage: "message.failure",
					execScripts:true
				});
            },
            findFormEmployee: function (e, p_obj) {

                var me = this;
                // Intercept before dialog show
                var doBeforeDialogShow = function (p_form, p_dialog) {
                    Alfresco.util.populateHTML(
                        [ p_dialog.id + "-form-container_h", this.msg("label.transfer.edit.title") ]
                    );
                };

                var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&ignoreNodes={ignoreNodes}&showCancelButton=true",
                    {
                        itemKind: "node",
                        itemId: this.options.documentRef,
                        mode: "edit",
                        formId: "transfer-right",
                        submitType: "json",
                        ignoreNodes: this.options.creatorRef
                    });

//				// Using Forms Service, so always create new instance
                var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails");
                createDetails.setOptions(
                    {
                        width: "50em",
                        templateUrl: templateUrl,
                        actionUrl: null,
                        destroyOnHide: true,
                        doBeforeDialogShow: {
                            fn: doBeforeDialogShow,
                            scope: this
                        },
                        onSuccess: {
                            fn: function (response) {
                                if (me.options.bublingLabel != null) {
                                }

                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.transfer.edit.success")
                                    });
                            },
                            scope: this
                        },
                        onFailure: {
                            fn: function (response) {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.transfer.edit.failure")
                                    });
                            },
                            scope: this
                        }
                    }).show();
            }
        });
})();