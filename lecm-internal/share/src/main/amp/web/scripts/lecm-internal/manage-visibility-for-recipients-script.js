(function () {
    var Dom = YAHOO.util.Dom;
    var Selector = YAHOO.util.Selector;
    YAHOO.Bubbling.on("recipientsVisibilityManagerLoaded", process);

    function process(layer, args) {
        var formID = args[1].formId;
        var sets = args[1].setsToProcess;
        sets = sets ? sets.split(",") : [];
        if (formID && sets.length) {
            YAHOO.util.Event.onContentReady(formID + "-form-container", function () {
                var form = Dom.get(formID + "-form-container");
                sets.forEach(function(setId){
                    var setEl = Selector.query("." + formID + "-form-panel." + setId, form, true);
                    if (setEl) {
                        Dom.removeClass(setEl, "hidden1");
                    }
                });
            });
        }
    }

})();