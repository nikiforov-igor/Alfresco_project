<#--
Макрос для добавления на страницу криптоапплета для работы с ЭП
-->
<#macro initApplet>
<script type="text/javascript">
	(function() {
		 LogicECM.module.Base.Util.loadScripts([
			'scripts/lecm-base/third-party/cadesplugin_api.js',
			'scripts/signed-docflow/CryptoAppletModule.js'
		 ], function() {
			CryptoApplet = new LogicECM.CryptoApplet('crypto-applet-module');
			CryptoApplet.onReady();
		});
	})();
</script>

</#macro>
