(function(){
    YAHOO.Bubbling.on("armIsForSecretaryChanged", toggleDelegationFields);
    var Dom = YAHOO.util.Dom;
    function toggleDelegationFields(layer, args){
        var formId = args[1].formId;
        var control = args[1].control;
        var checked = control.checkbox.checked;

        var delegationFieldsSet = YAHOO.util.Selector.query(".delegation-settings", Dom.get(formId), true);
        if (delegationFieldsSet) {
            if (checked) {
                Dom.removeClass(delegationFieldsSet, "hidden");
            } else {
                Dom.addClass(delegationFieldsSet, "hidden");
            }
        }
    }
})();