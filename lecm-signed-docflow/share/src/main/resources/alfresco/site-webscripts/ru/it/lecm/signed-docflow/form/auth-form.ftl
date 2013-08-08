<#assign htmlId = args.htmlid>
<#assign formId = htmlId + "-form">
<#assign controlId = htmlId + "-cntrl">
<#assign formContainerId = formId + "-container">

<div id="${formContainerId}">
<#if formUI == "true">
    <@formLib.renderFormsRuntime formId = formId />
</#if>

<@formLib.renderFormContainer formId = formId>

   <applet code="ru.businesslogic.crypto.userinterface.CryptoApplet.class"  
	archive="${url.server}${url.context}/scripts/signed-docflow/ITStampApplet.jar" 
	width="1" height="1" name="signApplet">
        <param name="signOnLoad" value="false"/>
        <param name="debug" value="true"/>
        <param name="providerType" value="CSP_CRYPTOPRO"/>
        <param name="doAfterLoad" value="true"/>
    </applet>
    
        <div id="selectContainer"></div><br/>
      
</@>
</div>

<script>
    var Button = YAHOO.widget.Button;
     var data = [];
    var CurrentContainer = '';

    var certsList = new Button({
        id: "certs",
        name: "certs",
        label: "Выбор сертификата",
        type: "menu",
        menu: data,
        container: "selectContainer"});

    var onSelectedMenuItemChange = function(event) {
        var oMenuItem = event.newValue;
        CurrentContainer = event.newValue.value;
		cryptoAppletModule.setCurrentContainer(event.newValue.value);
        cryptoAppletModule.reConfigCert(CurrentContainer);
        this.set("label", (oMenuItem.cfg.getProperty("text")));
    };
    certsList.on("selectedMenuItemChange", onSelectedMenuItemChange);

     function afterLoad() {
        cryptoAppletModule.startApplet();
        var infoStr = cryptoAppletModule.getCertsInfo();
        for (var i = 0; i < infoStr.length; i++) {
            data[i] = {};
            var CN = infoStr[i].SubjectName;
            var container = infoStr[i].container;
            var organization = infoStr[i].Organization;
            var orgUnit = infoStr[i].OrgUnit;
            if (CN)
                data[i].text = '<strong>' + CN + '</strong>';
            if (organization)
                data[i].text += '<br/>' + organization;
            if (orgUnit)
                data[i].text += '<br/>' + orgUnit;
            data[i].value = container;
        }
        

       
    }
    
</script>

