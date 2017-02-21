<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    "versionable": false,
    "totalRecords": 1,
    "startIndex": 0,
    "metadata": {
        "permissions": {
            "userAccess": {
                "create": true
            }
        }
    },
    "items":
    [
    <#list result as items>
        {
            "nodeRef": "",
            "createdOn": "",
            "createdBy": {
                "value": "",
                "displayValue": ""
            },
            "modifiedOn": "",
            "modifiedBy": {
                "value": "",
                "displayValue": ""
            },
            "tags": [],
            "permissions": {
                "userAccess": {
                    "delete": false,
                    "edit": false,
                    "create": false
                }
            },
            "itemData": {
            <#list items as field>
                "${field.fieldName}": {
                    "value": "${field.value}",
                    "displayValue": "${field.displayValue?string}"
                } <#if field_has_next>,</#if>
            </#list>
            }
        }
        <#if items_has_next>,</#if>
    </#list>
    ]
}
</#escape>