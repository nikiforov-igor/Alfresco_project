var node = resolutionsScript.getDashletSettings();

if (node) {
    model.armCode = node.properties["lecm-resolutions-settings:dashlet-settings-arm"];
    model.armGeneralPath = node.properties["lecm-resolutions-settings:dashlet-settings-path"];
}