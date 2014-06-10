var businessRoleRef = json.get("businessRoleId"),
    withDelegation = json.get("withDelegation");

model.employees = orgstructure.getEmployeesByBusinessRoleId(businessRoleRef, withDelegation.booleanValue());