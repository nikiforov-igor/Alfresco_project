<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-errands/document-errands.css" group='resources'/>
<@inlineScript group='resources'>
	if (typeof LogicECM == 'undefined' || !LogicECM) {
		LogicECM = {};
	}
	LogicECM.module = LogicECM.module || {};
	LogicECM.module.Documents = LogicECM.module.Documents|| {};
	LogicECM.module.Documents.ERRANDS_SETTINGS = LogicECM.module.Documents.ERRANDS_SETTINGS || <#if errandsSettings?? >${errandsSettings?string}<#else>{}</#if>;
</@>
