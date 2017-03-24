function main() {
    var ResolutionsDashletSettings = {
        name : "LogicECM.ResolutionsDashletSettings"
    };

    model.widgets = [ResolutionsDashletSettings];
    model.allowEdit = user.isAdmin
}

main();