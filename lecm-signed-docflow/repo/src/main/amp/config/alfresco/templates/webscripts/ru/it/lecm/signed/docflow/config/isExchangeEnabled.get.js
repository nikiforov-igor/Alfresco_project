(function () {
    var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    var signedDocflowService = ctx.getBean("signedDocflowService");
    model.result = signedDocflowService.isDsignExchangeEnabled();
})();
