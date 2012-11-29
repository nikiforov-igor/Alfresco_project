var organization = orgstructure.getOrganization();
var boss = orgstructure.getOrganizationBoss();
var logo =  orgstructure.getOrganizationLogo();

model.boss = boss != null ? boss.getNodeRef().toString() : "";
model.logo = logo != null ? logo.getNodeRef().toString() : "";
model.organization = organization;