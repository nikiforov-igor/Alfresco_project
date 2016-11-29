var isPluginEnabled = false;
function getXmlHttp(){
    var xmlhttp;
    try {
        xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
    } catch (e) {
        try {
            xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
        } catch (E) {
            xmlhttp = false;
        }
    }
    if (!xmlhttp && typeof XMLHttpRequest!='undefined') {
        xmlhttp = new XMLHttpRequest();
    }
    return xmlhttp;
}

//var async_code_included = 0;
//var async_Promise;
//var async_resolve;
//function include_async_code()
//{
//    if(async_code_included)
//    {
//        return async_Promise;
//    }
//    var fileref = document.createElement('script');
//    fileref.setAttribute("type", "text/javascript");
//    fileref.setAttribute("src", "async_code.js");
//    document.getElementsByTagName("head")[0].appendChild(fileref);
//    async_Promise = new Promise(function(resolve, reject){
//        async_resolve = resolve;
//    });
//    async_code_included = 1;
//    return async_Promise;
//}

function Common_RetrieveCertificate()
{
    var canAsync = !!cadesplugin.CreateObjectAsync;
    if(canAsync)
    {
        include_async_code().then(function(){
            return RetrieveCertificate_Async();
        });
    }else
    {
        return RetrieveCertificate_NPAPI();
    }
}

function Common_CreateSimpleSign(id)
{
    var canAsync = !!cadesplugin.CreateObjectAsync;
    if(canAsync)
    {
        include_async_code().then(function(){
            return CreateSimpleSign_Async(id);
        });
    }else
    {
        return CreateSimpleSign_NPAPI(id);
    }
}

function Common_SignCadesBES(id)
{
    var canAsync = !!cadesplugin.CreateObjectAsync;
    if(canAsync)
    {
        include_async_code().then(function(){
            return SignCadesBES_Async(id);
        });
    }else
    {
        return SignCadesBES_NPAPI(id);
    }
}

function Common_SignCadesXLong(id)
{
    var canAsync = !!cadesplugin.CreateObjectAsync;
    if(canAsync)
    {
        include_async_code().then(function(){
            return SignCadesXLong_Async(id);
        });
    }else
    {
        return SignCadesXLong_NPAPI(id);
    }
}

function Common_SignCadesXML(id)
{
    var canAsync = !!cadesplugin.CreateObjectAsync;
    if(canAsync)
    {
        include_async_code().then(function(){
            return SignCadesXML_Async(id);
        });
    }else
    {
        return SignCadesXML_NPAPI(id);
    }
}

function Common_CheckForPlugIn() {
    var canAsync = !!cadesplugin.CreateObjectAsync;
    if(canAsync)
    {
        include_async_code().then(function(){
            return CheckForPlugIn_Async();
        });
    }else
    {
        return CheckForPlugIn_NPAPI();
    }
}

function Common_Encrypt() {
    var canAsync = !!cadesplugin.CreateObjectAsync;
    if(canAsync)
    {
        include_async_code().then(function(){
            return Encrypt_Async();
        });
    }else
    {
        return Encrypt_NPAPI();
    }
}

function Common_Decrypt(id) {
    var canAsync = !!cadesplugin.CreateObjectAsync;
    if(canAsync)
    {
        include_async_code().then(function(){
            return Decrypt_Async(id);
        });
    }else
    {
        return Decrypt_NPAPI(id);
    }
}

function GetCertificate_NPAPI(certListBoxId) {
    var e = document.getElementById(certListBoxId);
    var selectedCertID = e.selectedIndex;
    if (selectedCertID == -1) {
        alert("Select certificate");
        return;
    }

    var thumbprint = e.options[selectedCertID].value.split(" ").reverse().join("").replace(/\s/g, "").toUpperCase();
    try {
        var oStore = cadesplugin.CreateObject("CAdESCOM.Store");
        oStore.Open();
    } catch (err) {
        alert('Failed to create CAdESCOM.Store: ' + GetErrorMessage(err));
        return;
    }

    var CAPICOM_CERTIFICATE_FIND_SHA1_HASH = 0;
    var oCerts = oStore.Certificates.Find(CAPICOM_CERTIFICATE_FIND_SHA1_HASH, thumbprint);

    if (oCerts.Count == 0) {
        alert("Certificate not found");
        return;
    }
    var oCert = oCerts.Item(1);
    return oCert;
}

function GetCertificateNPAPI(thumbprint) {

    thumbprint = thumbprint.replace(/\s/g, "").toUpperCase();
	try {
		var oStore = cadesplugin.CreateObject("CAdESCOM.Store");
		oStore.Open();
	} catch (err) {
		return null;
	}

	var CAPICOM_CERTIFICATE_FIND_SHA1_HASH = 0;
	var all_certs = oStore.Certificates;
	var oCerts = all_certs.Find(CAPICOM_CERTIFICATE_FIND_SHA1_HASH, thumbprint);

	if ((oCerts.Count) === 0) {
		return null;
	}
	var certificate = oCerts.Item(1);
    var privateKey = certificate.PrivateKey;
    function getAttributeValue(str, attribute) {
        var index = str.indexOf(attribute + "=");
        if (index == -1)
            return str;
        str = str.substring(index + attribute.length + 1);
        index = str.indexOf(",");
        if (index == -1) {
            return str;
        }
        return str.substring(0, index);
    }
    return {
        version: certificate.Version,                       //3
        validToDate: (new Date(certificate.ValidToDate)).getTime(),     //Date 2015-08-07T07:48:00.000Z
        validFromDate: (new Date(certificate.ValidFromDate)).getTime(), //Date 2016-08-07T07:47:23.000Z
        thumbprint: certificate.Thumbprint,                 //"040F78E8E5A619546EAF9AB891174BBAA00093B5"
        subjectName: certificate.SubjectName,               //"STREET=У 1, CN=Теле2_Тест_3, SN=Тестов, G=Тест Третий, C=RU, S=77 г. Москва, L=г. М, O=Теле2_Тест_3, T=Директор, OGRN=0067333755082, SNILS=26481117067, INN=009999324755"
        serialNumber: certificate.SerialNumber,             //"01D0D0E54C4B5D00000000000379085D"
        issuerName: certificate.IssuerName,                 //"CN=ЗАО Калуга Астрал (УЦ 889), O=ЗАО Калуга Астрал, E=ca@astralnalog.ru, S=40 Калужская область, L=Калуга, C=RU, INN=004029017981, OGRN=1024001434049, STREET=Улица Циолковского дом 4"
        isValid: certificate.IsValid().Result,      //1
        hasPrivateKey: certificate.HasPrivateKey(),          //true
        containerName: privateKey.ContainerName,
        providerName: privateKey.ProviderName,
        shortissuer: getAttributeValue(certificate.IssuerName, "CN"),
        shortsubject: getAttributeValue(certificate.SubjectName, "CN")
    };
}

