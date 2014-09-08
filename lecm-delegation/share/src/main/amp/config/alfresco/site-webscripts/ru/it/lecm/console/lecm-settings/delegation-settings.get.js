function main() {
	var delegationGlobalSettings = {
		name : "LogicECM.DelegationGlobalSettings"
	};

	model.widgets = [delegationGlobalSettings];
    model.allowEdit = user.isAdmin
}

main();