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

    this.loadCertificatesCallBack = function(results) {

        Alfresco.util.Ajax.jsonRequest({
			method: 'GET',
			url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/signed-docflow/getSTSAAddress',
			successCallback: {
				scope: this,
				fn: function (response) {
					CryptoApplet.setSTSAAddress(response.json[0].sTSAAddress);
                    var Event = YAHOO.util.Event,
                        Button = YAHOO.widget.Button,
                        data = [],
                        certs = results/*CryptoApplet.getCerts()*/,
                        certsList, i;

                    for(i = 0; i < certs.length; i++) {
                        data[i] = {};
                        data[i].text = "CN=" + certs[i].shortsubject + "; Выдан: " + certs[i].normalValidFrom;
                        data[i].value = i;
                    }

                    certsList = new Button({
                        id: "certs",
                        name: "certs",
                        label: "${msg('lecm.signdoc.lbl.select.cert')}",
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
				}
			},
		});
    };

    if (!CryptoApplet.useNPAPI) {
        GetES6CertsJson(this);
    } else {
        window.addEventListener("message", function(event) {
                if (event.data === "cadesplugin_loaded") {
                    CryptoApplet.useNPAPI = true;
                }
            },
            false);
        window.postMessage("cadesplugin_echo_request", "*");
        FillCertList_NPAPIJson(this);
    }

})();

</script>
