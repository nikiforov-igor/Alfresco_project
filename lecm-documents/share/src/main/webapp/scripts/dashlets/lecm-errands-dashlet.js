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
            doubleClickLock: false,
            options: {
                itemType: "lecm-errands:document",
                destination: null,
                parentDoc: null
            },

            onAddErrandClick: function Errands_onAddErrandsClick(args) {
                if (this.doubleClickLock) return;
                this.doubleClickLock = true;
                var destination = this.options.destination,
                    itemType = this.options.itemType;

                // Intercept before dialog show
                var doBeforeDialogShow = function (p_form, p_dialog) {
                    Alfresco.util.populateHTML(
                        [ p_dialog.id + "-form-container_h", this.msg("label.create-row.title") ]
                    );
                    Dom.addClass(p_dialog.id + "-form-container", "metadata-form-edit");
                    Dom.addClass(p_dialog.id + "-form-container", "lecm-errands_document");
                    this.doubleClickLock = false;
	                p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
                };

                var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
	            var templateRequestParams = {
		            itemKind: "type",
		            itemId: itemType,
		            destination: destination,
		            mode: "create",
		            formId: "workflow-form",
		            submitType: "json",
		            args: args ? YAHOO.lang.JSON.stringify(args) : {},
		            showCancelButton: true
	            };

                // Using Forms Service, so always create new instance
                var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createErrandDetails");
                createDetails.setOptions(
                    {
                        width: "60em",
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
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.save.success")
                                    });
                                window.location.href = window.location.protocol + "//" + window.location.host +
                                    Alfresco.constants.URL_PAGECONTEXT + "document?nodeRef=" + response.json.persistedObject;
                                this.doubleClickLock = false;
                            },
                            scope: this
                        },
                        onFailure: {
                            fn: function DataGrid_onActionCreate_failure(response) {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.save.failure")
                                    });
                                this.doubleClickLock = false;
                            },
                            scope: this
                        }
                    }).show();
            },

            createChildErrand: function Errands_onAddErrandsClick() {
                var limitElement = Dom.get("errandLimitationDate");
                var limitDate = "";
                if (limitElement){
                    limitDate = limitElement.value;
                }
                var args = {
                    parentDoc: this.options.parentDoc,
                    parentLimitationDate: limitDate
                };

                this.onAddErrandClick(args);
            }
        });
})();