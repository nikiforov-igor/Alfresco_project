(function () {
    YAHOO.Bubbling.on("armDelegationFieldsHandlerScriptLoaded", init);
    var Dom = YAHOO.util.Dom;

    function init(layer, args) {
        var params = args[1];
        if (params.bubblingName) {
            YAHOO.Bubbling.on(params.bubblingName, toggleDelegationFields, params);
        }
    }

    function toggleDelegationFields(layer, args, params) {
        var formId = args[1].formId;
        var control = args[1].control;
        var checked = control.checkbox.checked;
        var delegationSourceArmDiv, delegationSourceNodeDiv, delegationHtmlUrlDiv;
        var delegationSourceArmField = Dom.get(formId + "_" + params.delegation_source_arm_field);
        var delegationSourceNodeField = Dom.get(formId + "_" + params.delegation_source_node_field);
        var delegationHtmlUrlField = Dom.get(formId + "_" + params.delegation_html_url_field);
        if (delegationSourceArmField) {
            delegationSourceArmDiv = delegationSourceArmField.parentElement.parentElement.parentElement;
        }
        if (delegationSourceNodeField) {
            delegationSourceNodeDiv = delegationSourceNodeField.parentElement.parentElement;
        }
        if (delegationHtmlUrlField) {
            delegationHtmlUrlDiv = delegationHtmlUrlField.parentElement.parentElement.parentElement;
        }
        if (checked) {
            if (delegationSourceArmDiv) {
                Dom.removeClass(delegationSourceArmDiv, "hidden");
            }
            if (delegationSourceNodeDiv) {
                Dom.removeClass(delegationSourceNodeDiv, "hidden");
            }
            if (delegationHtmlUrlDiv) {
                Dom.removeClass(delegationHtmlUrlDiv, "hidden");
            }
        } else {
            if (delegationSourceArmDiv) {
                Dom.addClass(delegationSourceArmDiv, "hidden");
            }
            if (delegationSourceNodeDiv) {
                Dom.addClass(delegationSourceNodeDiv, "hidden");
            }
            if (delegationHtmlUrlDiv) {
                Dom.addClass(delegationHtmlUrlDiv, "hidden");
            }
        }
    }
})();