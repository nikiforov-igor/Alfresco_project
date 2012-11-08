<#escape x as jsonUtils.encodeJSONString(x)>
{
	"totalRecords": 1,
	"startIndex": 0,
	"metadata": {
		"parent": {
			"nodeRef": "${parent}",
			"permissions": {
				"userAccess": {
					"create": true
				}
			}
		}
	},
	"items": [{
			"nodeRef": "${result}",
			"itemData": {
				"testUnit": "${result}",
				"prop_lecm-dlg_testUnitId": {
					"value": "${result2.properties["lecm-dlg:testUnitId"]}",
					"displayValue": "${result2.properties["lecm-dlg:testUnitId"]}"
				},
				"prop_lecm-dlg_testUnitName": {
					"value": "${result2.properties["lecm-dlg:testUnitName"]}",
					"displayValue": "${result2.properties["lecm-dlg:testUnitName"]}"
				},
				"prop_lecm-dlg_testUnitTitle": {
					"value": "${result2.properties["lecm-dlg:testUnitTitle"]}",
					"displayValue": "${result2.properties["lecm-dlg:testUnitTitle"]}"
				},
				"prop_lecm-dlg_testUnitDate": {
					"value": "${result2.properties["lecm-dlg:testUnitDate"]?datetime}",
					"displayValue": "${result2.properties["lecm-dlg:testUnitDate"]?datetime}"
				},
				"nodeRef": "${result2}"
			}
		}
	]
}
</#escape>
