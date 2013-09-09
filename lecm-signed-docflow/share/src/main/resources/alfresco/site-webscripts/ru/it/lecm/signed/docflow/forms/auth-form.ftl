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
	var Event = YAHOO.util.Event; 
	var Button = YAHOO.widget.Button;
		var data = [];
		var CurrentContainer = '';

		var certs = CryptoApplet.getCerts();
        for (var i = 0; i < certs.length; i++) {
            data[i] = {};
            data[i].text = certs[i].getHumanReadable();
			data[i].value = i;
        }

		var certsList = new Button({
        id: "certs",
        name: "certs",
        label: "Выбор сертификата",
        type: "menu",
        menu: data,
        container: "selectContainer"});
	
		

    

    var onSelectedMenuItemChange = function(event) {
        var oMenuItem = event.newValue;
        CurrentContainerIndex = event.newValue.value;
		CryptoApplet.setCurrentSigningCert(certs[CurrentContainerIndex]);
        this.set("label", (oMenuItem.cfg.getProperty("text")));
    };
    certsList.on("selectedMenuItemChange", onSelectedMenuItemChange);

    
</script>

