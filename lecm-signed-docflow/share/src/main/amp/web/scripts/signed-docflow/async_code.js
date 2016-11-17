function CertificateAdjuster()
{
}



CertificateAdjuster.prototype.extract = function(from, what)
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

CertificateAdjuster.prototype.Print2Digit = function(digit)
{
    return (digit<10) ? "0"+digit : digit;
}

CertificateAdjuster.prototype.GetCertDate = function(paramDate)
{
    var certDate = new Date(paramDate);
    return this.Print2Digit(certDate.getUTCDate())+"."+this.Print2Digit(certDate.getMonth()+1)+"."+certDate.getFullYear() + " " +
             this.Print2Digit(certDate.getUTCHours()) + ":" + this.Print2Digit(certDate.getUTCMinutes()) + ":" + this.Print2Digit(certDate.getUTCSeconds());
}

CertificateAdjuster.prototype.GetCertName = function(certSubjectName)
{
    return this.extract(certSubjectName, 'CN=');
}

CertificateAdjuster.prototype.GetIssuer = function(certIssuerName)
{
    return this.extract(certIssuerName, 'CN=');
}

CertificateAdjuster.prototype.GetCertInfoString = function(certSubjectName, certFromDate)
{
    return this.extract(certSubjectName,'CN=') + "; Выдан: " + this.GetCertDate(certFromDate);
}

function CheckForPlugIn_Async() {
    function VersionCompare_Async(StringVersion, ObjectVersion)
    {
        if(typeof(ObjectVersion) == "string")
            return -1;
        var arr = StringVersion.split('.');
        var isActualVersion = true;

        cadesplugin.async_spawn(function *() {
            if((yield ObjectVersion.MajorVersion) == parseInt(arr[0]))
            {
                if((yield ObjectVersion.MinorVersion) == parseInt(arr[1]))
                {
                    if((yield ObjectVersion.BuildVersion) == parseInt(arr[2]))
                    {
                        isActualVersion = true;
                    }
                    else if((yield ObjectVersion.BuildVersion) < parseInt(arr[2]))
                    {
                        isActualVersion = false;
                    }
                }else if((yield ObjectVersion.MinorVersion) < parseInt(arr[1]))
                {
                    isActualVersion = false;
                }
            }else if((yield ObjectVersion.MajorVersion) < parseInt(arr[0]))
            {
                isActualVersion = false;
            }

            if(!isActualVersion)
            {
                document.getElementById('PluginEnabledImg').setAttribute("src", "Img/yellow_dot.png");
                document.getElementById('PlugInEnabledTxt').innerHTML = "Плагин загружен, но есть более свежая версия.";
            }
            document.getElementById('PlugInVersionTxt').innerHTML = "Версия плагина: " + (yield CurrentPluginVersion.toString());
            var oAbout = yield cadesplugin.CreateObjectAsync("CAdESCOM.About");
            var ver = yield oAbout.CSPVersion("", 75);
            var ret = (yield ver.MajorVersion) + "." + (yield ver.MinorVersion) + "." + (yield ver.BuildVersion);
            document.getElementById('CSPVersionTxt').innerHTML = "Версия CSP: " + ret;
            return;
        });
    }

    function GetLatestVersion_Async(CurrentPluginVersion) {
        var xmlhttp = getXmlHttp();
        xmlhttp.open("GET", "/sites/default/files/products/cades/latest_2_0.txt", true);
        xmlhttp.onreadystatechange = function() {
        var PluginBaseVersion;
            if (xmlhttp.readyState == 4) {
                if(xmlhttp.status == 200) {
                    PluginBaseVersion = xmlhttp.responseText;
                    VersionCompare_Async(PluginBaseVersion, CurrentPluginVersion)
                }
            }
        }
        xmlhttp.send(null);
    }

    document.getElementById('PluginEnabledImg').setAttribute("src", "Img/green_dot.png");
    document.getElementById('PlugInEnabledTxt').innerHTML = "Плагин загружен.";
    var CurrentPluginVersion;
    cadesplugin.async_spawn(function *() {
        var oAbout = yield cadesplugin.CreateObjectAsync("CAdESCOM.About");
        CurrentPluginVersion = yield oAbout.PluginVersion;
        GetLatestVersion_Async(CurrentPluginVersion);
        if(location.pathname.indexOf("symalgo_sample.html")>=0){
            FillCertList_Async('CertListBox1');
            FillCertList_Async('CertListBox2');
        }else {
            FillCertList_Async('CertListBox');
        }
        // var txtDataToSign = "Hello World";
        // document.getElementById("DataToSignTxtBox").innerHTML = txtDataToSign;
        // document.getElementById("SignatureTxtBox").innerHTML = "";
    }); //cadesplugin.async_spawn
}

function FillCertList_Async(lstId) {
    cadesplugin.async_spawn(function *() {
        var oStore = yield cadesplugin.CreateObjectAsync("CAdESCOM.Store");
        if (!oStore) {
            alert("store failed");
            return;
        }

        try {
            yield oStore.Open();
        }
        catch (ex) {
            alert("Ошибка при открытии хранилища: " + GetErrorMessage(ex));
            return;
        }

        var lst = document.getElementById(lstId);
        if(!lst)
        {
            return;
        }
        var certCnt;
        var certs;

        try {
            certs = yield oStore.Certificates;
            certCnt = yield certs.Count;
        }
        catch (ex) {
            return;
        }

        if(certCnt == 0)
        {
            return;
        }

        for (var i = 1; i <= certCnt; i++) {
            var cert;
            try {
                cert = yield certs.Item(i);
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
                var ValidToDate = new Date((yield cert.ValidToDate));
                var ValidFromDate = new Date((yield cert.ValidFromDate));
                var Validator = yield cert.IsValid();
                var IsValid = yield Validator.Result;
                if(dateObj< ValidToDate && (yield cert.HasPrivateKey()) && IsValid) {
                    oOpt_text = new CertificateAdjuster().GetCertInfoString(yield cert.SubjectName, ValidFromDate);
                }
                else {
                    continue;
                }
            }
            catch (ex) {
                alert("Ошибка при получении свойства SubjectName: " + GetErrorMessage(ex));
            }
            try {
                oOpt_value = yield cert.Thumbprint;
            }
            catch (ex) {
                alert("Ошибка при получении свойства Thumbprint: " + GetErrorMessage(ex));
            }
            var oOpt = new Option(oOpt_text, oOpt_value);
            lst.add(oOpt);
        }

        yield oStore.Close();
    });//cadesplugin.async_spawn
}

