<#assign pageId = page.id />
<div id="${pageId}-summary-table-wrapper"></div>

<script type="text/javascript">//<![CDATA[
(function () {
    YAHOO.Bubbling.on("dataItemsDeleted", drawSummaryTable);
    YAHOO.Bubbling.on("dataItemCreated", drawSummaryTable);

    drawSummaryTable();

    function drawSummaryTable() {
        Alfresco.util.Ajax.request({
            method: "GET",
            url: Alfresco.constants.URL_PAGECONTEXT + "ru/it/lecm/wcalendar/absence/absence-summary-table",
            requestContentType: "text/html",
            responseContentType: "text/html",
            successCallback: {
                fn: function (response) {
                    var result = response.serverResponse;
                    if (result != null) {
                        var wrapperNode = YAHOO.util.DDM.getElement("${pageId}-summary-table-wrapper");
                        if (wrapperNode != null) {
                            wrapperNode.innerHTML = result.responseText;
                        }
                    }
                },
                scope: this
            }
        });
    }
})();

//]]>
</script>
