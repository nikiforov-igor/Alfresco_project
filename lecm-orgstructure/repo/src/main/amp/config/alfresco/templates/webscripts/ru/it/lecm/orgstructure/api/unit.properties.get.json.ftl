<#escape x as x?js_string>
	{
		fullName: "${unit.properties["lecm-orgstr:element-full-name"]}",
		shortName: "${unit.properties["lecm-orgstr:element-short-name"]}",
		code: "${unit.properties["lecm-orgstr:unit-code"]}",
		type: "${unit.properties["lecm-orgstr:unit-type"]}",
		active: "${unit.properties["lecm-dic:active"]?string}"
	}
</#escape>