function CreateSimpleSign_Async() {
    cadesplugin.async_spawn(function*(arg) {
        try {
            var oStore = yield cadesplugin.CreateObjectAsync("CAdESCOM.Store");
            yield oStore.Open();
        } catch (err) {
            alert('Failed to create CAdESCOM.Store: ' + err.number);
            return;
        }
        var all_certs = yield oStore.Certificates;

        if ((yield all_certs.Count) == 0) {
            var errormes = document.getElementById("boxdiv").style.display = '';
            return;
        }

        var cert;
        var found = 0;
        for (var i = 1; i <= (yield all_certs.Count); i++) {
            try {
                cert = yield all_certs.Item(i);
            }
            catch (ex) {
                alert("Ошибка при перечислении сертификатов: " + GetErrorMessage(ex));
                return;
            }

            var dateObj = new Date();
            try {
                var certDate = new Date((yield cert.ValidToDate));
                var Validator = yield cert.IsValid();
                var IsValid = yield Validator.Result;
                if(dateObj< certDate && (yield cert.HasPrivateKey()) && IsValid) {
                    found = 1;
                    break;
                }
                else {
                    continue;
                }
            }
            catch (ex) {
                alert("Ошибка при получении свойства SubjectName: " + GetErrorMessage(ex));
            }
        }

        if (found == 0) {
            var errormes = document.getElementById("boxdiv").style.display = '';
            return;
        }

        var dataToSign = document.getElementById("DataToSignTxtBox").value;
        var SignatureFieldTitle = document.getElementsByName("SignatureTitle");
        var Signature;
        try
        {
            FillCertInfo_Async(cert);
            var errormes = "";
            try {
                var oSigner = yield cadesplugin.CreateObjectAsync("CAdESCOM.CPSigner");
            } catch (err) {
                errormes = "Failed to create CAdESCOM.CPSigner: " + err.number;
                throw errormes;
            }
            if (oSigner) {
                yield oSigner.propset_Certificate(cert);
            }
            else {
                errormes = "Failed to create CAdESCOM.CPSigner";
                throw errormes;
            }

            var oSignedData = yield cadesplugin.CreateObjectAsync("CAdESCOM.CadesSignedData");
            var CADES_BES = 1;

            if (dataToSign) {
                // Данные на подпись ввели
                yield oSignedData.propset_Content(dataToSign);
                yield oSigner.propset_Options(1); //CAPICOM_CERTIFICATE_INCLUDE_WHOLE_CHAIN
                try {
                    Signature = yield oSignedData.SignCades(oSigner, CADES_BES);
                }
                catch (err) {
                    errormes = "Не удалось создать подпись из-за ошибки: " + GetErrorMessage(err);
                    throw errormes;
                }
            }
            document.getElementById("SignatureTxtBox").innerHTML = Signature;
            SignatureFieldTitle[0].innerHTML = "Подпись сформирована успешно:";
        }
        catch(err)
        {
            SignatureFieldTitle[0].innerHTML = "Возникла ошибка:";
            document.getElementById("SignatureTxtBox").innerHTML = err;
        }
    }); //cadesplugin.async_spawn
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


function SignCadesBES_Async(certListBoxId) {
    cadesplugin.async_spawn(function*(arg) {
        var e = document.getElementById(arg[0]);
        var selectedCertID = e.selectedIndex;
        if (selectedCertID == -1) {
            alert("Select certificate");
            return;
        }

        var thumbprint = e.options[selectedCertID].value.split(" ").reverse().join("").replace(/\s/g, "").toUpperCase();
        try {
            var oStore = yield cadesplugin.CreateObjectAsync("CAdESCOM.Store");
            yield oStore.Open();
        } catch (err) {
            alert('Failed to create CAdESCOM.Store: ' + err.number);
            return;
        }

        var CAPICOM_CERTIFICATE_FIND_SHA1_HASH = 0;
        var all_certs = yield oStore.Certificates;
        var oCerts = yield all_certs.Find(CAPICOM_CERTIFICATE_FIND_SHA1_HASH, thumbprint);

        if ((yield oCerts.Count) == 0) {
            alert("Certificate not found");
            return;
        }
        var certificate = yield oCerts.Item(1);

        var dataToSign = document.getElementById("DataToSignTxtBox").value;
        var SignatureFieldTitle = document.getElementsByName("SignatureTitle");
        var Signature;
        try
        {
            FillCertInfo_Async(certificate);
            var errormes = "";
            try {
                var oSigner = yield cadesplugin.CreateObjectAsync("CAdESCOM.CPSigner");
            } catch (err) {
                errormes = "Failed to create CAdESCOM.CPSigner: " + err.number;
                throw errormes;
            }
            var oSigningTimeAttr = yield cadesplugin.CreateObjectAsync("CADESCOM.CPAttribute");

            var CAPICOM_AUTHENTICATED_ATTRIBUTE_SIGNING_TIME = 0;
            yield oSigningTimeAttr.propset_Name(CAPICOM_AUTHENTICATED_ATTRIBUTE_SIGNING_TIME);
            var oTimeNow = new Date();
            yield oSigningTimeAttr.propset_Value(oTimeNow);
            var attr = yield oSigner.AuthenticatedAttributes2;
            yield attr.Add(oSigningTimeAttr);


            var oDocumentNameAttr = yield cadesplugin.CreateObjectAsync("CADESCOM.CPAttribute");
            var CADESCOM_AUTHENTICATED_ATTRIBUTE_DOCUMENT_NAME = 1;
            yield oDocumentNameAttr.propset_Name(CADESCOM_AUTHENTICATED_ATTRIBUTE_DOCUMENT_NAME);
            yield oDocumentNameAttr.propset_Value("Document Name");
            yield attr.Add(oDocumentNameAttr);

            if (oSigner) {
                yield oSigner.propset_Certificate(certificate);
            }
            else {
                errormes = "Failed to create CAdESCOM.CPSigner";
                throw errormes;
            }


            var oSignedData = yield cadesplugin.CreateObjectAsync("CAdESCOM.CadesSignedData");
            var CADES_BES = 1;

            if (dataToSign) {
                // Данные на подпись ввели
                yield oSignedData.propset_Content(dataToSign);
                yield oSigner.propset_Options(1); //CAPICOM_CERTIFICATE_INCLUDE_WHOLE_CHAIN
                try {
                    Signature = yield oSignedData.SignCades(oSigner, CADES_BES);
                }
                catch (err) {
                    errormes = "Не удалось создать подпись из-за ошибки: " + GetErrorMessage(err);
                    throw errormes;
                }
            }
            document.getElementById("SignatureTxtBox").innerHTML = Signature;
            SignatureFieldTitle[0].innerHTML = "Подпись сформирована успешно:";
        }
        catch(err)
        {
            SignatureFieldTitle[0].innerHTML = "Возникла ошибка:";
            document.getElementById("SignatureTxtBox").innerHTML = err;
        }
    }, certListBoxId); //cadesplugin.async_spawn
}

function SignCadesXLong_Async(certListBoxId) {
    cadesplugin.async_spawn(function*(arg) {
        var e = document.getElementById(arg[0]);
        var selectedCertID = e.selectedIndex;
        if (selectedCertID == -1) {
            alert("Select certificate");
            return;
        }

        var thumbprint = e.options[selectedCertID].value.split(" ").reverse().join("").replace(/\s/g, "").toUpperCase();
        try {
            var oStore = yield cadesplugin.CreateObjectAsync("CAdESCOM.Store");
            yield oStore.Open();
        } catch (err) {
            alert('Failed to create CAdESCOM.Store: ' + err.number);
            return;
        }

        var CAPICOM_CERTIFICATE_FIND_SHA1_HASH = 0;
        var all_certs = yield oStore.Certificates;
        var oCerts = yield all_certs.Find(CAPICOM_CERTIFICATE_FIND_SHA1_HASH, thumbprint);

        if ((yield oCerts.Count) == 0) {
            alert("Certificate not found");
            return;
        }
        var certificate = yield oCerts.Item(1);

        var dataToSign = document.getElementById("DataToSignTxtBox").value;
        var SignatureFieldTitle = document.getElementsByName("SignatureTitle");
        var Signature;
        try
        {
            FillCertInfo_Async(certificate);
            var errormes = "";
            try {
                var oSigner = yield cadesplugin.CreateObjectAsync("CAdESCOM.CPSigner");
            } catch (err) {
                errormes = "Failed to create CAdESCOM.CPSigner: " + err.number;
                throw errormes;
            }
            if (oSigner) {
                yield oSigner.propset_Certificate(certificate);
            }
            else {
                errormes = "Failed to create CAdESCOM.CPSigner";
                throw errormes;
            }

            var oSignedData = yield cadesplugin.CreateObjectAsync("CAdESCOM.CadesSignedData");
            var CADESCOM_CADES_X_LONG_TYPE_1 = 0x5d;
            var tspService = document.getElementById("TSPServiceTxtBox").value ;

            if (dataToSign) {
                // Данные на подпись ввели
                yield oSignedData.propset_Content(dataToSign);
                yield oSigner.propset_Options(1); //CAPICOM_CERTIFICATE_INCLUDE_WHOLE_CHAIN
                yield oSigner.propset_TSAAddress(tspService);
                try {
                    Signature = yield oSignedData.SignCades(oSigner, CADESCOM_CADES_X_LONG_TYPE_1);
                }
                catch (err) {
                    errormes = "Не удалось создать подпись из-за ошибки: " + GetErrorMessage(err);
                    throw errormes;
                }
            }
            document.getElementById("SignatureTxtBox").innerHTML = Signature;
            SignatureFieldTitle[0].innerHTML = "Подпись сформирована успешно:";
        }
        catch(err)
        {
            SignatureFieldTitle[0].innerHTML = "Возникла ошибка:";
            document.getElementById("SignatureTxtBox").innerHTML = err;
        }
    }, certListBoxId); //cadesplugin.async_spawn
}

function SignCadesXML_Async(certListBoxId) {
    cadesplugin.async_spawn(function*(arg) {
        var e = document.getElementById(arg[0]);
        var selectedCertID = e.selectedIndex;
        if (selectedCertID == -1) {
            alert("Select certificate");
            return;
        }

        var thumbprint = e.options[selectedCertID].value.split(" ").reverse().join("").replace(/\s/g, "").toUpperCase();
        try {
            var oStore = yield cadesplugin.CreateObjectAsync("CAdESCOM.Store");
            yield oStore.Open();
        } catch (err) {
            alert('Failed to create CAdESCOM.Store: ' + err.number);
            return;
        }

        var CAPICOM_CERTIFICATE_FIND_SHA1_HASH = 0;
        var all_certs = yield oStore.Certificates;
        var oCerts = yield all_certs.Find(CAPICOM_CERTIFICATE_FIND_SHA1_HASH, thumbprint);

        if ((yield oCerts.Count) == 0) {
            alert("Certificate not found");
            return;
        }
        var certificate = yield oCerts.Item(1);

        var dataToSign = document.getElementById("DataToSignTxtBox").value;
        var SignatureFieldTitle = document.getElementsByName("SignatureTitle");
        var Signature;
        try
        {
            FillCertInfo_Async(certificate);
            var errormes = "";
            try {
                var oSigner = yield cadesplugin.CreateObjectAsync("CAdESCOM.CPSigner");
            } catch (err) {
                errormes = "Failed to create CAdESCOM.CPSigner: " + err.number;
                throw errormes;
            }
            if (oSigner) {
                yield oSigner.propset_Certificate(certificate);
            }
            else {
                errormes = "Failed to create CAdESCOM.CPSigner";
                throw errormes;
            }

            var oSignedXML = yield cadesplugin.CreateObjectAsync("CAdESCOM.SignedXML");

            var XmlDsigGost3410Url = "urn:ietf:params:xml:ns:cpxmlsec:algorithms:gostr34102001-gostr3411";
            var XmlDsigGost3411Url = "urn:ietf:params:xml:ns:cpxmlsec:algorithms:gostr3411";
            var CADESCOM_XML_SIGNATURE_TYPE_ENVELOPED = 0;

            if (dataToSign) {
                // Данные на подпись ввели
                yield oSignedXML.propset_Content(dataToSign);
                yield oSignedXML.propset_SignatureType(CADESCOM_XML_SIGNATURE_TYPE_ENVELOPED);
                yield oSignedXML.propset_SignatureMethod(XmlDsigGost3410Url);
                yield oSignedXML.propset_DigestMethod(XmlDsigGost3411Url);

                try {
                    Signature = yield oSignedXML.Sign(oSigner);
                }
                catch (err) {
                    errormes = "Не удалось создать подпись из-за ошибки: " + GetErrorMessage(err);
                    throw errormes;
                }
            }
            document.getElementById("SignatureTxtBox").innerHTML = Signature;
            SignatureFieldTitle[0].innerHTML = "Подпись сформирована успешно:";
        }
        catch(err)
        {
            SignatureFieldTitle[0].innerHTML = "Возникла ошибка:";
            document.getElementById("SignatureTxtBox").innerHTML = err;
        }
    }, certListBoxId); //cadesplugin.async_spawn
}

function FillCertInfo_Async(certificate, certBoxId)
{
    var BoxId;
    var field_prefix;
    if(typeof(certBoxId) == 'undefined')
    {
        BoxId = 'cert_info';
        field_prefix = '';
    }else {
        BoxId = certBoxId;
        field_prefix = certBoxId;
    }
    cadesplugin.async_spawn (function*(args) {
        var Adjust = new CertificateAdjuster();
        //document.getElementById(args[1]).style.display = '';
        //document.getElementById(args[2] + "subject").innerHTML = "Владелец: <b>" + Adjust.GetCertName(yield args[0].SubjectName) + "<b>";
        //document.getElementById(args[2] + "issuer").innerHTML = "Издатель: <b>" + Adjust.GetIssuer(yield args[0].IssuerName) + "<b>";
        //document.getElementById(args[2] + "from").innerHTML = "C <b>" + Adjust.GetCertDate(yield args[0].ValidFromDate); + "<b>";
        //document.getElementById(args[2] + "till").innerHTML = "Действителен до: <b>" + Adjust.GetCertDate(yield args[0].ValidToDate) + "<b>";
        var pubKey = yield args[0].PublicKey();
        var algo = yield pubKey.Algorithm;
        var fAlgoName = yield algo.FriendlyName;
        //document.getElementById(args[2] + "algorithm").innerHTML = "Алгоритм ключа: <b>" + fAlgoName + "<b>";
    }, certificate, BoxId, field_prefix);//cadesplugin.async_spawn
}

function Encrypt_Async() {
    cadesplugin.async_spawn (function*() {
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

        //Get First certificate
        var e = document.getElementById('CertListBox1');
        var selectedCertID = e.selectedIndex;
        if (selectedCertID == -1) {
            alert("Select certificate");
            return;
        }

        var thumbprint = e.options[selectedCertID].value.split(" ").reverse().join("").replace(/\s/g, "").toUpperCase();
        try {
            var oStore = yield cadesplugin.CreateObjectAsync("CAdESCOM.Store");
            yield oStore.Open();
        } catch (err) {
            alert('Failed to create CAdESCOM.Store: ' + err.number);
            return;
        }

        var CAPICOM_CERTIFICATE_FIND_SHA1_HASH = 0;
        var all_certs = yield oStore.Certificates;
        var oCerts = yield all_certs.Find(CAPICOM_CERTIFICATE_FIND_SHA1_HASH, thumbprint);

        if ((yield oCerts.Count) == 0) {
            alert("Certificate not found");
            return;
        }
        var certificate1 = yield oCerts.Item(1);

        //Get second Certificate
        var e = document.getElementById('CertListBox2');
        var selectedCertID = e.selectedIndex;
        if (selectedCertID == -1) {
            alert("Select certificate");
            return;
        }

        var thumbprint = e.options[selectedCertID].value.split(" ").reverse().join("").replace(/\s/g, "").toUpperCase();
        var oCerts = yield all_certs.Find(CAPICOM_CERTIFICATE_FIND_SHA1_HASH, thumbprint);

        if ((yield oCerts.Count) == 0) {
            alert("Certificate not found");
            return;
        }
        var certificate2 = yield oCerts.Item(1);

        var dataToEncr1 = Base64.encode(document.getElementById("DataToEncrTxtBox1").value);
        var dataToEncr2 = Base64.encode(document.getElementById("DataToEncrTxtBox2").value);

        try
        {
            FillCertInfo_Async(certificate1, 'cert_info1');
            FillCertInfo_Async(certificate2, 'cert_info2');
            var errormes = "";

            try {
                var oSymAlgo = yield cadesplugin.CreateObjectAsync("cadescom.symmetricalgorithm");
            } catch (err) {
                errormes = "Failed to create cadescom.symmetricalgorithm: " + err;
                alert(errormes);
                throw errormes;
            }

            yield oSymAlgo.GenerateKey();

            var oSesKey1 = yield oSymAlgo.DiversifyKey();
            var oSesKey1DiversData = yield oSesKey1.DiversData;
            var oSesKey1IV = yield oSesKey1.IV;
            var EncryptedData1 = yield oSesKey1.Encrypt(dataToEncr1, 1);
            document.getElementById("DataEncryptedDiversData1").innerHTML = oSesKey1DiversData;
            document.getElementById("DataEncryptedIV1").innerHTML = oSesKey1IV;
            document.getElementById("DataEncryptedBox1").innerHTML = EncryptedData1;

            var oSesKey2 = yield oSymAlgo.DiversifyKey();
            var oSesKey2DiversData = yield oSesKey2.DiversData;
            var oSesKey2IV = yield oSesKey2.IV;
            var EncryptedData2 = yield oSesKey2.Encrypt(dataToEncr2, 1);
            document.getElementById("DataEncryptedDiversData2").innerHTML = oSesKey2DiversData;
            document.getElementById("DataEncryptedIV2").innerHTML = oSesKey2IV;
            document.getElementById("DataEncryptedBox2").innerHTML = EncryptedData2;

            var ExportedKey1 = yield oSymAlgo.ExportKey(certificate1);
            document.getElementById("DataEncryptedKey1").innerHTML = ExportedKey1;

            var ExportedKey2 = yield oSymAlgo.ExportKey(certificate2);
            document.getElementById("DataEncryptedKey2").innerHTML = ExportedKey2;

            alert("Данные зашифрованы успешно:");
        }
        catch(err)
        {
            alert("Ошибка при шифровании данных:" + err);
            trow("Ошибка при шифровании данных:" + err);
        }
    });//cadesplugin.async_spawn
}

function Decrypt_Async(certListBoxId) {
    cadesplugin.async_spawn (function*(arg) {
        document.getElementById("DataDecryptedBox1").innerHTML = "";
        document.getElementById("DataDecryptedBox2").innerHTML = "";

        var e = document.getElementById(arg[0]);
        var selectedCertID = e.selectedIndex;
        if (selectedCertID == -1) {
            alert("Select certificate");
            return;
        }

        var thumbprint = e.options[selectedCertID].value.split(" ").reverse().join("").replace(/\s/g, "").toUpperCase();
        try {
            var oStore = yield cadesplugin.CreateObjectAsync("CAdESCOM.Store");
            yield oStore.Open();
        } catch (err) {
            alert('Failed to create CAdESCOM.Store: ' + err.number);
            return;
        }

        var CAPICOM_CERTIFICATE_FIND_SHA1_HASH = 0;
        var all_certs = yield oStore.Certificates;
        var oCerts = yield all_certs.Find(CAPICOM_CERTIFICATE_FIND_SHA1_HASH, thumbprint);

        if ((yield oCerts.Count) == 0) {
            alert("Certificate not found");
            return;
        }
        var certificate = yield oCerts.Item(1);

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
            FillCertInfo_Async(certificate, 'cert_info_decr');
            var errormes = "";

            try {
                var oSymAlgo = yield cadesplugin.CreateObjectAsync("cadescom.symmetricalgorithm");
            } catch (err) {
                errormes = "Failed to create cadescom.symmetricalgorithm: " + err;
                alert(errormes);
                throw errormes;
            }

            yield oSymAlgo.ImportKey(EncryptedKey, certificate);

            var oSesKey1DiversData = document.getElementById("DataEncryptedDiversData1").value;
            var oSesKey1IV = document.getElementById("DataEncryptedIV1").value;
            yield oSymAlgo.propset_DiversData(oSesKey1DiversData);
            var oSesKey1 = yield oSymAlgo.DiversifyKey();
            yield oSesKey1.propset_IV(oSesKey1IV);
            var EncryptedData1 = yield oSesKey1.Decrypt(dataToDecr1, 1);
            document.getElementById("DataDecryptedBox1").innerHTML = Base64.decode(EncryptedData1);

            var oSesKey2DiversData = document.getElementById("DataEncryptedDiversData2").value;
            var oSesKey2IV = document.getElementById("DataEncryptedIV2").value;
            yield oSymAlgo.propset_DiversData(oSesKey2DiversData);
            var oSesKey2 = yield oSymAlgo.DiversifyKey();
            yield oSesKey2.propset_IV(oSesKey2IV);
            var EncryptedData2 = yield oSesKey2.Decrypt(dataToDecr2, 1);
            document.getElementById("DataDecryptedBox2").innerHTML = Base64.decode(EncryptedData2);

            alert("Данные расшифрованы успешно:");
        }
        catch(err)
        {
            alert("Ошибка при шифровании данных:" + err);
            throw("Ошибка при шифровании данных:" + err);
        }
    }, certListBoxId);//cadesplugin.async_spawn
}

