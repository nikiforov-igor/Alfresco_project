<script type="text/javascript">//<![CDATA[
(function() {
    var submitButton;

    function init() {
        LogicECM.module.Base.Util.loadScripts([
            'scripts/contractors/contractors-check-duplicates.js'
        ], createControl);
    }

    function createControl() {
        var control = new LogicECM.module.Contractors.CheckDuplicates("${args.htmlid}-contractors-check-duplicates").setOptions({
            nodeRef: "${form.arguments.itemId!""}",
            submitButton: submitButton,
            rootElement: "${args.htmlid}"
        });

        control.init();
        YAHOO.Bubbling.unsubscribe("formContentReady", onContentReady);
    }

    function onContentReady(layer, args) {
        submitButton = args[1].buttons.submit;
        init();
    }

    YAHOO.Bubbling.on("formContentReady", onContentReady);





})();
//]]></script>
