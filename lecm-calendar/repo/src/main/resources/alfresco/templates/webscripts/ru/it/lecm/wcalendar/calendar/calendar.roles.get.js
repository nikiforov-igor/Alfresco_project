var isEngineer = orgstructure.isCalendarEngineer(orgstructure.getCurrentEmployee().nodeRef.toString());
var isBoss = orgstructure.isBoss(orgstructure.getCurrentEmployee().nodeRef.toString());
model.isEngineer = isEngineer;
model.isBoss = isBoss;