function RetrieveCertificate_Async()
{
    cadesplugin.async_spawn (function*(arg) {
        try {
            var PrivateKey = yield cadesplugin.CreateObjectAsync("X509Enrollment.CX509PrivateKey");
        }
        catch (e) {
            alert('Failed to create X509Enrollment.CX509PrivateKey: ' + GetErrorMessage(e));
            return;
        }

        yield PrivateKey.propset_ProviderName("Crypto-Pro GOST R 34.10-2001 Cryptographic Service Provider");
        yield PrivateKey.propset_ProviderType(75);
        yield PrivateKey.propset_KeySpec(1); //XCN_AT_KEYEXCHANGE

        try {
            var CertificateRequestPkcs10 = yield cadesplugin.CreateObjectAsync("X509Enrollment.CX509CertificateRequestPkcs10");
        }
        catch (e) {
            alert('Failed to create X509Enrollment.CX509CertificateRequestPkcs10: ' + GetErrorMessage(e));
            return;
        }

        yield CertificateRequestPkcs10.InitializeFromPrivateKey(0x1, PrivateKey, "");

        try {
            var DistinguishedName = yield cadesplugin.CreateObjectAsync("X509Enrollment.CX500DistinguishedName");
        }
        catch (e) {
            alert('Failed to create X509Enrollment.CX500DistinguishedName: ' + GetErrorMessage(e));
            return;
        }

        var CommonName = "Test Certificate";
        yield DistinguishedName.Encode("CN=\""+CommonName.replace(/"/g, "\"\"")+"\";");

        yield CertificateRequestPkcs10.propset_Subject(DistinguishedName);

        var KeyUsageExtension = yield cadesplugin.CreateObjectAsync("X509Enrollment.CX509ExtensionKeyUsage");
        var CERT_DATA_ENCIPHERMENT_KEY_USAGE = 0x10;
        var CERT_KEY_ENCIPHERMENT_KEY_USAGE = 0x20;
        var CERT_DIGITAL_SIGNATURE_KEY_USAGE = 0x80;
        var CERT_NON_REPUDIATION_KEY_USAGE = 0x40;

        yield KeyUsageExtension.InitializeEncode(
                    CERT_KEY_ENCIPHERMENT_KEY_USAGE |
                    CERT_DATA_ENCIPHERMENT_KEY_USAGE |
                    CERT_DIGITAL_SIGNATURE_KEY_USAGE |
                    CERT_NON_REPUDIATION_KEY_USAGE);

        yield (yield CertificateRequestPkcs10.X509Extensions).Add(KeyUsageExtension);

        try {
            var Enroll = yield cadesplugin.CreateObjectAsync("X509Enrollment.CX509Enrollment");
        }
        catch (e) {
            alert('Failed to create X509Enrollment.CX509Enrollment: ' + GetErrorMessage(e));
            return;
        }

        yield Enroll.InitializeFromRequest(CertificateRequestPkcs10);

        var cert_req = yield Enroll.CreateRequest(0x1);

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
                    cadesplugin.async_spawn (function*(arg) {
                        var response = arg[0];
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
                            var Enroll = yield cadesplugin.CreateObjectAsync("X509Enrollment.CX509Enrollment");
                        }
                        catch (e) {
                            alert('Failed to create X509Enrollment.CX509Enrollment: ' + GetErrorMessage(e));
                            return;
                        }

                        yield Enroll.Initialize(0x1);
                        yield Enroll.InstallResponse(0, sPKCS7, 0x7, "");
                        var errormes = document.getElementById("boxdiv").style.display = 'none';
                        if(location.pathname.indexOf("simple")>=0) {
                            location.reload();
                        }
                        else if(location.pathname.indexOf("symalgo_sample.html")>=0){
                            FillCertList_Async('CertListBox1');
                            FillCertList_Async('CertListBox2');
                        }
                        else{
                            FillCertList_Async('CertListBox');
                        }
                    }, xmlhttp.responseText);//cadesplugin.async_spawn
                }
            }
        }
        xmlhttp.send(params);
    });//cadesplugin.async_spawn
}

