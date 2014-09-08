function main() {
	var delegationGlobalSettings = {
		name : "LogicECM.DelegationGlobalSettings"
	};

	model.widgets = [delegationGlobalSettings];
    model.allowEdit = user.id === "admin"
}

main();