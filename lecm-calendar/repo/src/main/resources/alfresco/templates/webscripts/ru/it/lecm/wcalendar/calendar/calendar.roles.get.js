var isEngineer = calendarBase.isEngineer(orgstructure.getCurrentEmployee().nodeRef.toString());
var isBoss = calendarBase.isBoss(orgstructure.getCurrentEmployee().nodeRef.toString());
model.isEngineer = isEngineer;
model.isBoss = isBoss;
