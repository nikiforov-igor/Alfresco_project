<#assign id = args.htmlid>
<#assign containerId = id + "-container">

<script type="text/javascript">
//<![CDATA[
(function() {
    var container;
    var message;
    var createRow = function(innerHtml) {
        var div = document.createElement('div');
        div.setAttribute('class', 'row');
        if (innerHtml) {
            div.innerHTML = innerHtml;
        }
        return div;
    };
    function drawForm(){
        Alfresco.util.Ajax.jsonGet(
                {
                    url:Alfresco.constants.PROXY_URI + "lecm/contracts/summary",
                    successCallback:{
                        fn:function(response){
                            if (container != null) {
                                container.innerHTML = '';
                                var oResults = eval("(" + response.serverResponse.responseText + ")");
                                if (oResults != null) {
                                    for (var index in oResults) {
                                        var item = oResults[index].amountContracts;
                                        var div = createRow();
                                        var detail = document.createElement('span');
                                        detail.innerHTML = item.record;
                                        detail.setAttribute('class', 'detail');
                                        div.appendChild(detail);
                                        div.innerHTML = message[oResults[index].key] +" "+
                                                "<a class=\"status-button text-cropped\" href=\"/share/page/contracts-list?query=" +
                                                oResults[index].filter +"\">" +oResults[index].amountContracts + "</a>";
                                        container.appendChild(div);
                                    }
                                }
                            }
                        }
                    },
                    failureMessage:"message.failure"
                });
    }

    function init() {
        message =  {
            "Все": "${msg("label.info.allContracts")}",
            "В разработке": "${msg("label.info.contractsToDevelop")}",
            "Активные": "${msg("label.info.activeContracts")}",
            "Неактивные": "${msg("label.info.inactiveContracts")}",
            "participants": "${msg("label.info.participants")}"
        };
        container = Dom.get('${id}_results');
        drawForm();
        new Alfresco.widget.DashletResizer("${id}", "${instance.object.id}");
    }
    YAHOO.util.Event.onDOMReady(init);
})();
//]]>
</script>

<div class="dashlet contracts bordered">
    <div class="title dashlet-title">
        <span>${msg("label.title")}</span>
    </div>
    <div class="body scrollableList dashlet-body" id="${id}_results" <#if args.height??>style="height: ${args.height}px;"</#if>>
    </div>
</div>