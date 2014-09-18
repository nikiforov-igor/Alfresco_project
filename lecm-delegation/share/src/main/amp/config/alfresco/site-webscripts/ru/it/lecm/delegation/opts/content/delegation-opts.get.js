//получение delegator-а из параметров url-а
//если delegator передан то передача его в ftl-ку
//если не передан то дергаем наш скрипт и получаем delegator-а через пользователя системы
//TODO надо воткнуть какую-то проверку на то, что сотрудник является пользователем системы
var delegator = page.url.args["delegator"];
var pageId = page.id;
var jsonStr;
var obj;
if ("delegation-opts" == pageId && delegator && delegator.length > 0) {
	//проверям права на технолога или руководителя,
	//чтобы мы могли понять можно нам смотреть чужую страницу или нет
	model.delegator = delegator;
	//дергание скрипта и получение инфы о том активно ли делегирование
	jsonStr = remote.connect ("alfresco").get ("/lecm/delegation/is/active?nodeRef=" + model.delegator);
	obj = jsonUtils.toObject (jsonStr);
	model.isActive = obj.isActive;
    model.myProfile = false;
} else {
	//дергание скрипта и получение настоящего делегатора
	jsonStr = remote.connect ("alfresco").get ("/lecm/delegation/get/options");
	obj = jsonUtils.toObject (jsonStr);
	model.delegator = obj.delegationOpts;
	model.isActive = obj.isActive;
    model.myProfile = true;
}
//актуализируем список доверенностей у делегатора (сотрудник, пользователь, параметры делегирования)
remote.connect ("alfresco").get ("/lecm/delegation/get/procuracies?nodeRef=" + model.delegator);
