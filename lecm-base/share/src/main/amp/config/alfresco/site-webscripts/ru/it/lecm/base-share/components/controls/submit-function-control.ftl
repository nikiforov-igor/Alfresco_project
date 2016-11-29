<#assign params = field.control.params/>
<script type="text/javascript">//<![CDATA[
(function () {
    var currentForm;
    function onContentReady(layer, args) {
        currentForm = args[1];

        LogicECM.module.Base.Util.loadScripts([
            'scripts/lecm-base/components/submit-function-control.js'
        <#if params.scripts??>
            <#list params.scripts?split(",") as js>
                ,'${js}'
            </#list>
        </#if>

        ], createControl);
    }

    function createControl() {
        var control = new LogicECM.module.SubmitFunction("${fieldHtmlId}").setOptions({
            submitFunction: ${params['submitFunction']},
            form: currentForm,
            nodeRef: "${form.arguments.itemId!""}",
            mode: "${form.mode}"
        });

        control.init();
        YAHOO.Bubbling.unsubscribe("formContentReady", onContentReady);
    }

    YAHOO.Bubbling.on("formContentReady", onContentReady);
})();
//]]></script>
