(function() {
    model.node = edsGlobalSettings.getSettingsNode();
    model.isRegCenralized = !!edsGlobalSettings.isRegistrationCenralized();
    model.isHideProps = documentGlobalSettings.isHidePropsForRecipients();
}());