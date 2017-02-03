(function() {
    model.node = edsGlobalSettings.getSettingsNode();
    model.isRegCenralized = edsGlobalSettings.isRegistrationCenralized() ? edsGlobalSettings.isRegistrationCenralized() : false;
    model.isHideProps = documentGlobalSettings.isHidePropsForRecipients();
}());