var active = (args["onlyActive"] != null && args["onlyActive"] == "true");
model.positions = orgstructure.getStaffPositions(active);
