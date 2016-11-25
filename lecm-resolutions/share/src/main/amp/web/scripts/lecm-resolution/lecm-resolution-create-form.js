(function () {
    LogicECM.module.Base.Util.loadCSS([
        'css/lecm-errands/errands-create-form.css'
    ]);

    var Dom = YAHOO.util.Dom,
        Selector = YAHOO.util.Selector,
        Bubbling = YAHOO.Bubbling;
    var formId, formButtons;

    Bubbling.on('saveDraftResolutionButtonClick', saveDraft);
    Bubbling.on('resolutionCreateFormScriptLoaded', init);

    function saveDraft() {
        var isDraft = Dom.getElementBy(function (el) {
            return el.name == "prop_lecm-resolutions_is-draft";
        }, 'input', formId);
        var routeButton = Selector.query(".yui-submit-button", formButtons, true);

        isDraft.value = "true";
        routeButton.click();
    }

    function init(layer, args) {
        formId = args[1].formId;
        var submitButtonElement = Dom.get(formId + "-form-submit-button");
        submitButtonElement.innerHTML = Alfresco.util.message("label.route-errand");
    }
})();