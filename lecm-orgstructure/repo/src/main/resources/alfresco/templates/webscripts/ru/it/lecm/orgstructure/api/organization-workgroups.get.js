var results = [];

var active = args["onlyActive"];

var workgroups = orgstructure.getWorkGroups(active != null && active == "true");

for (var index in workgroups) {
    var wg = workgroups[index];
    results.push(wg);
}

model.workgroups = results;