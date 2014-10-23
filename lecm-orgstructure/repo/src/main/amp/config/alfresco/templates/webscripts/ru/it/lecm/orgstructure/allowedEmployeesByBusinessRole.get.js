var businessRoleId = args['businessRoleId'];
var withDelegation = ('true' == args['withDelegation']);
model.employees = orgstructure.getEmployeesByBusinessRoleId(businessRoleId, withDelegation);
