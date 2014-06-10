var employees = orgstructure.getEmployeesDirectory();
var result = [];
if (employees != null) {
    for each (var employee in employees.children) {
        var firstName = employee.properties["lecm-orgstr:employee-first-name"];
        var middleName = employee.properties["lecm-orgstr:employee-middle-name"];
        var lastName = employee.properties["lecm-orgstr:employee-last-name"];
        var login = orgstructure.getEmployeeLogin(employee);

        var direct = orgstructure.getEmployeeDirectRoles(employee.nodeRef.toString());
        var unit = orgstructure.getEmployeeUnitRoles(employee.nodeRef.toString());
        var workgroup = orgstructure.getEmployeeWGRoles(employee.nodeRef.toString());
        var position = orgstructure.getEmployeeDPRoles(employee.nodeRef.toString());
        var delegate = orgstructure.getEmployeeDelegatedBusinessRoles(employee.nodeRef.toString());

        var directRoles =[];
        for each (var role in direct) {
            directRoles.push({
                "roleName": role.properties["cm:name"],
                "roleCode": role.properties["lecm-orgstr:business-role-identifier"]
            });
        }

        var unitRoles =[];
        for each (var role in unit) {
            unitRoles.push({
                "roleName": role.properties["cm:name"],
                "roleCode": role.properties["lecm-orgstr:business-role-identifier"]
            });
        }

        var workgroupRoles =[];
        for each (var role in workgroup) {
            workgroupRoles.push({
                "roleName": role.properties["cm:name"],
                "roleCode": role.properties["lecm-orgstr:business-role-identifier"]
            });
        }

        var positionRoles =[];
        for each (var role in position) {
            positionRoles.push({
                "roleName": role.properties["cm:name"],
                "roleCode": role.properties["lecm-orgstr:business-role-identifier"]
            });
        }

        var resultDelegates =[];
        for each (var role in delegate) {
            resultDelegates.push({
                "roleName": role.properties["cm:name"],
                "roleCode": role.properties["lecm-orgstr:business-role-identifier"]
            });
        }
        result.push({
            "firstName": firstName,
            "middleName": middleName,
            "lastName": lastName,
            "login": login,
            "direct": directRoles,
            "unit": unitRoles,
            "workgroup": workgroupRoles,
            "position": positionRoles,
            "delegateRoles": resultDelegates
        });
    }
}

model.result = result;