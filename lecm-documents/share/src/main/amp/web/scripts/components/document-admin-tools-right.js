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

            onReady: function () {
                this.transfers.adminToolsButton = new YAHOO.widget.Button(this.controlId + "-admin-tools-right-button",
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
                            {
                                text: this.msg("menu.button.dynamic.roles"),
                                value: 3,
                                onclick: {
                                    fn: this.managementDynamicRoles,
                                    scope: this
                                }
                            },
                            {
                                text: this.msg("menu.button.delete.document"),
                                value: 4,
                                onclick: {
                                    fn: this.deleteDocument,
                                    scope: this
                                }
                            },
                            {
                                text: this.msg("menu.button.show.document.properties"),
                                value: 5,
                                onclick: {
                                    fn: this.showAllDocumentProperties,
                                    scope: this
                                }
                            }
                        ],
                        disabled: false
                    }
                );
            },

            showAllDocumentProperties: function() {
                window.open(Alfresco.constants.URL_PAGECONTEXT + 'console/admin-console/node-browser#state=panel%3Dview%26nodeRef%3D' + encodeURIComponent(this.options.documentRef), '_blank');
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

            managementDynamicRoles: function() {
				var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "/lecm/document/admin/managementDynamicRoles?nodeRef="+this.options.documentRef;
				Alfresco.util.PopupManager.displayWebscript(
					{
						title: this.msg("title.service.dynamic.roles"),
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
	                p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
                };

                var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "components/form";
	            var templateRequestParams = {
		            itemKind: "node",
		            itemId: this.options.documentRef,
		            mode: "edit",
		            formId: "transfer-right",
		            submitType: "json",
		            ignoreNodes: this.options.creatorRef,
		            showCancelButton: true,
					showCaption: false
	            };

//				// Using Forms Service, so always create new instance
                var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails");
                createDetails.setOptions(
                    {
                        width: "50em",
                        templateUrl: templateUrl,
	                    templateRequestParams: templateRequestParams,
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
            },

            deleteDocument: function () {
                var me = this;
                Alfresco.util.PopupManager.displayPrompt(
                    {
                        title: this.msg("msg.full_delete.title"),
                        text: this.msg("msg.full_delete.confirm"),
                        buttons: [
                            {
                                text: this.msg("button.yes"),
                                handler: function dlA_onAction_action() {
                                    this.destroy();
                                    Alfresco.util.Ajax.request(
                                        {
                                            method: "DELETE",
                                            url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/document-removal/document/delete?nodeRef=" + me.options.documentRef,
                                            successCallback: {
                                                fn: function () {
                                                    window.location.reload();
                                                },
                                                scope: this
                                            },
                                            failureMessage: "message.failure.delete",
                                            execScripts: true
                                        });
                                }
                            },
                            {
                                text: this.msg("button.cancel"),
                                handler: function ()
                                {
                                    this.destroy();
                                },
                                isDefault: true
                            }
                        ]
                    });
            }
        });
})();