function FillCertInfo_NPAPI(certificate, certBoxID)
{
    /*var BoxID;
    var field_prefix;
    if(typeof(certBoxID) == 'undefined')
    {
        BoxID = 'cert_info';
        field_prefix = '';
    }else {
        BoxID = certBoxID;
        field_prefix = certBoxID;
    }

    var certObj = new CertificateObj(certificate);
    document.getElementById(BoxID).style.display = '';
    document.getElementById(field_prefix + "subject").innerHTML = "Владелец: <b>" + certObj.GetCertName() + "<b>";
    document.getElementById(field_prefix + "issuer").innerHTML = "Издатель: <b>" + certObj.GetIssuer() + "<b>";
    document.getElementById(field_prefix + "from").innerHTML = "Выдан: <b>" + certObj.GetCertFromDate() + "<b>";
    document.getElementById(field_prefix + "till").innerHTML = "Действителен до: <b>" + certObj.GetCertTillDate() + "<b>";
    document.getElementById(field_prefix + "algorithm").innerHTML = "Алгоритм ключа: <b>" + certObj.GetPubKeyAlgorithm() + "<b>";*/
}

function AboutNPAPI(context) {
    try {
        var oAbout = cadesplugin.CreateObject("CAdESCOM.About");
        context.aboutCallBack(!!oAbout);
        return;
    } catch(err) {
		console.log(err)
    }
    context.aboutCallBack(false);
}

function MakeCadesBesSign_NPAPI(dataToSign, certObject) {
    var errormes = "";

    try {
        var oSigner = cadesplugin.CreateObject("CAdESCOM.CPSigner");
    } catch (err) {
        errormes = "Failed to create CAdESCOM.CPSigner: " + err.number;
        alert(errormes);
        throw errormes;
    }

    if (oSigner) {
        oSigner.Certificate = certObject;
    }
    else {
        errormes = "Failed to create CAdESCOM.CPSigner";
        alert(errormes);
        throw errormes;
    }

    try {
        var oSignedData = cadesplugin.CreateObject("CAdESCOM.CadesSignedData");
    } catch (err) {
        alert('Failed to create CAdESCOM.CadesSignedData: ' + err.number);
        return;
    }

    var CADES_BES = 1;
    var Signature;

    if (dataToSign) {
        // Данные на подпись ввели
        oSignedData.Content = dataToSign;
        oSigner.Options = 1; //CAPICOM_CERTIFICATE_INCLUDE_WHOLE_CHAIN
        try {
            Signature = oSignedData.SignCades(oSigner, CADES_BES);
        }
        catch (err) {
            errormes = "Не удалось создать подпись из-за ошибки: " + GetErrorMessage(err);
            alert(errormes);
            throw errormes;
        }
    }
    return Signature;
}

function MakeCadesXLongSign_NPAPI(dataToSign, tspService, certObject) {
    var errormes = "";

    try {
        var oSigner = cadesplugin.CreateObject("CAdESCOM.CPSigner");
    } catch (err) {
        errormes = "Failed to create CAdESCOM.CPSigner: " + err.number;
        alert(errormes);
        throw errormes;
    }

    if (oSigner) {
        oSigner.Certificate = certObject;
    }
    else {
        errormes = "Failed to create CAdESCOM.CPSigner";
        alert(errormes);
        throw errormes;
    }

    try {
        var oSignedData = cadesplugin.CreateObject("CAdESCOM.CadesSignedData");
    } catch (err) {
        alert('Failed to create CAdESCOM.CadesSignedData: ' + GetErrorMessage(err));
        return;
    }

    var CADESCOM_CADES_X_LONG_TYPE_1 = 0x5d;
    var Signature;

    if (dataToSign) {
        // Данные на подпись ввели
        oSignedData.Content = dataToSign;
        oSigner.Options = 1; //CAPICOM_CERTIFICATE_INCLUDE_WHOLE_CHAIN
        oSigner.TSAAddress = tspService;
        try {
            Signature = oSignedData.SignCades(oSigner, CADESCOM_CADES_X_LONG_TYPE_1);
        }
        catch (err) {
            errormes = "Не удалось создать подпись из-за ошибки: " + GetErrorMessage(err);
            alert(errormes);
            throw errormes;
        }
    }
    return Signature;
}

function MakeXMLSign_NPAPI(dataToSign, certObject) {
    try {
        var oSigner = cadesplugin.CreateObject("CAdESCOM.CPSigner");
    } catch (err) {
        errormes = "Failed to create CAdESCOM.CPSigner: " + err.number;
        alert(errormes);
        throw errormes;
    }

    if (oSigner) {
        oSigner.Certificate = certObject;
    }
    else {
        errormes = "Failed to create CAdESCOM.CPSigner";
        alert(errormes);
        throw errormes;
    }

    var XmlDsigGost3410Url = "urn:ietf:params:xml:ns:cpxmlsec:algorithms:gostr34102001-gostr3411";
    var XmlDsigGost3411Url = "urn:ietf:params:xml:ns:cpxmlsec:algorithms:gostr3411";
    var CADESCOM_XML_SIGNATURE_TYPE_ENVELOPED = 0;

    try {
        var oSignedXML = cadesplugin.CreateObject("CAdESCOM.SignedXML");
    } catch (err) {
        alert('Failed to create CAdESCOM.SignedXML: ' + GetErrorMessage(err));
        return;
    }

    oSignedXML.Content = dataToSign;
    oSignedXML.SignatureType = CADESCOM_XML_SIGNATURE_TYPE_ENVELOPED;
    oSignedXML.SignatureMethod = XmlDsigGost3410Url;
    oSignedXML.DigestMethod = XmlDsigGost3411Url;

    var sSignedMessage = "";
    try {
        sSignedMessage = oSignedXML.Sign(oSigner);
    }
    catch (err) {
        errormes = "Не удалось создать подпись из-за ошибки: " + GetErrorMessage(err);
        alert(errormes);
        throw errormes;
    }

    return sSignedMessage;
}

function GetSignatureTitleElement()
{
    var elementSignatureTitle = null;
    var x = document.getElementsByName("SignatureTitle");

    if(x.length == 0)
    {
        elementSignatureTitle = document.getElementById("SignatureTxtBox").parentNode.previousSibling;

        if(elementSignatureTitle.nodeName == "P")
        {
            return elementSignatureTitle;
        }
    }
    else
    {
        elementSignatureTitle = x[0];
    }

    return elementSignatureTitle;
}

