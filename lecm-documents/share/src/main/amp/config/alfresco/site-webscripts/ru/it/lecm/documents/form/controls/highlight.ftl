<script type="text/javascript">//<![CDATA[

LogicECM.form = LogicECM.form || {};

LogicECM.form.fieldPrefix = "${args.htmlid?js_string}_";
LogicECM.form.highlightFields = ${field.value};

(function() {

    for (var i = 0; i < LogicECM.form.highlightFields.length; i++) {
        var field = LogicECM.form.fieldPrefix + LogicECM.form.highlightFields[i];
        YAHOO.util.Event.onContentReady(field, function() {
            var el = YAHOO.util.Dom.getAncestorByClassName(field, "control");
            YAHOO.util.Dom.addClass(el, "highlight");

        });
    }

})();
//]]></script>