function CheckForPlugInUEC_Async()
{
    var isUECCSPInstalled = false;

    cadesplugin.async_spawn(function *() {
        var oAbout = yield cadesplugin.CreateObjectAsync("CAdESCOM.About");

        var UECCSPVersion;
        var CurrentPluginVersion = yield oAbout.PluginVersion;
        if( typeof(CurrentPluginVersion) == "undefined")
            CurrentPluginVersion = yield oAbout.Version;

        var PluginBaseVersion = "1.5.1633";
        var arr = PluginBaseVersion.split('.');

        var isActualVersion = true;

        if((yield CurrentPluginVersion.MajorVersion) == parseInt(arr[0]))
        {
            if((yield CurrentPluginVersion.MinorVersion) == parseInt(arr[1]))
            {
                if((yield CurrentPluginVersion.BuildVersion) == parseInt(arr[2]))
                {
                    isActualVersion = true;
                }
                else if((yield CurrentPluginVersion.BuildVersion) < parseInt(arr[2]))
                {
                    isActualVersion = false;
                }
            }else if((yield CurrentPluginVersion.MinorVersion) < parseInt(arr[1]))
            {
                    isActualVersion = false;
            }
        }else if((yield CurrentPluginVersion.MajorVersion) < parseInt(arr[0]))
        {
            isActualVersion = false;
        }

        if(!isActualVersion)
        {
            document.getElementById('PluginEnabledImg').setAttribute("src", "Img/yellow_dot.png");
            document.getElementById('PlugInEnabledTxt').innerHTML = "Плагин загружен, но он не поддерживает УЭК.";
        }
        else
        {
            document.getElementById('PluginEnabledImg').setAttribute("src", "Img/green_dot.png");
            document.getElementById('PlugInEnabledTxt').innerHTML = "Плагин загружен.";

            try
            {
                var oUECard = yield cadesplugin.CreateObjectAsync("CAdESCOM.UECard");
                UECCSPVersion = yield oUECard.ProviderVersion;
                isUECCSPInstalled = true;
            }
            catch (err)
            {
                UECCSPVersion = "Нет информации";
            }

            if(!isUECCSPInstalled)
            {
                document.getElementById('PluginEnabledImg').setAttribute("src", "Img/yellow_dot.png");
                document.getElementById('PlugInEnabledTxt').innerHTML = "Плагин загружен. Не установлен УЭК CSP.";
            }
        }
        document.getElementById('PlugInVersionTxt').innerHTML = "Версия плагина: " + (yield CurrentPluginVersion.toString());
        document.getElementById('CSPVersionTxt').innerHTML = "Версия УЭК CSP: " + (yield UECCSPVersion.toString());
    }); //cadesplugin.async_spawn
}