function SignCadesBES_NPAPI(certListBoxId) {
    var certificate = GetCertificate_NPAPI(certListBoxId);
    var dataToSign = document.getElementById("DataToSignTxtBox").value;
    var x = GetSignatureTitleElement();
    try
    {
        FillCertInfo_NPAPI(certificate);
        var signature = MakeCadesBesSign_NPAPI(dataToSign, certificate);
        document.getElementById("SignatureTxtBox").innerHTML = signature;
        if(x!=null)
        {
            x.innerHTML = "Подпись сформирована успешно:";
        }
    }
    catch(err)
    {
        if(x!=null)
        {
            x.innerHTML = "Возникла ошибка:";
        }
        document.getElementById("SignatureTxtBox").innerHTML = err;
    }
}

function SignCadesXLong_NPAPI(certListBoxId) {
    var certificate = GetCertificate_NPAPI(certListBoxId);
    var dataToSign = document.getElementById("DataToSignTxtBox").value;
    var tspService = document.getElementById("TSPServiceTxtBox").value ;
    var x = GetSignatureTitleElement();
    try
    {
        FillCertInfo_NPAPI(certificate);
        var signature = MakeCadesXLongSign_NPAPI(dataToSign, tspService, certificate);
        document.getElementById("SignatureTxtBox").value = signature;
        if(x!=null)
        {
            x.innerHTML = "Подпись сформирована успешно:";
        }
    }
    catch(err)
    {
        if(x!=null)
        {
            x.innerHTML = "Возникла ошибка:";
        }
        document.getElementById("SignatureTxtBox").innerHTML = err;
    }
}

function SignCadesXML_NPAPI(certListBoxId) {
    var certificate = GetCertificate_NPAPI(certListBoxId);
    var dataToSign = document.getElementById("DataToSignTxtBox").value;
    var x = GetSignatureTitleElement();
    try
    {
        FillCertInfo_NPAPI(certificate);
        var signature = MakeXMLSign_NPAPI(dataToSign, certificate);
        document.getElementById("SignatureTxtBox").value = signature;

        if(x!=null)
        {
            x.innerHTML = "Подпись сформирована успешно:";
        }
    }
    catch(err)
    {
        if(x!=null)
        {
            x.innerHTML = "Возникла ошибка:";
        }
        document.getElementById("SignatureTxtBox").innerHTML = err;
    }
}

function MakeVersionString(oVer)
{
    var strVer;
    if(typeof(oVer)=="string")
        return oVer;
    else
        return oVer.MajorVersion + "." + oVer.MinorVersion + "." + oVer.BuildVersion;
}

function CheckForPlugIn_NPAPI() {
    function VersionCompare_NPAPI(StringVersion, ObjectVersion)
    {
        if(typeof(ObjectVersion) == "string")
            return -1;
        var arr = StringVersion.split('.');

        if(ObjectVersion.MajorVersion == parseInt(arr[0]))
        {
            if(ObjectVersion.MinorVersion == parseInt(arr[1]))
            {
                if(ObjectVersion.BuildVersion == parseInt(arr[2]))
                {
                    return 0;
                }
                else if(ObjectVersion.BuildVersion < parseInt(arr[2]))
                {
                    return -1;
                }
            }else if(ObjectVersion.MinorVersion < parseInt(arr[1]))
            {
                return -1;
            }
        }else if(ObjectVersion.MajorVersion < parseInt(arr[0]))
        {
            return -1;
        }

        return 1;
    }

    function GetCSPVersion_NPAPI() {
        try {
           var oAbout = cadesplugin.CreateObject("CAdESCOM.About");
        } catch (err) {
            alert('Failed to create CAdESCOM.About: ' + GetErrorMessage(err));
            return;
        }
        var ver = oAbout.CSPVersion("", 75);
        return ver.MajorVersion + "." + ver.MinorVersion + "." + ver.BuildVersion;
    }

    function ShowCSPVersion_NPAPI(CurrentPluginVersion)
    {
        if(typeof(CurrentPluginVersion) != "string")
        {
            document.getElementById('CSPVersionTxt').innerHTML = "Версия CSP: " + GetCSPVersion_NPAPI();
        }
    }
    function GetLatestVersion_NPAPI(CurrentPluginVersion) {
        var xmlhttp = getXmlHttp();
        xmlhttp.open("GET", "/sites/default/files/products/cades/latest_2_0.txt", true);
        xmlhttp.onreadystatechange = function() {
            var PluginBaseVersion;
            if (xmlhttp.readyState == 4) {
                if(xmlhttp.status == 200) {
                    PluginBaseVersion = xmlhttp.responseText;
                    if (isPluginWorked) { // плагин работает, объекты создаются
                        if (VersionCompare_NPAPI(PluginBaseVersion, CurrentPluginVersion)<0) {
                            document.getElementById('PluginEnabledImg').setAttribute("src", "Img/yellow_dot.png");
                            document.getElementById('PlugInEnabledTxt').innerHTML = "Плагин загружен, но есть более свежая версия.";
                        }
                    }
                    else { // плагин не работает, объекты не создаются
                        if (isPluginLoaded) { // плагин загружен
                            if (!isPluginEnabled) { // плагин загружен, но отключен
                                document.getElementById('PluginEnabledImg').setAttribute("src", "Img/red_dot.png");
                                document.getElementById('PlugInEnabledTxt').innerHTML = "Плагин загружен, но отключен в настройках браузера.";
                            }
                            else { // плагин загружен и включен, но объекты не создаются
                                document.getElementById('PluginEnabledImg').setAttribute("src", "Img/red_dot.png");
                                document.getElementById('PlugInEnabledTxt').innerHTML = "Плагин загружен, но не удается создать объекты. Проверьте настройки браузера.";
                            }
                        }
                        else { // плагин не загружен
                            document.getElementById('PluginEnabledImg').setAttribute("src", "Img/red_dot.png");
                            document.getElementById('PlugInEnabledTxt').innerHTML = "Плагин не загружен.";
                        }
                    }
                }
            }
        }
        xmlhttp.send(null);
    }

    var isPluginLoaded = false;
    var isPluginWorked = false;
    var isActualVersion = false;
    try {
        var oAbout = cadesplugin.CreateObject("CAdESCOM.About");
        isPluginLoaded = true;
        isPluginEnabled = true;
        isPluginWorked = true;
        // Это значение будет проверяться сервером при загрузке демо-страницы
        var CurrentPluginVersion = oAbout.PluginVersion;
        if( typeof(CurrentPluginVersion) == "undefined")
            CurrentPluginVersion = oAbout.Version;

        document.getElementById('PluginEnabledImg').setAttribute("src", "Img/green_dot.png");
        document.getElementById('PlugInEnabledTxt').innerHTML = "Плагин загружен.";
        document.getElementById('PlugInVersionTxt').innerHTML = "Версия плагина: " + MakeVersionString(CurrentPluginVersion);
        ShowCSPVersion_NPAPI(CurrentPluginVersion);
    }
    catch (err) {
        // Объект создать не удалось, проверим, установлен ли
        // вообще плагин. Такая возможность есть не во всех браузерах
        var mimetype = navigator.mimeTypes["application/x-cades"];
        if (mimetype) {
            isPluginLoaded = true;
            var plugin = mimetype.enabledPlugin;
            if (plugin) {
                isPluginEnabled = true;
            }
        }
    }
    GetLatestVersion_NPAPI(CurrentPluginVersion);
    if(location.pathname.indexOf("symalgo_sample.html")>=0){
        FillCertList_NPAPI('CertListBox1');
        FillCertList_NPAPI('CertListBox2');
    } else{
        FillCertList_NPAPI('CertListBox');
    }
}

