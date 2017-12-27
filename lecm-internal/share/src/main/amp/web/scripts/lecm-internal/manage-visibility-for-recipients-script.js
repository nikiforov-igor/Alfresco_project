(function () {
    var Dom = YAHOO.util.Dom;
    var Selector = YAHOO.util.Selector;
    YAHOO.Bubbling.on("recipientsVisibilityManagerLoaded", process);

    function process(layer, args) {
        var formID = args[1].formId;
        var fieldsToHide = args[1].fieldsToHide;
        if (formID && fieldsToHide) {
            fieldsToHide = fieldsToHide.split(",");
            var form = Dom.get(formID + "-form-container");
            if (form) {
                fieldsToHide.forEach(function (field) {
                    var els = Selector.query("[id^='" + formID + "_" + field + "'][class*='control']", form);
                    if (els) {
                        els.forEach(function(el) {
                            Dom.setStyle(el, "display", "none");
                            Dom.addClass(el, "hidden");
                        });
                    }
                });
            }
        }
    }

})();