function FoundCertInStore_Async(cerToFind) {
    return new Promise(function(resolve, reject){
        cadesplugin.async_spawn(function *(args) {

            if((typeof cerToFind == "undefined") || (cerToFind == null))
                args[0](false);

            var oStore = yield cadesplugin.CreateObjectAsync("CAPICOM.store");
            if (!oStore) {
                alert("store failed");
                args[0](false);
            }
            try {
                yield oStore.Open();
            }
            catch (ex) {
                alert("Ошибка при открытии хранилища: " + GetErrorMessage(ex));
                args[0](false);
            }

            var certCnt;

            var Certificates = yield oStore.Certificates;
            var certCnt = yield Certificates.Count;
            if(certCnt==0)
            {
                oStore.Close();
                args[0](false);
            }

            var ThumbprintToFind = yield cerToFind.Thumbprint;

            for (var i = 1; i <= certCnt; i++) {
                var cert;
                try {
                    cert = yield Certificates.Item(i);
                }
                catch (ex) {
                    alert("Ошибка при перечислении сертификатов: " + GetErrorMessage(ex));
                    args[0](false);
                }

                try {
                    var Thumbprint = yield cert.Thumbprint;
                    if(Thumbprint == ThumbprintToFind) {
                        var dateObj = new Date();
                        var ValidToDate = new Date(yield cert.ValidToDate);
                        var HasPrivateKey = yield cert.HasPrivateKey();
                        var IsValid = yield cert.IsValid();
                        IsValid = yield IsValid.Result;

                        if(dateObj<ValidToDate && HasPrivateKey && IsValid) {
                            args[0](true);
                        }
                    }
                    else {
                        continue;
                    }
                }
                catch (ex) {
                    alert("Ошибка при получении свойства Thumbprint: " + GetErrorMessage(ex));
                    args[0](false);
                }
            }
            oStore.Close();

            args[0](false);

        }, resolve, reject);
    });
}

