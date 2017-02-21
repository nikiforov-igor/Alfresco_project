<#escape x as jsonUtils.encodeJSONString(x)!''>
{
	"title": "${organization.getName()}",
	"nodeRef": "${organization.getNodeRef()}",
	"full-name": "${organization.properties["lecm-orgstr:element-full-name"]}",
	"short-name": "${organization.properties["lecm-orgstr:element-short-name"]}",
	"leg-address": "${organization.properties["lecm-orgstr:org-leg-address"]}",
	"act-address": "${organization.properties["lecm-orgstr:org-act-address"]}",
	"off-site": "${organization.properties["lecm-orgstr:org-off-site"]}",
	"phone": "${organization.properties["lecm-orgstr:org-phone"]}",
	"fax": "${organization.properties["lecm-orgstr:org-fax"]}",
	"email": "${organization.properties["lecm-orgstr:org-email"]}",
	"tin": "${organization.properties["lecm-orgstr:org-tin"]}",
	"ownership-type": "${organization.properties["lecm-orgstr:org-ownership-type"]}",
	"incorporation-form": "${organization.properties["lecm-orgstr:org-incorporation-form"]}",
	"founding-docs": "${organization.properties["lecm-orgstr:org-founding-docs"]}",
	"logo": "${logo!""}",
	"boss": "${boss!""}"
}
</#escape>