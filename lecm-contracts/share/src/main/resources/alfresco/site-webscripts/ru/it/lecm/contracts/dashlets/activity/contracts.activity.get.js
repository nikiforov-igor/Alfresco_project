<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

var TYPE_CODES = ["LECM_CONTRACT", "LECM_CONTRACT_ADDITIONAL_DOCUMENT"]; // добавить здесь другие коды при необходимости

function getRecords(codes) {
    var refs = "";

    for each (code in codes) {
        var url = '/lecm/dictionary/api/getDictionaryByCode?code=' + code;
        var result = remote.connect("alfresco").get(url);

        if (result.status == 200) {
            if (refs != "") {
                refs += ",";
            }
            refs += eval('(' + result + ')').nodeRef;
        }
    }
	return refs;
    /*if (refs != "") {
        var url = '/lecm/business-journal/api/search?type=' + refs + "&days=30&whose=&checkMainObject=true";
        var result = remote.connect("alfresco").get(url);
        if (result.status != 200) {
            AlfrescoUtil.error(result.status, 'Could not get records for the types: ' + refs);
            return [];
        }
        return eval('(' + result + ')');
    }*/
}

function main() {
    //model.records = jsonUtils.toJSONString(getRecords(TYPE_CODES));
    model.refs = getRecords(TYPE_CODES);
}

main();