function getUECCertificate_Async() {
    return new Promise(function(resolve, reject)
        {
            showWaitMessage("Выполняется загрузка сертификата, это может занять несколько секунд...");
            cadesplugin.async_spawn(function *(args) {
                try {
                    var oCard = yield cadesplugin.CreateObjectAsync("CAdESCOM.UECard");
                    var oCertTemp = yield oCard.Certificate;

                    if(typeof oCertTemp == "undefined")
                    {
                        document.getElementById("cert_info1").style.display = '';
                        document.getElementById("certerror").innerHTML = "Сертификат не найден или отсутствует.";
                        throw "";
                    }

                    if(oCertTemp==null)
                    {
                        document.getElementById("cert_info1").style.display = '';
                        document.getElementById("certerror").innerHTML = "Сертификат не найден или отсутствует.";
                        throw "";
                    }

                    if(yield FoundCertInStore_Async(oCertTemp)) {
                        FillCertInfo_Async(oCertTemp);
                        g_oCert = oCertTemp;
                    }
                    else {
                        document.getElementById("cert_info1").style.display = '';
                        document.getElementById("certerror").innerHTML = "Сертификат не найден в хранилище MY.";
                        throw "";
                    }
                    args[0]();
                }
                catch (e) {
                    var message = GetErrorMessage(e);
                    if("The action was cancelled by the user. (0x8010006E)" == message) {
                        document.getElementById("cert_info1").style.display = '';
                        document.getElementById("certerror").innerHTML = "Карта не найдена или отсутствует сертификат на карте.";
                    }
                    args[1]();
                }
            }, resolve, reject);
        });
}

function createSignature_Async() {
    return new Promise(function(resolve, reject){
        cadesplugin.async_spawn(function *(args) {
            var signedMessage = "";
            try {
                var oSigner = yield cadesplugin.CreateObjectAsync("CAdESCOM.CPSigner");
                yield oSigner.propset_Certificate(g_oCert);
                var CAPICOM_CERTIFICATE_INCLUDE_WHOLE_CHAIN = 1;
                yield oSigner.propset_Options(CAPICOM_CERTIFICATE_INCLUDE_WHOLE_CHAIN);

                var oSignedData = yield cadesplugin.CreateObjectAsync("CAdESCOM.CadesSignedData");
                yield oSignedData.propset_Content("DataToSign");

                var CADES_BES = 1;
                signedMessage = yield oSignedData.SignCades(oSigner, CADES_BES);
                args[0](signedMessage);
            }
            catch (e) {
                showErrorMessage("Ошибка: Не удалось создать подпись. Код ошибки: " + GetErrorMessage(e));
                args[1]("");
            }
            args[0](signedMessage);
        }, resolve, reject);
    });
}

function verifyCert_Async() {
    if (!g_oCert) {
        removeWaitMessage();
        return;
    }
    createSignature_Async().then(
        function(signedMessage){
            document.getElementById("SignatureTxtBox").innerHTML = signedMessage;
            var x = document.getElementsByName("SignatureTitle");
            x[0].innerHTML = "Подпись сформирована успешно:";
            removeWaitMessage();
        },
        function(signedMessage){
            removeWaitMessage();
        }
    );
}

function GetES6Certs(lstId, context) {
	cadesplugin.async_spawn(function *() {
		yield cadesplugin;
		var oStore = yield cadesplugin.CreateObjectAsync("CAdESCOM.Store");
		if (!oStore) {
			alert("store failed");
			return;
		}

		try {
			yield oStore.Open();
		}
		catch (ex) {
			alert("Ошибка при открытии хранилища: " + GetErrorMessage(ex));
			return;
		}

		var lst = document.getElementById(lstId);
		if(!lst)
		{
			return;
		}
		var certCnt;
		var certs;

		try {
			certs = yield oStore.Certificates;
			certCnt = yield certs.Count;
		}
		catch (ex) {
			return;
		}

		if(certCnt == 0)
		{
			return;
		}

		for (var i = 1; i <= certCnt; i++) {
			var cert;
			try {
				cert = yield certs.Item(i);
			}
			catch (ex) {
				alert("Ошибка при перечислении сертификатов: " + GetErrorMessage(ex));
				return;
			}

			var oOpt = document.createElement("OPTION");
			var dateObj = new Date();
			try {
				var ValidToDate = new Date((yield cert.ValidToDate));
				var ValidFromDate = new Date((yield cert.ValidFromDate));
				var Validator = yield cert.IsValid();
				var IsValid = yield Validator.Result;
				if(dateObj< ValidToDate && (yield cert.HasPrivateKey()) && IsValid) {
					oOpt.text = new CertificateAdjuster().GetCertInfoString(yield cert.SubjectName, ValidFromDate);
				}
				else {
					continue;
				}
			}
			catch (ex) {
				alert("Ошибка при получении свойства SubjectName: " + GetErrorMessage(ex));
			}
			try {
				oOpt.value = yield cert.Thumbprint;
			}
			catch (ex) {
				alert("Ошибка при получении свойства Thumbprint: " + GetErrorMessage(ex));
			}

			lst.options.add(oOpt);
		}

		yield oStore.Close();
	});//.then(function (){ context.loadCertificatesCallBack(); });
}

