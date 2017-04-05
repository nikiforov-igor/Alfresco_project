(function () {
    var node = search.findNode(args['nodeRef']);
    var currentEmployee = orgstructure.getCurrentEmployee();
    model.user = {};
    model.document = {};
    model.user.nodeRef = currentEmployee.nodeRef.toString();
    model.user.roles = [];
    var isBR_INITIATOR = lecmPermission.hasEmployeeDynamicRole(node, currentEmployee, "BR_INITIATOR");
    if (isBR_INITIATOR) {
        model.user.roles.push("BR_INITIATOR");
    }
    var isDA_REGISTRAR_DYN = lecmPermission.hasEmployeeDynamicRole(node, currentEmployee, "DA_REGISTRAR_DYN");
    if (isDA_REGISTRAR_DYN) {
        model.user.roles.push("DA_REGISTRAR_DYN")
    }
    var isDA_REGISTRARS = orgstructure.hasBusinessRole(currentEmployee, "DA_REGISTRARS");
    if (isDA_REGISTRARS) {
        model.user.roles.push("DA_REGISTRARS")
    }
    var isController = false;
    var controllerAssoc = node.assocs["lecm-ord:controller-assoc"];
    if (controllerAssoc && controllerAssoc.length) {
        var controller = controllerAssoc[0];
        if (controller.equals(currentEmployee)){
            isController = true;
        }
    }
    var isErrandsCreated = false;
    var tableAssocs = node.assocs["lecm-ord-table-structure:items-assoc"];
    if (tableAssocs && tableAssocs.length) {
        var table = tableAssocs[0];
        var pointAssocs = table.childAssocs["cm:contains"];
        if (pointAssocs && pointAssocs.length) {
            isErrandsCreated = pointAssocs.some(function (point) {
                var errandsAssoc = point.assocs["lecm-ord-table-structure:errand-assoc"];
                return errandsAssoc && errandsAssoc.length;
            });
        }
    }
    model.user.isController = isController;
    model.document.status = node.properties["lecm-statemachine:status"];
    model.document.isErrandsCreated = isErrandsCreated;

})();