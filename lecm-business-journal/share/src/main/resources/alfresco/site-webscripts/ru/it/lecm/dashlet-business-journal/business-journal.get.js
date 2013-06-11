<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    model.name = "Business Journal";
	model.hasAccess = hasRole("BR_BUSINESS_JOURNAL_ENGENEER");
}

main();