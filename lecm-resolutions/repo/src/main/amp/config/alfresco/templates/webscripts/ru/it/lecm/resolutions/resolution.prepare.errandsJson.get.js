<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">

var fromJson = false;
var resolutionRef = args["nodeRef"];
if (resolutionRef && resolutionRef.search("SpacesStore") != -1) {
    var resolution = search.findNode(resolutionRef);
    if (resolution) {
        var status = resolution.properties["lecm-statemachine:status"];
        if (status != "На исполнении" && status != "Аннулировано" && status != "Завершено") {
            fromJson = true;

            var items = [];
            var jsonValue = resolution.properties["lecm-resolutions:errands-json"];
            if (jsonValue && jsonValue.length) {
                var i = 1;
                eval(jsonValue).forEach(function (errandJson) {
                    errandJson["prop_lecm-errands_child-index-counter"] = i++;
                    errandJson["prop_lecm-errands_limitation-date-text"] = edsDocument.getComplexDateText(
                        errandJson["prop_lecm-errands_limitation-date-radio"],
                        errandJson["prop_lecm-errands_limitation-date"] ? errandJson["prop_lecm-errands_limitation-date"] : null,
                        errandJson["prop_lecm-errands_limitation-date-type"] ? errandJson["prop_lecm-errands_limitation-date-type"] : null,
                        errandJson["prop_lecm-errands_limitation-date-days"] ? errandJson["prop_lecm-errands_limitation-date-days"] : null);

                    var fields = Object.keys(errandJson);
                    var itemData = {};
                    fields.forEach(function (field) {
                        var isAssoc = field.indexOf("assoc_") == 0;
                        var value = errandJson[field];
                        var data = [];
                        if (isAssoc) {
                            if (value && value.length) {
                                var values = value.split(",");
                                values.forEach(function (val) {
                                    var obj = Evaluator.getContentObject(val);
                                    if (obj) {
                                        data.push({
                                            value: val,
                                            displayValue: substitude.getObjectDescription(obj)
                                        })
                                    }
                                });
                            }
                        } else {
                            var realFieldName = field.replace("prop_", "").replace("assoc_", "").replace("_", ":");
                            data.push({
                                value: value == "true" ? true : value == "false" ? false : value,
                                displayValue: Evaluator.translateField(base.getProperty(realFieldName), value)
                            })
                        }

                        if (data.length == 1) {
                            itemData[field] = data[0];
                        } else {
                            itemData[field] = data;
                        }

                    });
                    items.push({
                        nodeData: itemData
                    });
                });
            }
            model.items = items;
        }
    }
}
model.fromJson = fromJson;

