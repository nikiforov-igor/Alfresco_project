function findValueByDotNotation(obj, propertyPath, defaultValue) {
    var value = defaultValue ? defaultValue : null;
    if (propertyPath && obj && obj != "") {
        var currObj = obj;
        var props = propertyPath.split(".");
        for (var i = 0; i < props.length; i++) {
            currObj = currObj[props[i]];
            if (typeof currObj == "undefined") {
                return value;
            }
        }
        return currObj;
    }
    return value;
}

var settings = remote.connect("alfresco").get("/lecm/reports-editor/settings");
if (settings.status == 200) {
    model.settings = settings;
}
if (page.url.args.reportId) {
    var setStr = remote.connect("alfresco").get("/lecm/reports-editor/getReportSettings?reportId=" + page.url.args.reportId);
    if (setStr.status == 200) {
        model.reportSettings = setStr;
    }
}

var PREFERENCE_RE_STATE = "ru.it.lecm.reports-editor.state";
var prefStr = remote.connect("alfresco").get("/api/people/" + encodeURIComponent(user.id) + "/preferences?pf=" + PREFERENCE_RE_STATE);
if (prefStr.status == 200) {
    var stateStr = findValueByDotNotation(eval("(" + prefStr +")"), PREFERENCE_RE_STATE, {});
    model.preferences = eval("(" + stateStr +")");
}
