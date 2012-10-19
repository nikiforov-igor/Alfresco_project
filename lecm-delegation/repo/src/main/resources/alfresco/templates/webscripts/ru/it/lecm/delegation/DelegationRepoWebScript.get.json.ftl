<#escape x as x?js_string>
{
	id: "${model.id}",
	name: "${model.name}",
	title: "${model.title}",
	date: "${model.date?datetime}"
}
</#escape>