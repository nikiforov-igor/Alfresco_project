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
            if (this.editDialogOpening) {
                return;
            }
            this.editDialogOpening = true;

            var config = {
                meta: meta,
                callback: callback,
                successMessage: successMessage,
                context: this,
                isAddRowClicked: false,
                dataRow: null,
                useSequentialCreation: this.options.useSequentialCreation
            };

            var args = {};
            this.formId = this.id.substring(0, this.id.indexOf("_assoc"));
            var executeDateField = Dom.get(this.formId + "_prop_lecm-eds-document_execution-date");
            var subjectField = Dom.get(this.formId + "_assoc_lecm-document_subject-assoc");
            var controllerField = Dom.get(this.formId + "_assoc_lecm-ord_controller-assoc");
            if (executeDateField && executeDateField.value) {
                args["prop_lecm-ord-table-structure_execution-date"] = new Date(executeDateField.value)
            }
            if (subjectField && subjectField.value) {
                args["assoc_lecm-ord-table-structure_subject-assoc"] = subjectField.value;
            }
            if (controllerField && controllerField.value) {
                args["assoc_lecm-ord-table-structure_controller-assoc"] = controllerField.value;
            }

            var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
            var templateRequestParams = {
                itemKind: "type",
                args: JSON.stringify(args),
                itemId: meta.itemType,
                destination: meta.nodeRef,
                mode: "create",
                formId: meta.createFormId != null ? meta.createFormId : "",
                submitType: "json",
                showCancelButton: true,
                showCaption: false
            };

            // Using Forms Service, so always create new instance
            var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails-" + Alfresco.util.generateDomId());
            createDetails.setOptions(
                {
                    width: "50em",
                    templateUrl: templateUrl,
                    templateRequestParams: templateRequestParams,
                    actionUrl: null,
                    destroyOnHide: true,
                    doBeforeDialogShow: {
                        fn: this.doBeforeCreateDialogShow,
                        scope: this,
                        obj: config
                    },
                    onSuccess: {
                        scope: this,
                        fn: function (response, config) {
                            this.doOnCreateNode(response, config, null);
                        },
                        obj: config
                    },
                    onFailure: {
                        fn: function  (response, config) {
                            this.doOnFailure(response, config);
                        },
                        scope: this,
                        obj: config
                    }
                }).show();
        }
    }, true)

})();
