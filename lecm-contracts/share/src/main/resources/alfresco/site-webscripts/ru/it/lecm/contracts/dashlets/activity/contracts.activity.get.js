<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

var DICT_CODE = "LECM_CONTRACT";

function getRecords(code) {
    var url = '/lecm/dictionary/api/getDictionaryByCode?code=' + code;
    var result = remote.connect("alfresco").get(url);

    if (result.status != 200) {
        AlfrescoUtil.error(result.status, 'Could not get dictionary for code ' + code);
        return;
    }
    var nodeRef = eval('(' + result + ')').nodeRef;

    if (nodeRef != null) {
        var url = '/lecm/business-journal/api/search?type=' + nodeRef + "&days=30&whose=&checkMainObject=true";
        var result = remote.connect("alfresco").get(url);
        if (result.status != 200) {
            AlfrescoUtil.error(result.status, 'Could not get records for node ' + nodeRef);
        }
        return eval('(' + result + ')');
    }
}

function main() {
    model.records = jsonUtils.toJSONString(getRecords(DICT_CODE));
}

main();
