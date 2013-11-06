var employees = orgstructure.getEmployeesDirectory();
var result = [];
if (employees != null) {
    for each (var employee in employees.children) {
        var firstName = employee.properties["lecm-orgstr:employee-first-name"];
        var middleName = employee.properties["lecm-orgstr:employee-middle-name"];
        var lastName = employee.properties["lecm-orgstr:employee-last-name"];
        var login = orgstructure.getEmployeeLogin(employee);
        var roles = orgstructure.getEmployeeBusinessRoles(employee.nodeRef.toString());
        var delegate = orgstructure.getEmployeeDelegatedBusinessRoles(employee.nodeRef.toString());

        var resultRoles =[];
        for each (var role in roles) {
            resultRoles.push({
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
            "roles": resultRoles,
            "delegateRoles": resultDelegates
        });
    }
}

model.result = result;