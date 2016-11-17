(function () {
    var document = search.findNode(args['nodeRef']);
    model.employees = routesService.getEmployeesOfAllDocumentRoutes(document);
})();



