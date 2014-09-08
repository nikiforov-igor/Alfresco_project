function main() {
	var ContractDashletSettings = {
		name : "LogicECM.ContractDashletSettings"
	};

	model.widgets = [ContractDashletSettings];
    model.allowEdit = user.id === "admin"
}

main();