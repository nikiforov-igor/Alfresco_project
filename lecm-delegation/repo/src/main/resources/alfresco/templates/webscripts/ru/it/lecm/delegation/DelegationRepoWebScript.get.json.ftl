<#escape x as jsonUtils.encodeJSONString(x)>
{
	id: "${model.id}",
	name: "${model.name}",
	title: "${model.title}",
	date: "${model.date?datetime}"
}
</#escape>