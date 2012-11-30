//получение delegator-а из параметров url-а
//если delegator передан то передача его в ftl-ку
//если не передан то дергаем наш скрипт и получаем delegator-а через пользователя системы
var delegator = page.url.args["delegator"];
if (typeof(delegator) == "string" && delegator.length > 0) {
	//проверям права на технолога или руководителя,
	//чтобы мы могли понять можно нам смотреть чужую страницу или нет
	model.delegator = delegator;
} else {
	//дергание скрипта и получение настоящего делегатора
	model.delegator = "test";
}
