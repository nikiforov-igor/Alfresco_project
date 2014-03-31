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


    LogicECM.module.Members.DocumentMembers = function (id) {
        LogicECM.module.Members.DocumentMembers.superclass.constructor.call(this, "LogicECM.module.Members.DocumentMembers", id, [ "container", "datasource"]);
        this.id = id;
        this.currentMembers = [];

        YAHOO.Bubbling.on("memberCreated", this.onMembersUpdate, this);
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

            memberButton: null,

            currentMembers: [],

            doubleClickLock: false,

            onReady: function () {
                this.memberButton = Alfresco.util.createYUIButton(this, this.id + "-addMember-button", this.onAdd.bind(this), {}, Dom.get(this.id + "-addMember-button"));
            },

            onAdd: function (e, p_obj) {
                if (this.doubleClickLock) return;
                this.doubleClickLock = true;
                this.currentMembers = [];
                var membersRefsDivs = Dom.getElementsByClassName('member-ref');
                for (var index in membersRefsDivs) {
                    this.currentMembers.push(membersRefsDivs[index].innerHTML);
                }

                var me = this;
                // Intercept before dialog show
                var doBeforeDialogShow = function (p_form, p_dialog) {
                    Alfresco.util.populateHTML(
                        [ p_dialog.id + "-form-container_h", this.msg("label.member.add.title") ]
                    );
                    this.doubleClickLock = false;
	                p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
                    /*var added = p_dialog.dialog.form['assoc_lecm-doc-members_employee-assoc_added'];
                    if (added != null) {
                        added.value = this.currentMembers.join(",")
                    }
                    var current = p_dialog.dialog.form['assoc_lecm-doc-members_employee-assoc'];
                    if (current != null) {
                        current.value = this.currentMembers.join(",")
                    }*/
                };

                var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
	            var templateRequestParams = {
		            itemKind: "type",
		            itemId: "lecm-doc-members:member",
		            destination: this.options.documentMembersFolderRef,
		            mode: "create",
		            formId: this.id + "-create-form",
		            submitType: "json",
		            ignoreNodes: this.currentMembers.join(","),
		            showCancelButton: true
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
                                if (me.options.datagridBublingLabel != null) {
                                    /*YAHOO.Bubbling.fire("dataItemCreated", // обновить данные в гриде
                                        {
                                            nodeRef:response.json.persistedObject,
                                            bubblingLabel:me.options.datagridBublingLabel
                                        });*/
                                    YAHOO.Bubbling.fire("memberCreated",
                                        {
                                            nodeRef:response.json.persistedObject
                                        });
                                }

                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.member.add.success")
                                    });
                                this.doubleClickLock = false;
                            },
                            scope: this
                        },
                        onFailure: {
                            fn: function (response) {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.member.add.failure")
                                    });
                                this.doubleClickLock = false;
                            },
                            scope: this
                        }
                    }).show();
            },

            onMembersUpdate: function DocumentMembers_onMembersUpdate(layer, args) {
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/members-list",
                        dataObj: {
                            nodeRef: this.options.documentNodeRef,
                            htmlid: this.id + "-" + Alfresco.util.generateDomId()
                        },
                        successCallback: {
                            fn:function(response){
                                var container = Dom.get(this.id);
                                if (container != null) {
                                    container.innerHTML = response.serverResponse.responseText;
                                }
                            },
                            scope: this
                        },
                        failureMessage: this.msg("message.failure"),
                        scope: this,
                        execScripts: true
                    });
            }
        });
})();