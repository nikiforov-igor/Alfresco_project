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
 * LogicECM Connection module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Members
 */
LogicECM.module.Members = LogicECM.module.Members || {};

(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Bubbling = YAHOO.Bubbling;

    LogicECM.module.Members.DocumentMembers = function (id) {
        LogicECM.module.Members.DocumentMembers.superclass.constructor.call(this, "LogicECM.module.Members.DocumentMembers", id, [ "container", "datasource"]);
        this.id = id;
        this.currentMembers = [];

        YAHOO.Bubbling.on("memberCreated", this.onMembersUpdate, this);
        YAHOO.Bubbling.on("onMembersUpdate", this.onMembersUpdate, this);
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

                this.modules.actions = new LogicECM.module.Base.Actions();

                var me = this;
                var fnActionHandler = function DataGrid_fnActionHandler(layer, args)
                {
                    var owner = Bubbling.getOwnerByTagName(args[1].anchor, "div");
                    if (owner !== null)
                    {
                        var nodeName = owner.getAttribute('data-name');
                        var nodeRef = owner.getAttribute('data-noderef');
                        if (typeof me[owner.className] == "function" && nodeName != null && nodeRef != null)
                        {
                            me[owner.className].call(me, nodeName, nodeRef);
                        }
                    }
                    return true;
                };
                Bubbling.addDefaultAction("list-action-link", fnActionHandler);

                var rows = Dom.getElementsByClassName('detail-list-item');
                for (var i = 0; i < rows.length; i++)
                {
                    Event.addListener(rows[i], "mouseover", this.onEventHighlightRow, {row: rows[i]}, this);
                    Event.addListener(rows[i], "mouseout", this.onEventUnhighlightRow, {row: rows[i]}, this);
                }

            },

            onAdd: function (e, p_obj) {
                if (this.doubleClickLock) return;
                this.doubleClickLock = true;
                this._updateCurrentMembers();
                var me = this;
                // Intercept before dialog show
                var doBeforeDialogShow = function (p_form, p_dialog) {
                    Alfresco.util.populateHTML(
                        [ p_dialog.id + "-form-container_h", this.msg("label.member.add.title") ]
                    );
                    this.doubleClickLock = false;
                    p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
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
                                if (me.options.datagridBublingLabel != null) {
                                    YAHOO.Bubbling.fire("memberCreated",
                                        {
                                            nodeRef: response.json.persistedObject
                                        });
                                }

                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.member.add.success"),
                                        displayTime: 3
                                    });
                                this.doubleClickLock = false;
                            },
                            scope: this
                        },
                        onFailure: {
                            fn: function (response) {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.member.add.failure"),
                                        displayTime: 3
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
                                    var customRegion = container.parentElement;
                                    customRegion.innerHTML = "";
                                    customRegion.innerHTML = response.serverResponse.responseText;

                                    this._updateCurrentMembers();
                                }
                            },
                            scope: this
                        },
                        failureMessage: this.msg("message.failure"),
                        scope: this,
                        execScripts: true
                    });
            },

            onEventHighlightRow: function(e, itemInfo) {
                Dom.addClass(itemInfo.row, "highlighted");
            },

            onEventUnhighlightRow: function(e, itemInfo) {
                Dom.removeClass(itemInfo.row, "highlighted");
            },

            onActionDelete: function (name, noderef) {
                if (noderef != null && name != null) {
                    var me = this;

                    var fnActionDeleteConfirm = function DataGridActions__onActionDelete_confirm(nodeRef) {
                        this.modules.actions.genericAction(
                            {
                                success:{
                                    event:{
                                        name:"onMembersUpdate"
                                    },
                                    message:me.msg("message.delete.success")
                                },
                                failure:{
                                    message:me.msg("message.delete.failure")
                                },
                                webscript:{
                                    method:Alfresco.util.Ajax.DELETE,
                                    name:"delete",
                                    queryString:"full=true"
                                },
                                config:{
                                    requestContentType:Alfresco.util.Ajax.JSON,
                                    dataObj:{
                                        nodeRefs:[nodeRef]
                                    }
                                }
                            });
                    };

                    Alfresco.util.PopupManager.displayPrompt(
                        {
                            title:this.msg("message.confirm.delete.title"),
                            text: this.msg("message.confirm.delete.description", '"' + name + '"'),
                            buttons:[
                                {
                                    text:this.msg("button.delete"),
                                    handler:function () {
                                        this.destroy();
                                        fnActionDeleteConfirm.call(me, noderef);
                                    }
                                },
                                {
                                    text:this.msg("button.cancel"),
                                    handler:function () {
                                        this.destroy();
                                    },
                                    isDefault:true
                                }
                            ]
                        });
                }
            },

            _updateCurrentMembers: function() {
                this.currentMembers = [];
                var membersRefsDivs = Dom.getElementsByClassName('member-ref');
                for (var index in membersRefsDivs) {
                    if (membersRefsDivs.hasOwnProperty(index)) {
                        this.currentMembers.push(membersRefsDivs[index].innerHTML);
                    }
                }
            }
        });
})();