function GetES6CertsJson(context) {
	cadesplugin.async_spawn(function *() {
		yield cadesplugin;
		var oStore = yield cadesplugin.CreateObjectAsync("CAdESCOM.Store");
		if (!oStore) {
			alert("store failed");
			return;
		}

		try {
			yield oStore.Open();
		}
		catch (ex) {
			alert("Ошибка при открытии хранилища: " + GetErrorMessage(ex));
			return;
		}

		var certCnt;
		var certs;

		try {
			certs = yield oStore.Certificates;
			certCnt = yield certs.Count;
		}
		catch (ex) {
			return;
		}

		if(certCnt == 0)
		{
			return;
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
				cert = yield certs.Item(i);
			}
			catch (ex) {
				alert("Ошибка при перечислении сертификатов: " + GetErrorMessage(ex));
				return;
			}

			var dateObj = new Date();
			try {
                var Validator = yield cert.IsValid();
                var IsValid = yield Validator.Result;
                var ValidFromDate = new Date((yield cert.ValidFromDate));
                var ValidToDate = new Date((yield cert.ValidToDate));

				if(dateObj< ValidToDate && (yield cert.HasPrivateKey()) && IsValid) {
                    var version = yield cert.Version;                           //3
                    var thumbprint = yield cert.Thumbprint;                     //"040F78E8E5A619546EAF9AB891174BBAA00093B5"
                    var subject = "";
                    try {
                        var subject = yield cert.SubjectName;                       //"STREET=У 1, CN=Теле2_Тест_3, SN=Тестов, G=Тест Третий, C=RU, S=77 г. Москва, L=г. М, O=Теле2_Тест_3, T=Директор, OGRN=0067333755082, SNILS=26481117067, INN=009999324755"
                    } catch(ex) {
                    };
                    var serialNumber = yield cert.SerialNumber;                 //"01D0D0E54C4B5D00000000000379085D"
                    var issuer = yield cert.IssuerName;                         //"CN=ЗАО Калуга Астрал (УЦ 889), O=ЗАО Калуга Астрал, E=ca@astralnalog.ru, S=40 Калужская область, L=Калуга, C=RU, INN=004029017981, OGRN=1024001434049, STREET=Улица Циолковского дом 4"
                    var hasPrivateKey = yield cert.HasPrivateKey();             //true
                    var privateKey = yield cert.PrivateKey;
                    var containerName = yield privateKey.ContainerName;    //27B340E2-FD1D-4B0C-9561-F05FBF51FF87
                    var providerName = yield privateKey.ProviderName;      //Crypto-Pro GOST R 34.10-2001 Cryptographic Service Provider
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

		yield oStore.Close();
        return yield result;
	}).then(function (result){ context.loadCertificatesCallBack(result); });
}

function SignES6Hash(thumbprint, hash, hashNodeRef, sTSAAddress) {

	return cadesplugin.async_spawn(function *() {
		yield cadesplugin;
		var CADESCOM_HASH_ALGORITHM_CP_GOST_3411 = 100;

		thumbprint = thumbprint.replace(/\s/g, "").toUpperCase();
		try {
			var oStore = yield cadesplugin.CreateObjectAsync("CAdESCOM.Store");
			yield oStore.Open();
		} catch (err) {
			return yield { signature: null, errorString: ("Ошибка создания объекта CAdESCOM.Store: " + err.number) };
		}

		var CAPICOM_CERTIFICATE_FIND_SHA1_HASH = 0;
		var all_certs = yield oStore.Certificates;
		var oCerts = yield all_certs.Find(CAPICOM_CERTIFICATE_FIND_SHA1_HASH, thumbprint);

		if ((yield oCerts.Count) == 0) {
			return yield { signature: null, errorString: "Сертификат не найден." };
		}
		var certificate = yield oCerts.Item(1);


		var Signature;
        var Error;
		try
		{
			FillCertInfo_Async(certificate);
			try {
				var oSigner = yield cadesplugin.CreateObjectAsync("CAdESCOM.CPSigner");
			} catch (err) {
				return yield { signature: null, errorString: ("Ошибка создания объекта CAdESCOM.CPSigner: " + err.number) };
			}
			var oSigningTimeAttr = yield cadesplugin.CreateObjectAsync("CADESCOM.CPAttribute");

			var CAPICOM_AUTHENTICATED_ATTRIBUTE_SIGNING_TIME = 0;
			yield oSigningTimeAttr.propset_Name(CAPICOM_AUTHENTICATED_ATTRIBUTE_SIGNING_TIME);
			var oTimeNow = new Date();
			yield oSigningTimeAttr.propset_Value(oTimeNow);
			var attr = yield oSigner.AuthenticatedAttributes2;
			yield attr.Add(oSigningTimeAttr);


			var oDocumentNameAttr = yield cadesplugin.CreateObjectAsync("CADESCOM.CPAttribute");
			var CADESCOM_AUTHENTICATED_ATTRIBUTE_DOCUMENT_NAME = 1;
			yield oDocumentNameAttr.propset_Name(CADESCOM_AUTHENTICATED_ATTRIBUTE_DOCUMENT_NAME);
			yield oDocumentNameAttr.propset_Value("Document Name");
			yield attr.Add(oDocumentNameAttr);

			if (oSigner) {
				yield oSigner.propset_Certificate(certificate);
			}
			else {
				return yield { signature: null, errorString: "Ошибка создания объекта CAdESCOM.CPSigner" };
			}

			//var dataToSign = "AF1nSmg80OdADyoZZVfwX92QFyMovabNEUJ8LLqvTFs=";

			var oSignedData = yield cadesplugin.CreateObjectAsync("CAdESCOM.CadesSignedData");
			var hashAlg = CADESCOM_HASH_ALGORITHM_CP_GOST_3411; // ГОСТ Р 34.11-94
			var sHashValue = hash;
			var oHashedData = yield cadesplugin.CreateObjectAsync("CAdESCOM.HashedData");
			oHashedData.propset_Algorithm = hashAlg;
			oHashedData.SetHashValue(sHashValue);
			var CADES = sTSAAddress ? (sTSAAddress !== "${dsign.tsp.url}" ? 0x5D : 1) : 1;

			if (sHashValue) {
				// Данные на подпись ввели
				//yield oSignedData.propset_Content(dataToSign);
				yield oSigner.propset_Options(1); //CAPICOM_CERTIFICATE_INCLUDE_WHOLE_CHAIN
                if (sTSAAddress) {
                    yield oSigner.propset_TSAAddress(sTSAAddress);
                }
				try {
					//Signature = yield oSignedData.SignCades(oSigner, CADES_BES);
					Signature = yield oSignedData.SignHash(oHashedData, oSigner, CADES);
				}
				catch (err) {
					return yield { signature: null, errorString: ("Не удалось создать подпись из-за ошибки: " + GetErrorMessage(err)) };
				}
			}
		}
		catch(err)
		{
			return yield { signature: null, errorString: ("Не удалось создать подпись из-за ошибки: " + GetErrorMessage(err)) };
		}
        var result = {hash: hash, hashNodeRef: hashNodeRef, signature: Signature};
		return yield { signature: result, errorString: "" };
	});
}

function SignES6Hashes(thumbprint, docs, sTSAAddress, signCallBack, context) {
	var promises = [];
    docs.forEach(function (doc) {
        var promise = SignES6Hash(thumbprint, doc.hash, doc.hashNodeRef, sTSAAddress)
		promises.push(promise);
    });

	var result = [];
	Promise.all(promises).then(
		function( values ) {
            if (docs.length === values.length) {
				for(i = 0; i < values.length; i++) {
					if (!values[i].signature) {
						signCallBack(context, values[i].errorString);
						return;
					}
				}

				var promises2 = [];
				var promise2 = GetCertificateES6(thumbprint)
				promises2.push(promise2);
				Promise.all(promises2).then(
					function( values2 ) {
						signCallBack(context, "", { signResult: values, certInfo: values2[0] });
					}
				);
            } else {
                signCallBack(context, "Количество подписей и документов не совпадает.");
            }
		}
	);
}

function verifySignature (hash, sSignedMessage){
    return cadesplugin.async_spawn(verifyGenerator);
    
    function* verifyGenerator(){
        yield cadesplugin;
        var CADESCOM_HASH_ALGORITHM_CP_GOST_3411 = 100;
        var signers, signer, cert;
        var oHashedData = yield cadesplugin.CreateObjectAsync("CAdESCOM.HashedData");
        oHashedData.Algorithm = CADESCOM_HASH_ALGORITHM_CP_GOST_3411; 
        oHashedData.SetHashValue(hash);

        var oSignedData = yield cadesplugin.CreateObjectAsync("CAdESCOM.CadesSignedData");

        try {
            yield oSignedData.VerifyHash(oHashedData, sSignedMessage);
            signers = yield oSignedData.Signers;
            signer = yield signers.Item(1);
            cert = yield signer.Certificate;
        } catch (err) {
            return {"certificate":cert, "valid": false};
        }

        return {"certificate":cert, "valid": true};
    }
}

function verifySignaturesSync (signs, callback) {
    var promises = signs.map(function (sign) {
        return verifySignature(sign.hash, sign.signedMessage);
    });

    Promise.all(promises).then(function(results) {
        callback(results);
    });
}

function GetCertificateInfo(cert, callback) {
    return cadesplugin.async_spawn(function*() {
        yield cadesplugin;
        var result;

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

        var dateObj = new Date();
        try {
            var Validator = yield cert.IsValid();
            var IsValid = yield Validator.Result;
            var ValidFromDate = new Date((yield cert.ValidFromDate));
            var ValidToDate = new Date((yield cert.ValidToDate));
            var hasPrivateKey = yield cert.HasPrivateKey();

            if(hasPrivateKey) {
                var privateKey = yield cert.PrivateKey;
                var containerName = yield privateKey.ContainerName;
                var providerName = yield privateKey.ProviderName; 
            }
            var version = yield cert.Version;
            var thumbprint = yield cert.Thumbprint;
            var subject = "";
            var subject = yield cert.SubjectName;
            var serialNumber = yield cert.SerialNumber;
            var issuer = yield cert.IssuerName;
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
                isValid: IsValid,
                containerName: containerName,
                providerName: providerName
            };
        }catch (ex) {
            console.log("Ошибка при получении информации из объекта сертификата: " + GetErrorMessage(ex));
        }
        return yield result;
    }).then(function (result){ callback(result); });
}

function GetCertificateES6(thumbprint) {
    return cadesplugin.async_spawn(function*(arg) {
        yield cadesplugin;
		var CADESCOM_HASH_ALGORITHM_CP_GOST_3411 = 100;

		thumbprint = thumbprint.replace(/\s/g, "").toUpperCase();
		try {
			var oStore = yield cadesplugin.CreateObjectAsync("CAdESCOM.Store");
			yield oStore.Open();
		} catch (err) {
			return null;
		}

		var CAPICOM_CERTIFICATE_FIND_SHA1_HASH = 0;
		var all_certs = yield oStore.Certificates;
		var oCerts = yield all_certs.Find(CAPICOM_CERTIFICATE_FIND_SHA1_HASH, thumbprint);

		if ((yield oCerts.Count) == 0) {
			return null;
		}
		var certificate = yield oCerts.Item(1);
        var privateKey = yield certificate.PrivateKey;
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
        return yield {
            version: yield certificate.Version,                       //3
            validToDate: (new Date(yield certificate.ValidToDate)).getTime(),     //Date 2015-08-07T07:48:00.000Z
            validFromDate: (new Date(yield certificate.ValidFromDate)).getTime(), //Date 2016-08-07T07:47:23.000Z
            thumbprint: yield certificate.Thumbprint,                 //"040F78E8E5A619546EAF9AB891174BBAA00093B5"
            subjectName: yield certificate.SubjectName,               //"STREET=У 1, CN=Теле2_Тест_3, SN=Тестов, G=Тест Третий, C=RU, S=77 г. Москва, L=г. М, O=Теле2_Тест_3, T=Директор, OGRN=0067333755082, SNILS=26481117067, INN=009999324755"
            serialNumber: yield certificate.SerialNumber,             //"01D0D0E54C4B5D00000000000379085D"
            issuerName: yield certificate.IssuerName,                 //"CN=ЗАО Калуга Астрал (УЦ 889), O=ЗАО Калуга Астрал, E=ca@astralnalog.ru, S=40 Калужская область, L=Калуга, C=RU, INN=004029017981, OGRN=1024001434049, STREET=Улица Циолковского дом 4"
            isValid: yield certificate.IsValid().Result,              //1
            hasPrivateKey: yield certificate.HasPrivateKey(),          //true
            containerName: yield privateKey.ContainerName,
            providerName: yield privateKey.ProviderName
        };
	});
}


function AboutES6(context) {
	try {
		var oAbout = cadesplugin.CreateObjectAsync("CAdESCOM.About");
		context.aboutCallBack(!!oAbout);
		return;
	} catch(err) {
		console.log(err);
	}
	context.aboutCallBack(false);
}

//async_resolve();
