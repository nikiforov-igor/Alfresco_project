var organization = companyhome.childByNamePath("Организация");
var bossAssoc = organization.assocs["lecm-orgstr:org-boss-assoc"];
if(bossAssoc) {
    var boss = bossAssoc[0].nodeRef.toString();
    model.boss = boss;
}
var logoAssoc = organization.assocs["lecm-orgstr:org-logo-assoc"];
if (logoAssoc) {
    var logo = logoAssoc[0].nodeRef.toString();
    model.logo = logo;
}
model.organization = organization;