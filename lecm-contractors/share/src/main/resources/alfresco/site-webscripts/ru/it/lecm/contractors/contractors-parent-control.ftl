<#assign currentContractorRef = args.itemId/>

<#import "/ru/it/lecm/base-share/components/view.lib.ftl" as viewContractorParentForm/>
<#assign viewFormId = "view-contractor-parent"/>
<@viewContractorParentForm.viewForm formId = viewFormId/>

<div class="form-field">
    <div class="viewmode-field">
        <span class="viewmode-label">Материнская компания:</span>
        <span id="zxcv-view-button" class="viewmode-value"><button onclick="testViewParentForm();">Попробовать</button></span>
    </div>
</div>

<script type="text/javascript">//<![CDATA[
    function testViewParentForm() {
        Alfresco.util.Ajax.request({
            method: "POST",
            url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/contractors/getparent",
            dataObj: { "childContractor": "${currentContractorRef}" },
            requestContentType: "application/json",
            responseContentType: "application/json",
            successCallback: {
                fn: function (response) {

                    debugger;

                    var url = "components/form" +
                            "?itemKind={itemKind}" +
                            "&itemId={itemId}" +
                            "&mode={mode}" +
                            "&setId={setId}" +
                            "&showCancelButton=true";

                    console.log("The [response.parentContractor] value was: " + response.json.parentContractor);

                    if(response.json.parentContractor.length > 0) {
//                        var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + url, {
//                            itemKind: "node",
//                            itemId: encodeURIComponent(response.json.parentContractor),
//                            mode: "view",
//                            setId: "common"
//                        });
//
//                        //var putFormIn = YAHOO.util.Dom.get("view-contractor-parent-form");
//                        var viewParentContractorForm = new Alfresco.module.SimpleDialog("view-contractor-parent");
//
//                        viewParentContractorForm.setOptions({
//                            width: "500px",
//                            templateUrl: templateUrl,
//                            modal: true,
//                            destroyOnHide: true
//                        });
//
//                        viewParentContractorForm.show();

                        viewAttributes(response.json.parentContractor);

                    } else {
                        window.alert("[response.json.parentContractor.length] === 0");
                    }
                },
                scope: window.document
            },
            failureCallback: {
                fn: function (response) {

                },
                scope: this
            }
        });
    }
//]]>
</script>