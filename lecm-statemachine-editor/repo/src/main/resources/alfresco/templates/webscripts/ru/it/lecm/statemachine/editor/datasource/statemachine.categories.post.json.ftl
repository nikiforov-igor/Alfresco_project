<#escape x as x?js_string>
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
            "actionSet": "",
            "tags": [],
            "permissions": {
                "userAccess": {
                    "delete": false,
                    "edit": false,
                    "create": false
                }
            },
            "actionLabels": {
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