if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Errands = LogicECM.module.Errands || {};
LogicECM.module.Errands.dashlet = LogicECM.module.Errands.dashlet || {};

(function () {
    LogicECM.module.Errands.dashlet.Errands = function Errands_constructor(htmlId) {
        LogicECM.module.Errands.dashlet.Errands.superclass.constructor.call(this, "LogicECM.module.Errands.dashlet.Errands", htmlId, ["button", "container"]);
        return this;
    };

    YAHOO.extend(LogicECM.module.Errands.dashlet.Errands, Alfresco.component.Base,
        {
            options: {
                itemType: "lecm-errands:document",
                destination: null,
                parentDoc: null
            },

            onAddErrandClick: function Errands_onAddErrandsClick() {
                var destination = this.options.destination,
                    itemType = this.options.itemType;

                // Intercept before dialog show
                var doBeforeDialogShow = function (p_form, p_dialog) {
                    Alfresco.util.populateHTML(
                        [ p_dialog.id + "-form-container_h", this.msg("label.create-row.title") ]
                    );
                    Dom.addClass(p_dialog.id + "-form", "errands-form-edit");
                };

                var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true&maxLimit={maxLimit}",
                    {
                        itemKind: "type",
                        itemId: itemType,
                        destination: destination,
                        mode: "create",
                        formId: "workflow-form",
                        submitType: "json"
                    });

                // Using Forms Service, so always create new instance
                var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createErrandDetails");
                createDetails.setOptions(
                    {
                        width: "55em",
                        templateUrl: templateUrl,
                        actionUrl: null,
                        destroyOnHide: true,
                        doBeforeDialogShow: {
                            fn: doBeforeDialogShow,
                            scope: this
                        },
                        onSuccess: {
                            fn: function DataGrid_onActionCreate_success(response) {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.save.success")
                                    });
                                window.location.href = window.location.protocol + "//" + window.location.host +
                                    Alfresco.constants.URL_PAGECONTEXT + "document?nodeRef=" + response.json.persistedObject;
                            },
                            scope: this
                        },
                        onFailure: {
                            fn: function DataGrid_onActionCreate_failure(response) {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.save.failure")
                                    });
                            },
                            scope: this
                        }
                    }).show();
            },

            createChildErrand: function Errands_onAddErrandsClick() {
                Alfresco.util.Ajax.request({
                    method: "GET",
                    url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/errands/getAvailableEmployeesForChildErrand",
                    dataObj: {
                        parentDoc: this.options.parentDoc
                    },
                    successCallback: {
                        fn: onSuccess,
                        scope: this
                    },
                    failureCallback: {
                        fn: onFailure,
                        scope: this
                    },
                    execScripts: true
                });

                function onSuccess(response) {
                    this.onAddErrandClick(response.json.employees);
                }

                function onFailure(response) {
                    this.onAddErrandClick(null);
                }
            }
        });
})();