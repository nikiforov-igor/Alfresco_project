var url = "/lecm/statemachine/editor/datagrid/status?nodeRef=" + args["nodeRef"];
var json = remote.connect("alfresco").get(url);
var columns = [];
if (json.status == 200) {
    var statuses = eval("(" + json + ")");
    columns.push({
        "type": "property",
        "name": "form_field",
        "formsName": "form_field",
        "label": msg.get("label.properties_states"),
        "dataType": "text",
        "sortable": false
    });

    for each (var status in statuses.data) {
        columns.push({
            "type": "property",
            "name": status.name,
            "formsName": status.name,
            "label": status.name,
            "dataType": "checkboxtable",
            "sortable": false
        });
    }
}
model.columns = columns;

