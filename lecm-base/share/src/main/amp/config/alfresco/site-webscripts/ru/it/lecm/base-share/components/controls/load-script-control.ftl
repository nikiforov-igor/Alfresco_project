<#assign params = field.control.params/>
<#if params.scripts??>
	<script type="text/javascript">//<![CDATA[
	(function()
	{
		function init() {
			LogicECM.module.Base.Util.loadScripts([
				<#list params.scripts?split(",") as js>
					'${js}'<#if js_has_next>,</#if>
				</#list>
			]);
		}

		YAHOO.util.Event.onDOMReady(init);
	})();
	//]]></script>
</#if>