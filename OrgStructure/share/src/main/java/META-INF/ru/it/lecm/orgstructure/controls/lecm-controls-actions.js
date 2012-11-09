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
 * LogicECM Base module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Orgstructure
 */
LogicECM.module.Orgstructure = LogicECM.module.Orgstructure || {};

(function () {
    /**
     * YUI Library aliases
     */
    var Bubbling = YAHOO.Bubbling;
    var $html = Alfresco.util.encodeHTML;

    /**
     * LogicECM.module.Base.DataActions implementation
     */
    LogicECM.module.Orgstructure.CtrlActions = {};
    LogicECM.module.Orgstructure.CtrlActions.prototype =
    {
        currentActionsMenu: null,
        showingMoreActions: null,
        deferredActionsMenu: null,

        onActionEdit:function Ctrl_Edit(item) {
            var me = this;
            // Intercept before dialog show
            var doBeforeDialogShow = function Ctrl_onActionEdit_doBeforeDialogShow(p_form, p_dialog) {
                Alfresco.util.populateHTML(
                    [ p_dialog.id + "-dialogTitle", this.msg("label.edit-row.title") ]
                );
            };

            var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&mode={mode}&submitType={submitType}&showCancelButton=true",
                {
                    itemKind:"node",
                    itemId:item.nodeRef,
                    mode:"edit",
                    submitType:"json"
                });

            // Using Forms Service, so always create new instance
            var editDetails = new Alfresco.module.SimpleDialog(this.id + "-editDetails");
            editDetails.setOptions(
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
                        fn:function Ctrl_onActionEdit_success(response) {
                            me.updateRows();
                        },
                        scope:this
                    },
                    onFailure:{
                        fn:function Ctrl_onActionEdit_failure(response) {
                            Alfresco.util.PopupManager.displayMessage(
                                {
                                    text:this.msg("message.details.failure")
                                });
                        },
                        scope:this
                    }
                }).show();
        },

        fnRenderCellActions:function Ctrl_fnRenderCellActions() {
            var scope = this;

            return function Ctrl_renderCellActions(elCell, oRecord, oColumn, oData) {
                Dom.setStyle(elCell, "width", oColumn.width + "px");
                Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

                elCell.innerHTML = '<div id="' + scope.id + '-actions-' + oRecord.getId() + '" class="hidden"></div>';
            };
        },

        onEventHighlightRow:function Ctrl_onEventHighlightRow(oArgs) {
            var elActions = Dom.get(this.id + "-actions-" + oArgs.target.id);

            // Inject the correct action elements into the actionsId element
            if (elActions && elActions.firstChild === null) {
                // Call through to get the row highlighted by YUI
                this.table.onEventHighlightRow.call(this.table, oArgs);

                // Clone the actionSet template node from the DOM
                var record = this.table.getRecord(oArgs.target.id),
                    clone = Dom.get(this.id + "-actionSet").cloneNode(true);

                // Token replacement
                clone.innerHTML = YAHOO.lang.substitute(window.unescape(clone.innerHTML), this.getActionUrls(record));

                // Generate an id
                clone.id = elActions.id + "_a";

                // Simple view by default
                Dom.addClass(clone, "simple");

                // Trim the items in the clone depending on the user's access
                var actionLabels = record.getData("actionLabels") || {};

                // Remove any actions the user doesn't have permission for
                var actions = YAHOO.util.Selector.query("div", clone),
                    action, aTag, spanTag, actionPermissions, aP, i, ii, j, jj;

                for (i = 0, ii = actions.length; i < ii; i++) {
                    action = actions[i];
                    aTag = action.firstChild;
                    spanTag = aTag.firstChild;
                    if (spanTag && actionLabels[action.className]) {
                        spanTag.innerHTML = $html(actionLabels[action.className]);
                    }
                }
                elActions.appendChild(clone);
            }

            this.currentActionsMenu = elActions;
            // Show the actions
            Dom.removeClass(elActions, "hidden");
            this.deferredActionsMenu = null;
        },

        getActionUrls:function Ctrl_getActionUrls(record) {
            var recordData = YAHOO.lang.isFunction(record.getData) ? record.getData() : record,
                nodeRef = recordData.nodeRef;
            return (
            {
                editMetadataUrl:"edit-dataitem?nodeRef=" + nodeRef
            });
        },

        onEventUnhighlightRow:function Ctrl_onEventUnhighlightRow(oArgs) {
            // Call through to get the row unhighlighted by YUI
            this.table.onEventUnhighlightRow.call(this.table, oArgs);

            var elActions = Dom.get(this.id + "-actions-" + (oArgs.target.id));

            Dom.addClass(elActions, "hidden");
            this.deferredActionsMenu = null;
        }
    };

    /**
     * Augment prototype with Common Actions module
     */
    YAHOO.lang.augmentProto(LogicECM.module.Orgstructure.CtrlActions, LogicECM.module.Base.DataActions);
})();