var businessRoleRef = json.get("businessRoleRef"),
    withDelegation = json.get("withDelegation");

model.employees = orgstructure.getEmployeesByBusinessRole(businessRoleRef, withDelegation.booleanValue());