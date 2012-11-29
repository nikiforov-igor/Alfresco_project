var results = [];
var active = args["onlyActive"] != null && args["onlyActive"] == "true";

var subUnits = orgstructure.getSubUnits(args["nodeRef"], active);
model.subUnits = subUnits;