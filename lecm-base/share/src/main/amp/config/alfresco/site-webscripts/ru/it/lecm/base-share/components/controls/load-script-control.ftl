<#assign formId=args.htmlid?js_string?html/>
<#assign fieldId=field.id!"">
<#assign params = field.control.params/>
<#if params.scripts??>

	<#assign scriptLoadedFireAction = "scriptLoaded"/>
	<#if field.control.params.scriptLoadedFireAction??>
		<#assign scriptLoadedFireAction = field.control.params.scriptLoadedFireAction/>
	</#if>

<script type="text/javascript">//<![CDATA[
(function () {
    function init() {
        LogicECM.module.Base.Util.loadScripts([
			<#list params.scripts?split(",") as js>
                '${js}'<#if js_has_next>,</#if>
			</#list>
        ], callback);
    }

    function callback() {
        YAHOO.Bubbling.fire("${scriptLoadedFireAction}", {
            formId: "${formId}",
            fieldId: "${fieldId}"
        });
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>
</#if>