function CertificateObj(certObj)
{
    this.cert = certObj;
    this.certFromDate = new Date(this.cert.ValidFromDate);
    this.certTillDate = new Date(this.cert.ValidToDate);
}

CertificateObj.prototype.check = function(digit)
{
    return (digit<10) ? "0"+digit : digit;
}

CertificateObj.prototype.extract = function(from, what)
{
    certName = "";

    var begin = from.indexOf(what);

    if(begin>=0)
    {
        var end = from.indexOf(', ', begin);
        certName = (end<0) ? from.substr(begin) : from.substr(begin, end - begin);
    }

    return certName;
}

CertificateObj.prototype.DateTimePutTogether = function(certDate)
{
    return this.check(certDate.getUTCDate())+"."+this.check(certDate.getMonth()+1)+"."+certDate.getFullYear() + " " +
                 this.check(certDate.getUTCHours()) + ":" + this.check(certDate.getUTCMinutes()) + ":" + this.check(certDate.getUTCSeconds());
}

CertificateObj.prototype.GetCertString = function()
{
    return this.extract(this.cert.SubjectName,'CN=') + "; Выдан: " + this.GetCertFromDate();
}

CertificateObj.prototype.GetCertFromDate = function()
{
    return this.DateTimePutTogether(this.certFromDate);
}

CertificateObj.prototype.GetCertTillDate = function()
{
    return this.DateTimePutTogether(this.certTillDate);
}

CertificateObj.prototype.GetPubKeyAlgorithm = function()
{
    return this.cert.PublicKey().Algorithm.FriendlyName;
}

CertificateObj.prototype.GetCertName = function()
{
    return this.extract(this.cert.SubjectName, 'CN=');
}

CertificateObj.prototype.GetIssuer = function()
{
    return this.extract(this.cert.IssuerName, 'CN=');
}

function GetFirstCert_NPAPI() {
    try {
        var oStore = cadesplugin.CreateObject("CAdESCOM.Store");
        oStore.Open();
    }
    catch (e) {
        alert("Ошибка при открытии хранилища: " + GetErrorMessage(e));
        return;
    }

    var dateObj = new Date();
    var certCnt;

    try {
        certCnt = oStore.Certificates.Count;
        if(certCnt==0)
            throw "Cannot find object or property. (0x80092004)";
    }
    catch (ex) {
        var message = GetErrorMessage(ex);
        if("Cannot find object or property. (0x80092004)" == message ||
           "oStore.Certificates is undefined" == message ||
           "Объект или свойство не найдено. (0x80092004)" == message)
        {
            oStore.Close();
            var errormes = document.getElementById("boxdiv").style.display = '';
            return;
        }
    }

    if(certCnt) {
        try {
            for (var i = 1; i <= certCnt; i++) {
                var cert = oStore.Certificates.Item(i);
                if(dateObj<cert.ValidToDate && cert.HasPrivateKey() && cert.IsValid().Result){
                    return cert;
                }
            }
        }
        catch (ex) {
            alert("Ошибка при перечислении сертификатов: " + GetErrorMessage(ex));
            return;
        }
    }
}

function CreateSimpleSign_NPAPI()
{
    oCert = GetFirstCert_NPAPI();
    var x = GetSignatureTitleElement();
    try
    {
        if (typeof oCert != "undefined") {
            FillCertInfo_NPAPI(oCert);
            var sSignedData = MakeCadesBesSign_NPAPI(txtDataToSign, oCert);
            document.getElementById("SignatureTxtBox").innerHTML = sSignedData;
            if(x!=null)
            {
                x.innerHTML = "Подпись сформирована успешно:";
            }
        }
    }
    catch(err)
    {
        if(x!=null)
        {
            x.innerHTML = "Возникла ошибка:";
        }
        document.getElementById("SignatureTxtBox").innerHTML = err;
    }
}

function FillCertList_NPAPI(lstId, context) {
    try {
        var oStore = cadesplugin.CreateObject("CAdESCOM.Store");
        oStore.Open();
    }
    catch (ex) {
        alert("Ошибка при открытии хранилища: " + GetErrorMessage(ex));
        return;
    }

    try {
        var lst = document.getElementById(lstId);
        if(!lst)
            return;
    }
    catch (ex) {
        return;
    }

    var certCnt;

    try {
        certCnt = oStore.Certificates.Count;
        if(certCnt==0)
            throw "Cannot find object or property. (0x80092004)";
    }
    catch (ex) {
        var message = GetErrorMessage(ex);
        if("Cannot find object or property. (0x80092004)" == message ||
           "oStore.Certificates is undefined" == message ||
           "Объект или свойство не найдено. (0x80092004)" == message)
        {
            oStore.Close();
            return;
        }
    }

    for (var i = 1; i <= certCnt; i++) {
        var cert;
        try {
            cert = oStore.Certificates.Item(i);
        }
        catch (ex) {
            alert("Ошибка при перечислении сертификатов: " + GetErrorMessage(ex));
            return;
        }

        //var oOpt = document.createElement("OPTION");
        var oOpt_text;
        var oOpt_value;
        var dateObj = new Date();
        try {
            if(dateObj<cert.ValidToDate && cert.HasPrivateKey() && cert.IsValid().Result) {
                var certObj = new CertificateObj(cert);
                oOpt_text = certObj.GetCertString();
            }
            else {
                continue;
            }
        }
        catch (ex) {
            alert("Ошибка при получении свойства SubjectName: " + GetErrorMessage(ex));
        }
        try {
            oOpt_value = cert.Thumbprint;
        }
        catch (ex) {
            alert("Ошибка при получении свойства Thumbprint: " + GetErrorMessage(ex));
        }
        var oOpt = new Option(oOpt_text, oOpt_value);
        lst.add(oOpt);
    }

    oStore.Close();
}

