var isEngineer = absenceBase.isEngineer(orgstructure.getCurrentEmployee().nodeRef.toString());
var isBoss = absenceBase.isBoss(orgstructure.getCurrentEmployee().nodeRef.toString());
model.isEngineer = isEngineer;
model.isBoss = isBoss;
