(function () {
    var type = args["docType"];
    var DEFAULT_EXPANDED_COMPONENT_KEY = "lecm.form.defaultExpandedComponent";
    var defaultExpandedComponent = base.getGlobalProperty(DEFAULT_EXPANDED_COMPONENT_KEY + "." + type.toString(), "null");
    if (defaultExpandedComponent == null) {
        defaultExpandedComponent = base.getGlobalProperty(DEFAULT_EXPANDED_COMPONENT_KEY, "null");
    }
    model.result = decode_utf8(defaultExpandedComponent);

    function decode_utf8(s) {
        return decodeURIComponent(escape(s));
    }
})();