function FillCertList_NPAPIJson(context) {
    try {
        var oStore = cadesplugin.CreateObject("CAdESCOM.Store");
        oStore.Open();
    }
    catch (ex) {
        alert("Ошибка при открытии хранилища: " + GetErrorMessage(ex));
        return;
    }

    var certCnt;

    try {
        certCnt = oStore.Certificates.Count;
        if(certCnt===0)
            throw "Cannot find object or property. (0x80092004)";
    }
    catch (ex) {
        var message = GetErrorMessage(ex);
        if("Cannot find object or property. (0x80092004)" === message ||
           "oStore.Certificates is undefined" === message ||
           "Объект или свойство не найдено. (0x80092004)" === message)
        {
            oStore.Close();
            return;
        }
    }
    function getNormalDate(d) {
        return  ('0' + d.getDate()).slice(-2) + '.' + ('0' + (d.getMonth() + 1)).slice(-2) + '.' + d.getFullYear();
    }

    function getAttributeValue(str, attribute) {
        var index = str.indexOf(attribute + "=");
        if (index == -1)
            return str;
        str = str.substring(index + attribute.length + 1);
        index = str.indexOf(",");
        if (index == -1) {
            return str;
        }
        return str.substring(0, index);
    }

    var result = [];
    for (var i = 1; i <= certCnt; i++) {
        var cert;
        try {
            cert = oStore.Certificates.Item(i);
        }
        catch (ex) {
            alert("Ошибка при перечислении сертификатов: " + GetErrorMessage(ex));
            return;
        }

        var dateObj = new Date();
        try {
                var Validator = cert.IsValid();
                var IsValid = Validator.Result;
                var ValidFromDate = new Date((cert.ValidFromDate));
                var ValidToDate = new Date((cert.ValidToDate));

                if(dateObj< ValidToDate && (cert.HasPrivateKey()) && IsValid) {
                    var version = cert.Version;                           //3
                    var thumbprint = cert.Thumbprint;                     //"040F78E8E5A619546EAF9AB891174BBAA00093B5"
                    var subject = "";
                    try {
                        var subject = cert.SubjectName;                       //"STREET=У 1, CN=Теле2_Тест_3, SN=Тестов, G=Тест Третий, C=RU, S=77 г. Москва, L=г. М, O=Теле2_Тест_3, T=Директор, OGRN=0067333755082, SNILS=26481117067, INN=009999324755"
                    } catch(ex) {
                    };
                    var serialNumber = cert.SerialNumber;                 //"01D0D0E54C4B5D00000000000379085D"
                    var issuer = cert.IssuerName;                         //"CN=ЗАО Калуга Астрал (УЦ 889), O=ЗАО Калуга Астрал, E=ca@astralnalog.ru, S=40 Калужская область, L=Калуга, C=RU, INN=004029017981, OGRN=1024001434049, STREET=Улица Циолковского дом 4"
                    var hasPrivateKey = cert.HasPrivateKey();             //true
                    var privateKey = cert.PrivateKey;
                    var containerName = privateKey.ContainerName;    //27B340E2-FD1D-4B0C-9561-F05FBF51FF87
                    var providerName = privateKey.ProviderName;      //Crypto-Pro GOST R 34.10-2001 Cryptographic Service Provider
                    var shortissuer = getAttributeValue(issuer, "CN");
                    var shortsubject = getAttributeValue(subject, "CN");
                    result.push({
                        shortissuer: shortissuer,
                        shortsubject: shortsubject,
                        validTo: /*getNormalDate(*/ValidToDate/*)*/,
						validFrom: /*getNormalDate(*/ValidFromDate/*)*/,
                        normalValidTo: getNormalDate(ValidToDate),
						normalValidFrom: getNormalDate(ValidFromDate),
                        version: version,
                        thumbprint: thumbprint,
                        subject: subject,
                        serialNumber : serialNumber,
                        issuer: issuer,
                        hasPrivateKey: hasPrivateKey,
                        isValid: IsValid,
                        containerName: containerName,
                        providerName: providerName
                    });
                }
                else {
                    continue;
                }
            }
            catch (ex) {
                alert("Ошибка при получении свойства SubjectName: " + GetErrorMessage(ex));
            }
    }

    oStore.Close();

    context.loadCertificatesCallBack(result);
}

function decimalToHexString(number) {
    if (number < 0) {
        number = 0xFFFFFFFF + number + 1;
    }

    return number.toString(16).toUpperCase();
}

function GetErrorMessage(e) {
    var err = e.message;
    if (!err) {
        err = e;
    } else if (e.number) {
        err += " (0x" + decimalToHexString(e.number) + ")";
    }
    return err;
}

function CreateCertRequest_NPAPI()
{
    try {
        var PrivateKey = cadesplugin.CreateObject("X509Enrollment.CX509PrivateKey");
    }
    catch (e) {
        alert('Failed to create X509Enrollment.CX509PrivateKey: ' + GetErrorMessage(e));
        return;
    }

    PrivateKey.ProviderName = "Crypto-Pro GOST R 34.10-2001 Cryptographic Service Provider";
    PrivateKey.ProviderType = 75;
    PrivateKey.KeySpec = 1; //XCN_AT_KEYEXCHANGE

    try {
        var CertificateRequestPkcs10 = cadesplugin.CreateObject("X509Enrollment.CX509CertificateRequestPkcs10");
    }
    catch (e) {
        alert('Failed to create X509Enrollment.CX509CertificateRequestPkcs10: ' + GetErrorMessage(e));
        return;
    }

    CertificateRequestPkcs10.InitializeFromPrivateKey(0x1, PrivateKey, "");

    try {
        var DistinguishedName = cadesplugin.CreateObject("X509Enrollment.CX500DistinguishedName");
    }
    catch (e) {
        alert('Failed to create X509Enrollment.CX500DistinguishedName: ' + GetErrorMessage(e));
        return;
    }

    var CommonName = "Test Certificate";
    DistinguishedName.Encode("CN=\""+CommonName.replace(/"/g, "\"\"")+"\";");

    CertificateRequestPkcs10.Subject = DistinguishedName;

    var KeyUsageExtension = cadesplugin.CreateObject("X509Enrollment.CX509ExtensionKeyUsage");
    var CERT_DATA_ENCIPHERMENT_KEY_USAGE = 0x10;
    var CERT_KEY_ENCIPHERMENT_KEY_USAGE = 0x20;
    var CERT_DIGITAL_SIGNATURE_KEY_USAGE = 0x80;
    var CERT_NON_REPUDIATION_KEY_USAGE = 0x40;

    KeyUsageExtension.InitializeEncode(
                CERT_KEY_ENCIPHERMENT_KEY_USAGE |
                CERT_DATA_ENCIPHERMENT_KEY_USAGE |
                CERT_DIGITAL_SIGNATURE_KEY_USAGE |
                CERT_NON_REPUDIATION_KEY_USAGE);

    CertificateRequestPkcs10.X509Extensions.Add(KeyUsageExtension);

    try {
        var Enroll = cadesplugin.CreateObject("X509Enrollment.CX509Enrollment");
    }
    catch (e) {
        alert('Failed to create X509Enrollment.CX509Enrollment: ' + GetErrorMessage(e));
        return;
    }

    Enroll.InitializeFromRequest(CertificateRequestPkcs10);

    return Enroll.CreateRequest(0x1);
}

