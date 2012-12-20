//получение delegator-а из параметров url-а
//если delegator передан то передача его в ftl-ку
//если не передан то дергаем наш скрипт и получаем delegator-а через пользователя системы
//TODO надо воткнуть какую-то проверку на то, что сотрудник является пользователем системы
var delegator = page.url.args["delegator"];
if (delegator && delegator.length > 0) {
	//проверям права на технолога или руководителя,
	//чтобы мы могли понять можно нам смотреть чужую страницу или нет
	model.delegator = delegator;
} else {
	//дергание скрипта и получение настоящего делегатора
	var jsonStr = remote.connect ("alfresco").get ("/lecm/delegation/get/options/person");
	var obj = jsonUtils.toObject (jsonStr);
	model.delegator = obj.delegationOpts;
}
