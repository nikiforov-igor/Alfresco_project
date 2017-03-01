(function () {
    var type = args["docType"];
    model.result = decode_utf8(base.getGlobalProperty("default_expanded_component." + type.toString(), "null"));

    function decode_utf8(s) {
        return decodeURIComponent(escape(s));
    }
})();