<#--
Макрос для добавления на страницу криптоапплета для работы с ЭП
-->
<#macro initApplet>
<script type="text/javascript">
	function afterLoad(){
		CryptoApplet = new LogicECM.CryptoApplet('crypto-applet-module');
		CryptoApplet.onReady();
	}
</script>
<applet codebase="/share/scripts/signed-docflow"
		code="ru.businesslogic.crypto.userinterface.CryptoApplet.class"
		archive="/share/scripts/signed-docflow/ITStampApplet.jar"
		name="signApplet"
		width=1
		height=1>
	<param name="signOnLoad" value="false"/>
	<param name="debug" value="true"/>
	<param name="providerType" value="CSP_CRYPTOPRO"/>
	<param name="doAfterLoad" value="true"/>
</applet>
</#macro>