function RetrieveCertificate_NPAPI()
{
    var cert_req = CreateCertRequest_NPAPI();
    var params = 'CertRequest=' + encodeURIComponent(cert_req) +
                 '&Mode=' + encodeURIComponent('newreq') +
                 '&TargetStoreFlags=' + encodeURIComponent('0') +
                 '&SaveCert=' + encodeURIComponent('no');

    var xmlhttp = getXmlHttp();
    xmlhttp.open("POST", "https://www.cryptopro.ru/certsrv/certfnsh.asp", true);
    xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    var response;
    xmlhttp.onreadystatechange = function() {

		function isIE() {
			var retVal = (("Microsoft Internet Explorer" == navigator.appName) || // IE < 11
				navigator.userAgent.match(/Trident\/./i)); // IE 11
			return retVal;
		}

		if (xmlhttp.readyState == 4) {
            if(xmlhttp.status == 200) {
                response = xmlhttp.responseText;
                var cert_data = "";

                if(!isIE())
                {
                    var start = response.indexOf("var sPKCS7");
                    var end = response.indexOf("sPKCS7 += \"\"") + 13;
                    cert_data = response.substring(start, end);
                }
                else
                {
                    var start = response.indexOf("sPKCS7 & \"") + 9;
                    var end = response.indexOf("& vbNewLine\r\n\r\n</Script>");
                    cert_data = response.substring(start, end);
                    cert_data = cert_data.replace(new RegExp(" & vbNewLine",'g'),";");
                    cert_data = cert_data.replace(new RegExp("&",'g'),"+");
                    cert_data = "var sPKCS7=" + cert_data + ";";
                }

                eval(cert_data);

                try {
                    var Enroll = cadesplugin.CreateObject("X509Enrollment.CX509Enrollment");
                }
                catch (e) {
                    alert('Failed to create X509Enrollment.CX509Enrollment: ' + GetErrorMessage(e));
                    return;
                }

                Enroll.Initialize(0x1);
                Enroll.InstallResponse(0, sPKCS7, 0x7, "");
                var errormes = document.getElementById("boxdiv").style.display = 'none';
                if(location.pathname.indexOf("simple")>=0) {
                    location.reload();
                }
                else if(location.pathname.indexOf("symalgo_sample.html")>=0){
                    FillCertList_NPAPI('CertListBox1');
                    FillCertList_NPAPI('CertListBox2');
                }
                else{
                    FillCertList_NPAPI('CertListBox');
                }
            }
        }
    }
    xmlhttp.send(params);
}

function Encrypt_NPAPI() {

    document.getElementById("DataEncryptedIV1").innerHTML = "";
    document.getElementById("DataEncryptedIV2").innerHTML = "";
    document.getElementById("DataEncryptedDiversData1").innerHTML = "";
    document.getElementById("DataEncryptedDiversData2").innerHTML = "";
    document.getElementById("DataEncryptedBox1").innerHTML = "";
    document.getElementById("DataEncryptedBox2").innerHTML = "";
    document.getElementById("DataEncryptedKey1").innerHTML = "";
    document.getElementById("DataEncryptedKey2").innerHTML = "";
    document.getElementById("DataDecryptedBox1").innerHTML = "";
    document.getElementById("DataDecryptedBox2").innerHTML = "";

    var certificate1 = GetCertificate_NPAPI('CertListBox1');
    if(typeof(certificate1) == 'undefined')
    {
        return;
    }
    var certificate2 = GetCertificate_NPAPI('CertListBox2');
    if(typeof(certificate2) == 'undefined')
    {
        return;
    }

    var dataToEncr1 = Base64.encode(document.getElementById("DataToEncrTxtBox1").value);
    var dataToEncr2 = Base64.encode(document.getElementById("DataToEncrTxtBox2").value);

    try
    {
        FillCertInfo_NPAPI(certificate1, 'cert_info1');
        FillCertInfo_NPAPI(certificate2, 'cert_info2');
        var errormes = "";

        try {
            var oSymAlgo = cadesplugin.CreateObject("cadescom.symmetricalgorithm");
        } catch (err) {
            errormes = "Failed to create cadescom.symmetricalgorithm: " + err;
            alert(errormes);
            throw errormes;
        }

        oSymAlgo.GenerateKey();

        var oSesKey1 = oSymAlgo.DiversifyKey();
        var oSesKey1DiversData = oSesKey1.DiversData;
        document.getElementById("DataEncryptedDiversData1").value = oSesKey1DiversData;
        var oSesKey1IV = oSesKey1.IV;
        document.getElementById("DataEncryptedIV1").value = oSesKey1IV;
        var EncryptedData1 = oSesKey1.Encrypt(dataToEncr1, 1);
        document.getElementById("DataEncryptedBox1").value = EncryptedData1;

        var oSesKey2 = oSymAlgo.DiversifyKey();
        var oSesKey2DiversData = oSesKey2.DiversData;
        document.getElementById("DataEncryptedDiversData2").value = oSesKey2DiversData;
        var oSesKey2IV = oSesKey2.IV;
        document.getElementById("DataEncryptedIV2").value = oSesKey2IV;
        var EncryptedData2 = oSesKey2.Encrypt(dataToEncr2, 1);
        document.getElementById("DataEncryptedBox2").value = EncryptedData2;

        var ExportedKey1 = oSymAlgo.ExportKey(certificate1);
        document.getElementById("DataEncryptedKey1").value = ExportedKey1;

        var ExportedKey2 = oSymAlgo.ExportKey(certificate2);
        document.getElementById("DataEncryptedKey2").value = ExportedKey2;

        alert("Данные зашифрованы успешно:");
    }
    catch(err)
    {
        alert("Ошибка при шифровании данных:" + err);
    }
}

