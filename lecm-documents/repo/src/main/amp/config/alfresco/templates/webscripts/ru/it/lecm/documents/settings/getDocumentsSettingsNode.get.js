(function() {
    var settingsNode = documentGlobalSettings.getSettingsNode();
    var linksViewMode = '' + documentGlobalSettings.getLinksViewMode();
    
    model.settingsNodeRef = settingsNode ? settingsNode.nodeRef.toString() : '';
    model.isHideProps = documentGlobalSettings.isHidePropsForRecipients();
    model.linksViewMode = linksViewMode;
}());