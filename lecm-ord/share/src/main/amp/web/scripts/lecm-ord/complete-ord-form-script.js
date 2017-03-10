(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Bubbling = YAHOO.Bubbling,
        Selector = YAHOO.util.Selector;

    LogicECM.module.Base.Util.loadCSS([
        'css/lecm-ord/ord-item-completion-form-script.css'
    ]);
    Bubbling.on('completeORDWFScriptLoaded', init);

    function init(layer, args) {
        var formId = args[1].formId;
        Event.onContentReady(formId + "-form-submit-button", function () {
            var submitButtonElement = Dom.get(formId + "-form-submit-button");
            submitButtonElement.innerHTML = Alfresco.util.message("ord.register.dialog.button.continue");
        });
    }

})();