function Decrypt_NPAPI(certListBoxId) {

    document.getElementById("DataDecryptedBox1").value = "";
    document.getElementById("DataDecryptedBox2").value = "";

    var certificate = GetCertificate_NPAPI(certListBoxId);
    if(typeof(certificate) == 'undefined')
    {
        return;
    }
    var dataToDecr1 = document.getElementById("DataEncryptedBox1").value;
    var dataToDecr2 = document.getElementById("DataEncryptedBox2").value;
    var field;
    if(certListBoxId == 'CertListBox1')
        field ="DataEncryptedKey1";
    else
        field ="DataEncryptedKey2";

    var EncryptedKey = document.getElementById(field).value;
    try
    {
        FillCertInfo_NPAPI(certificate, 'cert_info_decr');
        var errormes = "";

        try {
            var oSymAlgo = cadesplugin.CreateObject("cadescom.symmetricalgorithm");
        } catch (err) {
            errormes = "Failed to create cadescom.symmetricalgorithm: " + err;
            alert(errormes);
            throw errormes;
        }
        oSymAlgo.ImportKey(EncryptedKey, certificate);
        var oSesKey1DiversData = document.getElementById("DataEncryptedDiversData1").value;
        var oSesKey1IV = document.getElementById("DataEncryptedIV1").value;
        oSymAlgo.DiversData = oSesKey1DiversData;
        var oSesKey1 = oSymAlgo.DiversifyKey();
        oSesKey1.IV = oSesKey1IV;
        var EncryptedData1 = oSesKey1.Decrypt(dataToDecr1, 1);
        document.getElementById("DataDecryptedBox1").value = Base64.decode(EncryptedData1);
        var oSesKey2DiversData = document.getElementById("DataEncryptedDiversData2").value;
        var oSesKey2IV = document.getElementById("DataEncryptedIV2").value;
        oSymAlgo.DiversData = oSesKey2DiversData;
        var oSesKey2 = oSymAlgo.DiversifyKey();
        oSesKey2.IV = oSesKey2IV;
        var EncryptedData2 = oSesKey2.Decrypt(dataToDecr2, 1);
        document.getElementById("DataDecryptedBox2").value = Base64.decode(EncryptedData2);

        alert("Данные расшифрованы успешно:");
    }
    catch(err)
    {
        alert("Ошибка при шифровании данных:" + err);
    }
}

function SignHashes_NPAPI(thumbprint, docs, sTSAAddress, signCallBack, context) {
	var signedhashes = [];
    docs.forEach(function (doc) {
        var signedhash = SignHash_NPAPI(thumbprint, doc.hash, doc.hashNodeRef, sTSAAddress);
		if (!signedhash.signature) {
            signCallBack(context, signedhash.errorString);
			return;
		}
		signedhashes.push(signedhash);
    });

	if (docs.length === signedhashes.length) {
        signCallBack(context, "", { signResult: signedhashes, certInfo: GetCertificateNPAPI(thumbprint) });
    } else {
        signCallBack(context, "Количество подписей и документов не совпадает.");
    }
}

function SignHash_NPAPI(thumbprint, hash, hashNodeRef, sTSAAddress) {
	var CADESCOM_HASH_ALGORITHM_CP_GOST_3411 = 100;

	thumbprint = thumbprint.replace(/\s/g, "").toUpperCase();
	try {
		var oStore = cadesplugin.CreateObject("CAdESCOM.Store");
		oStore.Open();
	} catch (err) {
		return { signature: null, errorString: ("Ошибка создания объекта CAdESCOM.Store: " + err.number) };
	}

	var CAPICOM_CERTIFICATE_FIND_SHA1_HASH = 0;
	var all_certs = oStore.Certificates;
	var oCerts = all_certs.Find(CAPICOM_CERTIFICATE_FIND_SHA1_HASH, thumbprint);

	if ((oCerts.Count) == 0) {
		return { signature: null, errorString: "Сертификат не найден." };
	}
	var certificate = oCerts.Item(1);


	var Signature;
	try
	{
            FillCertInfo_NPAPI(certificate);
            try {
                var oSigner = cadesplugin.CreateObject("CAdESCOM.CPSigner");
            } catch (err) {
                return { signature: null, errorString: ("Ошибка создания объекта CAdESCOM.CPSigner: " + err.number) };
            }
            //var oSigningTimeAttr = cadesplugin.CreateObject("CADESCOM.CPAttribute");

            //var CAPICOM_AUTHENTICATED_ATTRIBUTE_SIGNING_TIME = 0;
            //oSigningTimeAttr.Name = CAPICOM_AUTHENTICATED_ATTRIBUTE_SIGNING_TIME;
            //var oTimeNow = new Date();
            //oSigningTimeAttr.Value = oTimeNow;
            //var attr = oSigner.AuthenticatedAttributes2;
            //attr.Add(oSigningTimeAttr);


            //var oDocumentNameAttr = cadesplugin.CreateObject("CADESCOM.CPAttribute");
            //var CADESCOM_AUTHENTICATED_ATTRIBUTE_DOCUMENT_NAME = 1;
            //oDocumentNameAttr.Name = CADESCOM_AUTHENTICATED_ATTRIBUTE_DOCUMENT_NAME;
            //oDocumentNameAttr.Value = "Document Name";
            //attr.Add(oDocumentNameAttr);

            if (oSigner) {
                oSigner.Certificate = certificate;
            }
            else {
                return { signature: null, errorString: "Ошибка создания объекта CAdESCOM.CPSigner" };
            }

            //var dataToSign = "AF1nSmg80OdADyoZZVfwX92QFyMovabNEUJ8LLqvTFs=";

            var oSignedData = cadesplugin.CreateObject("CAdESCOM.CadesSignedData");
            var hashAlg = CADESCOM_HASH_ALGORITHM_CP_GOST_3411; // ГОСТ Р 34.11-94
            var sHashValue = hash;
            var oHashedData = cadesplugin.CreateObject("CAdESCOM.HashedData");
            oHashedData.Algorithm = hashAlg;
            oHashedData.SetHashValue(sHashValue);
            var CADES = sTSAAddress ? (sTSAAddress !== "${dsign.tsp.url}" ? 0x5D : 1) : 1;

            if (sHashValue) {
                // Данные на подпись ввели
                //yield oSignedData.propset_Content(dataToSign);
                oSigner.Options = 1; //CAPICOM_CERTIFICATE_INCLUDE_WHOLE_CHAIN
                if (sTSAAddress) {
                    oSigner.TSAAddress = sTSAAddress;
                }
                try {
                    //Signature = yield oSignedData.SignCades(oSigner, CADES_BES);
                    Signature = oSignedData.SignHash(oHashedData, oSigner, CADES);
                }
                catch (err) {
                    return { signature: null, errorString: ("Не удалось создать подпись из-за ошибки: " + GetErrorMessage(err)) };
                }
            }
	}
	catch(err)
	{
        return { signature: null, errorString: ("Не удалось создать подпись из-за ошибки: " + GetErrorMessage(err)) };
	}
    var result = {hash: hash, hashNodeRef: hashNodeRef, signature: Signature};
	return { signature: result, errorString: "" };
}

