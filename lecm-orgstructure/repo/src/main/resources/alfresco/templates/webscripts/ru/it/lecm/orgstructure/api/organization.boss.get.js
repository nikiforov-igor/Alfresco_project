var boss = orgstructure.getOrganizationBoss();
model.bossRef = boss != null ? boss.getNodeRef().toString() : "";
model.boss = boss != null ? boss.getName().toString() : "";
