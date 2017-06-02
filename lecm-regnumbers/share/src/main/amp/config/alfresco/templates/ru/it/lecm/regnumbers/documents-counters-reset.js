(function () {
    model.isEngineer = user.isAdmin;

    var countersContainer = remote.connect("alfresco").get("/lecm/regnumbers/counters/getContainer");
    if (countersContainer.status == 200) {
        model.countersContainer = countersContainer;
    }
})();