function verifySignaturesSync (signs, callback) {
    var results = signs.map(function (sign) {
        return verifySignature(sign.hash, sign.signedMessage);
    });

    callback(results);
}

function verifySignature (hash, sSignedMessage){
    var CADESCOM_HASH_ALGORITHM_CP_GOST_3411 = 100;
    var oHashedData = cadesplugin.CreateObject("CAdESCOM.HashedData");
    oHashedData.Algorithm = CADESCOM_HASH_ALGORITHM_CP_GOST_3411; 
    oHashedData.SetHashValue(hash);
    
    var oSignedData = cadesplugin.CreateObject("CAdESCOM.CadesSignedData");
    
    try {
        oSignedData.VerifyHash(oHashedData, sSignedMessage);
    } catch (err) {
        console.log("Failed to verify signature");
        return {"certificate":oSignedData.Signers(1).Certificate, "valid": false};
    }
    return {"certificate":oSignedData.Signers(1).Certificate, "valid": true};
}

function GetCertificateInfo(cert, callback) {

    function getNormalDate(d) {
        return  ('0' + d.getDate()).slice(-2) + '.' + ('0' + (d.getMonth() + 1)).slice(-2) + '.' + d.getFullYear();
    }

    function getAttributeValue(str, attribute) {
        var index = str.indexOf(attribute + "=");
        if (index == -1) {
            return str;
        }
        str = str.substring(index + attribute.length + 1);
        index = str.indexOf(",");
        if (index == -1) {
            return str;
        }
        return str.substring(0, index);
    }

    var result;
    try {
        var Validator = cert.IsValid();
        var IsValid = Validator.Result;
        var ValidFromDate = new Date((cert.ValidFromDate));
        var ValidToDate = new Date((cert.ValidToDate));
        var version = cert.Version;
        var thumbprint = cert.Thumbprint;
        var subject = "";
        var subject = cert.SubjectName;
        var serialNumber = cert.SerialNumber;
        var issuer = cert.IssuerName;
        var hasPrivateKey = cert.HasPrivateKey();
        var shortissuer = getAttributeValue(issuer, "CN");
        var shortsubject = getAttributeValue(subject, "CN");
        result = {
            shortissuer: shortissuer,
            shortsubject: shortsubject,
            validTo: ValidToDate,
            validFrom: ValidFromDate,
            normalValidTo: getNormalDate(ValidToDate),
            normalValidFrom: getNormalDate(ValidFromDate),
            version: version,
            thumbprint: thumbprint,
            subject: subject,
            serialNumber : serialNumber,
            issuer: issuer,
            hasPrivateKey: hasPrivateKey,
            isValid: IsValid
        };
    }catch (ex) {
        console.log("Ошибка при получении информации из объекта сертификата: " + GetErrorMessage(ex));
    }

    callback(result);
}

//-----------------------------------
var Base64 = {


    _keyStr: "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",


    encode: function(input) {
            var output = "";
        var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
        var i = 0;

        input = Base64._utf8_encode(input);

        while (i < input.length) {

            chr1 = input.charCodeAt(i++);
            chr2 = input.charCodeAt(i++);
            chr3 = input.charCodeAt(i++);

            enc1 = chr1 >> 2;
            enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
            enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
            enc4 = chr3 & 63;

            if (isNaN(chr2)) {
                    enc3 = enc4 = 64;
            } else if (isNaN(chr3)) {
                    enc4 = 64;
            }

            output = output + this._keyStr.charAt(enc1) + this._keyStr.charAt(enc2) + this._keyStr.charAt(enc3) + this._keyStr.charAt(enc4);

        }

        return output;
    },


    decode: function(input) {
            var output = "";
        var chr1, chr2, chr3;
        var enc1, enc2, enc3, enc4;
        var i = 0;

        input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

        while (i < input.length) {

            enc1 = this._keyStr.indexOf(input.charAt(i++));
            enc2 = this._keyStr.indexOf(input.charAt(i++));
            enc3 = this._keyStr.indexOf(input.charAt(i++));
            enc4 = this._keyStr.indexOf(input.charAt(i++));

            chr1 = (enc1 << 2) | (enc2 >> 4);
            chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
            chr3 = ((enc3 & 3) << 6) | enc4;

            output = output + String.fromCharCode(chr1);

            if (enc3 != 64) {
                    output = output + String.fromCharCode(chr2);
            }
            if (enc4 != 64) {
                    output = output + String.fromCharCode(chr3);
            }

        }

        output = Base64._utf8_decode(output);

        return output;

    },

    _utf8_encode: function(string) {
            string = string.replace(/\r\n/g, "\n");
        var utftext = "";

        for (var n = 0; n < string.length; n++) {

            var c = string.charCodeAt(n);

            if (c < 128) {
                    utftext += String.fromCharCode(c);
            }
            else if ((c > 127) && (c < 2048)) {
                    utftext += String.fromCharCode((c >> 6) | 192);
                utftext += String.fromCharCode((c & 63) | 128);
            }
            else {
                    utftext += String.fromCharCode((c >> 12) | 224);
                utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                utftext += String.fromCharCode((c & 63) | 128);
            }

        }

        return utftext;
    },

    _utf8_decode: function(utftext) {
            var string = "";
        var i = 0;
        var c = c1 = c2 = 0;

        while (i < utftext.length) {

            c = utftext.charCodeAt(i);

            if (c < 128) {
                    string += String.fromCharCode(c);
                i++;
            }
            else if ((c > 191) && (c < 224)) {
                    c2 = utftext.charCodeAt(i + 1);
                string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
                i += 2;
            }
            else {
                    c2 = utftext.charCodeAt(i + 1);
                c3 = utftext.charCodeAt(i + 2);
                string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
                i += 3;
            }

        }

        return string;
    }

}
