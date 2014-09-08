function main() {
	var ErrandsDashletSettings = {
		name : "LogicECM.ErrandsDashletSettings"
	};

	model.widgets = [ErrandsDashletSettings];
    model.allowEdit = user.id === "admin"
}

main();