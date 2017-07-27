(function() {
    model.node = edsGlobalSettings.getSettingsNode();
    model.isRegCenralized = !!edsGlobalSettings.isRegistrationCenralized();
    model.isAllowSigningOnPaper = edsGlobalSettings.isAllowSigningOnPaper();
    model.isHideProps = documentGlobalSettings.isHidePropsForRecipients();
}());