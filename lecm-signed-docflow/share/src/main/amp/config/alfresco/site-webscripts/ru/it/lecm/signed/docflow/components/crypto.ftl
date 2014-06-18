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

<object type="application/x-java-applet" height="1" width="1" name="signApplet">
	<param name="codebase" valu="/share/scripts/signed-docflow"/>
	<param name="code" value="ru.businesslogic.crypto.userinterface.CryptoApplet.class" />
	<param name="archive" value="/share/scripts/signed-docflow/ITStampApplet.jar" />
	<param name="signOnLoad" value="false"/>
	<param name="debug" value="true"/>
	<param name="providerType" value="CSP_CRYPTOPRO"/>
	<param name="doAfterLoad" value="true"/>
</object>

</#macro>
