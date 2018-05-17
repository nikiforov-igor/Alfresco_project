var node = contracts.getDashletSettings();

model.armCode = node.properties["lecm-contract-ext:dashlet-settings-code"];
model.armPath = node.properties["lecm-contract-ext:dashlet-settings-path"];

model.currentContractsPath = node.properties["lecm-contract-ext:current-contracts-path"];
model.projectContractsPath = node.properties["lecm-contract-ext:project-contracts-path"];
model.executionContractsPath = node.properties["lecm-contract-ext:execution-contracts-path"];