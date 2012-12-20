var active = args["onlyActive"] != null && args["onlyActive"] == "true";
var workgroups = orgstructure.getWorkGroups(active);

model.workgroups = workgroups;