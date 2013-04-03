<script type="text/javascript">//<![CDATA[

LogicECM.form = LogicECM.form || {};

LogicECM.form.fieldPrefix = "${args.htmlid?js_string}_";
LogicECM.form.highlightFields = ${field.value};

(function() {

    YAHOO.Bubbling.on("formContentReady", function (){
        for (var i = 0; i < LogicECM.form.highlightFields.length; i++) {
            var field = LogicECM.form.fieldPrefix + LogicECM.form.highlightFields[i];
            var el = YAHOO.util.Dom.getAncestorByClassName(field, "form-field");
            YAHOO.util.Dom.addClass(el, "highlight_field");
        }
    });

})();
//]]></script>
