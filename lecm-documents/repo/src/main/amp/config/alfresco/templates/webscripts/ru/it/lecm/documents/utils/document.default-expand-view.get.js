(function () {
    var type = args["docType"];
    var DEFAULT_EXPANDED_COMPONENT_KEY = "lecm.form.defaultExpandedComponent";
    var defaultExpandedComponent = base.getGlobalProperty(DEFAULT_EXPANDED_COMPONENT_KEY + "." + type.toString());
    if (!defaultExpandedComponent) {
        defaultExpandedComponent = base.getGlobalProperty(DEFAULT_EXPANDED_COMPONENT_KEY);
    }
    model.result = defaultExpandedComponent;
})();