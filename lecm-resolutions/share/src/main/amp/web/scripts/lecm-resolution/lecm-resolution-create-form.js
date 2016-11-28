(function () {
    LogicECM.module.Base.Util.loadCSS([
        'css/lecm-errands/errands-create-form.css'
    ]);

    var Dom = YAHOO.util.Dom,
        Selector = YAHOO.util.Selector,
        Bubbling = YAHOO.Bubbling;
    var formId, formButtons;

    Bubbling.on('saveDraftResolutionButtonClick', saveDraft);
    Bubbling.on('sendResolution', sendResolution);
    Bubbling.on('resolutionCreateFormScriptLoaded', init);

    function saveDraft(layer, args) {
        if (args[1] && args[1].formId) {
            var form = Dom.get(args[1].formId + "-form");
            if (form && form["prop_lecm-resolutions_is-draft"]) {
                form["prop_lecm-resolutions_is-draft"].value = true;
            }
        }
        var routeButton = Selector.query(".yui-submit-button", formButtons, true);
        routeButton.click();
    }

    function sendResolution(layer, args) {
        if (args[1] && args[1].form) {
            var formData = args[1].form.getFormData();
            if (formData) {
                var resolutionExecutionDate = {
                    "date-radio": formData["prop_lecm-resolutions_limitation-date-radio"],
                    "date-days": formData["prop_lecm-resolutions_limitation-date-days"],
                    "date-type": formData["prop_lecm-resolutions_limitation-date-type"],
                    "date": formData["prop_lecm-resolutions_limitation-date"]
                };

                var errandsJson = eval(formData["prop_lecm-resolutions_errands-json"]);
                var errandsExecutionDates = [];
                if (errandsJson && errandsJson.length) {
                    errandsJson.forEach(function (obj) {
                        errandsExecutionDates.push({
                            "date-radio": obj["prop_lecm-errands_limitation-date-radio"],
                            "date-days": obj["prop_lecm-errands_limitation-date-days"],
                            "date-type": obj["prop_lecm-errands_limitation-date-type"],
                            "date": obj["prop_lecm-errands_limitation-date"]
                        });
                    })
                }

                Alfresco.util.Ajax.jsonPost(
                    {
                        url: Alfresco.constants.PROXY_URI + "lecm/resolutions/checkExecutionDates",
                        dataObj: {
                            resolutionExecutionDate: resolutionExecutionDate,
                            errandsExecutionDates: errandsExecutionDates
                        },
                        successCallback: {
                            fn: function (response) {
                                if (response.json && response.json.length) {

                                }
                            }
                        }
                    });
            }
        }
    }

    function init(layer, args) {
        formId = args[1].formId;
        formButtons = Dom.get(formId + "-form-buttons");
        var submitButtonElement = Dom.get(formId + "-form-submit-button");
        submitButtonElement.innerHTML = Alfresco.util.message("label.route-errand");
    }
})();