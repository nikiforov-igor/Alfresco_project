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
 * LogicECM Connection module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Members
 */
LogicECM.module.Members = LogicECM.module.Members || {};

(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;


    LogicECM.module.Members.DocumentMembers = function (fieldHtmlId) {
        LogicECM.module.Members.DocumentMembers.superclass.constructor.call(this, "LogicECM.module.Members.DocumentMembers", fieldHtmlId, [ "container", "datasource"]);
        this.id = fieldHtmlId;
        this.controlId = fieldHtmlId + "-cntrl";
        return this;
    };

    YAHOO.extend(LogicECM.module.Members.DocumentMembers, Alfresco.component.Base,
        {
            options: {
                documentNodeRef: null,
                documentMembersFolderRef: null,
                datagridBublingLabel: null
            },

            id: null,

            controlId: null,

            memberButton: null,

            onReady: function () {
                this.memberButton = Alfresco.util.createYUIButton(this, this.controlId + "-add-member-button", this.onAdd.bind(this), {}, Dom.get(this.controlId + "-add-member-button"));
            },

            onAdd: function (e, p_obj) {
                var me = this;
                // Intercept before dialog show
                var doBeforeDialogShow = function (p_form, p_dialog) {
                    Alfresco.util.populateHTML(
                        [ p_dialog.id + "-form-container_h", this.msg("label.member.add.title") ]
                    );
                };

                var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&ignoreNodes={ignoreNodes}&showCancelButton=true",
                    {
                        itemKind: "type",
                        itemId: "lecm-doc-members:member",
                        destination: this.options.documentMembersFolderRef,
                        mode: "create",
                        formId: this.id + "-create-form",
                        submitType: "json",
                        ignoreNodes: this.options.documentMembersFolderRef
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
                                if (me.options.datagridBublingLabel != null) {
                                    /*YAHOO.Bubbling.fire("dataItemCreated", // обновить данные в гриде
                                        {
                                            nodeRef: response.json.persistedObject,
                                            bubblingLabel: me.options.datagridBublingLabel
                                        });*/
                                    YAHOO.Bubbling.fire("datagridRefresh",
                                        {
                                            bubblingLabel:me.options.bubblingLabel
                                        });
                                }

                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.member.add.success")
                                    });
                            },
                            scope: this
                        },
                        onFailure: {
                            fn: function (response) {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.member.add.failure")
                                    });
                            },
                            scope: this
                        }
                    }).show();
            }
        });
})();