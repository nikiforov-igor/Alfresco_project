<#--
Макрос для добавления на страницу криптоапплета для работы с ЭП
-->
<#macro initApplet>
<script type="text/javascript">
	CryptoApplet = new LogicECM.CryptoApplet('crypto-applet-module');
	CryptoApplet.onReady();
</script>

</#macro>
