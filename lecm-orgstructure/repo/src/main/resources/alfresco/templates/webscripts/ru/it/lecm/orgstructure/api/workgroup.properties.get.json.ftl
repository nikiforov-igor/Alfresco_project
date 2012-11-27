<#escape x as x?js_string>
	{
		fullName: "${workgroup.properties["lecm-orgstr:element-full-name"]}",
		shortName: "${workgroup.properties["lecm-orgstr:element-short-name"]}",
		description: "${workgroup.properties["lecm-orgstr:workGroup-description"]}",
		active: "${workgroup.properties["lecm-dic:active"]?string}"
	}
</#escape>