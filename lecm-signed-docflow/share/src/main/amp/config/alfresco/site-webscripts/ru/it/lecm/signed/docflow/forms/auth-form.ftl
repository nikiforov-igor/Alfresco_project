<#assign htmlId = args.htmlid>
<#assign formId = htmlId + "-form">
<#assign controlId = htmlId + "-cntrl">
<#assign formContainerId = formId + "-container">

<div id="${formContainerId}">
<#if formUI == "true">
	<@formLib.renderFormsRuntime formId = formId />
</#if>

<@formLib.renderFormContainer formId = formId>

	<div id="selectContainer"></div><br/>

</@>
</div>

<script>
(function() {

	var Event = YAHOO.util.Event,
		Button = YAHOO.widget.Button,
		data = [],
		certs = CryptoApplet.getCerts(),
		certsList, i;

	for (i = 0; i < certs.length; i++) {
		data[i] = {};
		data[i].text = certs[i].getHumanReadable();
		data[i].value = i;
	}

	certsList = new Button({
		id: "certs",
		name: "certs",
		label: "Выбор сертификата",
		type: "menu",
		menu: data,
		container: "selectContainer"
	});


	certsList.on("selectedMenuItemChange", function(event) {
		var oMenuItem = event.newValue,
			CurrentContainerIndex = event.newValue.value;

		CryptoApplet.setCurrentSigningCert(certs[CurrentContainerIndex]);
		this.set("label", oMenuItem.cfg.getProperty("text"));
	});
})();

</script>
