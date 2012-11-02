<#escape x as x?js_string>
{
	"totalRecords": 1,
	"startIndex": 0,
	"metadata": {
		"parent": {
			"nodeRef": "${result}",
			"permissions": {
				"userAccess": {
					"create": true
				}
			}
		}
	},
	"items": [{
			"testUnit": "${result}",
			"id": "${result2.properties["lecm-dlg:testUnitId"]}",
			"name": "${result2.properties["lecm-dlg:testUnitName"]}",
			"title": "${result2.properties["lecm-dlg:testUnitTitle"]}",
			"date": "${result2.properties["lecm-dlg:testUnitDate"]?datetime}",
			"nodeRef": "${result2}"
		}
	]
}
</#escape>
