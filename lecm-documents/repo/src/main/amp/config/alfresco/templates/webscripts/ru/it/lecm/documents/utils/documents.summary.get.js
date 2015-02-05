var type = args["docType"];
var archive = ("" + args["archive"] == "true");
var skippedStatuses = args["skippedStatuses"];
var considerFilter = args["considerFilter"] ? args["considerFilter"]: null;

var paths = [];

var map = documentScript.getFilters(type, archive);

var types = [];
types.push(type);

var list = [];
var members = [];

for (var key in map) {
    var amountDocs = documentScript.getAmountDocuments(types, paths, map[key].split(","), considerFilter);
    list.push({
        key: key,
        skip: skippedStatuses != null && skippedStatuses.indexOf(key) >= 0,
        amount: amountDocs,
        filter: map[key]
    });
}
if (list.length == 0) { //добавляем пункт Все, если у нас не заданы фильтры
    var amountDocs = documentScript.getAmountDocuments(types, paths, ["*"], considerFilter);
    list.push({
        key: "msg.all",
        skip: false,
        amount: amountDocs,
        filter: "*"
    });
}

var amountMembers = documentScript.getAmountMembers(type);
members.push({
    key: "msg.participants",
    amountMembers: amountMembers
});
model.members = members;
model.list = list;