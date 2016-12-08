(function(){

    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector,
        Bubbling = YAHOO.Bubbling;
    var formId;
    var formButtons;

    Bubbling.on("errandsWFCoexecutorReportScriptLoaded",process);
    Bubbling.on("routeReportButtonClick",submitForm);

    function process(layer, args){
        formId = args[1].formId +"-form";
        setUpForm();
    }

    function setUpForm(){
        var form = Dom.get(formId);
        Dom.addClass(form,"errands-coexecutor-report-form");
        var connectionControl = Selector.query(".control.association-search",form,true);
        var button = Selector.query(".container .buttons-div input",connectionControl, true);
        button.value = Alfresco.util.message("button.create-connection");
        var visibleValueDiv = Selector.query(".container .value-div .control-selected-values.mandatory-highlightable",connectionControl,true);
        Dom.setStyle(visibleValueDiv,"display","none");
        Event.addListener(visibleValueDiv,'DOMSubtreeModified',function(){
            if(!visibleValueDiv.hasChildNodes()){
                Dom.setStyle(visibleValueDiv,"display","none");
            }else{
                Dom.setStyle(visibleValueDiv,"display","block");
            }
        });
        formButtons = Dom.get(formId + "-buttons");
        var saveReportElement = Dom.get(formId + "-submit-button");
        saveReportElement.innerHTML = Alfresco.util.message("button.save-report");

    }


    function submitForm(){
        var routeReport = Selector.query('input[name="prop_lecmErrandWf_coexecutorReportRouteReport"]',Dom.get(formId),true);
        routeReport.value = true;
        var submitButton = Selector.query(".yui-submit-button", formButtons, true);
        submitButton.click();
    }

})();