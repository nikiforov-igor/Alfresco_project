var isEngineer = sheduleBase.isEngineer(orgstructure.getCurrentEmployee().nodeRef.toString());
var isBoss = sheduleBase.isBoss(orgstructure.getCurrentEmployee().nodeRef.toString());
model.isEngineer = isEngineer;
model.isBoss = isBoss;
