/**
 * Created by ALoginov on 15.03.2017.
 */
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

LogicECM.ORD = LogicECM.ORD || {};

(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector,
        Bubbling = YAHOO.Bubbling;
    var $html = Alfresco.util.encodeHTML,
        $links = Alfresco.util.activateLinks,
        $combine = Alfresco.util.combinePaths,
        $userProfile = Alfresco.util.userProfileLink;

    LogicECM.ORD.PointsCreateFormDatagrid = function (htmlId) {
        return LogicECM.ORD.PointsCreateFormDatagrid.superclass.constructor.call(this, htmlId);
    };

    YAHOO.lang.extend(LogicECM.ORD.PointsCreateFormDatagrid, LogicECM.module.Base.AssociationDataGrid);
    YAHOO.lang.augmentObject(LogicECM.ORD.PointsCreateFormDatagrid.prototype, {

        showCreateDialog: function (meta, callback, successMessage) {
            if (this.editDialogOpening) return;
            this.editDialogOpening = true;
            var me = this;
            this.formId = this.id.substring(0, this.id.indexOf("_assoc"));
            var executeDate = new Date(Dom.get(this.formId + "_prop_lecm-eds-document_execution-date").value);
            var subject = Dom.get(this.formId + "_assoc_lecm-document_subject-assoc").value;
            var controller = Dom.get(this.formId + "_assoc_lecm-ord_controller-assoc").value;
            var doBeforeDialogShow = function DataGrid_onActionEdit_doBeforeDialogShow(p_form, p_dialog) {
                var addMsg = meta.addMessage;
                var contId = p_dialog.id + "-form-container";
                Alfresco.util.populateHTML(
                    [contId + "_h", addMsg ? addMsg : this.msg(this.options.createFormTitleMsg)]
                );
                if (meta.itemType && meta.itemType != "") {
                    Dom.addClass(contId, meta.itemType.replace(":", "_") + "_edit");
                }
                me.editDialogOpening = false;
                p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
            };

            var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
            var templateRequestParams = {
                itemKind: "type",
                args: JSON.stringify({
                    "prop_lecm-ord-table-structure_execution-date": executeDate,
                    "assoc_lecm-ord-table-structure_subject-assoc": subject,
                    "assoc_lecm-ord-table-structure_controller-assoc": controller
                }),
                itemId: meta.itemType,
                destination: meta.nodeRef,
                mode: "create",
                formId: meta.createFormId != null ? meta.createFormId : "",
                submitType: "json",
                showCancelButton: true,
                showCaption: false
            };

            // Using Forms Service, so always create new instance
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
                        fn: function DataGrid_onActionCreate_success(response) {
                            if (callback) {// вызов дополнительного события
                                callback.call(this, response.json.persistedObject);
                            } else { // вызов события по умолчанию
                                YAHOO.Bubbling.fire("nodeCreated",
                                    {
                                        nodeRef: response.json.persistedObject,
                                        bubblingLabel: this.options.bubblingLabel
                                    });
                                YAHOO.Bubbling.fire("dataItemCreated", // обновить данные в гриде
                                    {
                                        nodeRef: response.json.persistedObject,
                                        bubblingLabel: this.options.bubblingLabel
                                    });
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg(successMessage ? successMessage : "message.save.success")
                                    });
                            }
                            this.editDialogOpening = false;
                        },
                        scope: this
                    },
                    onFailure: {
                        fn: function DataGrid_onActionCreate_failure(response) {
                            LogicECM.module.Base.Util.displayErrorMessageWithDetails(me.msg("logicecm.base.error"), me.msg("message.save.failure"), response.json.message);
                            me.editDialogOpening = false;
                            this.widgets.cancelButton.set("disabled", false);
                        },
                        scope: createDetails
                    }
                }).show();
        }
    }, true)

})();
