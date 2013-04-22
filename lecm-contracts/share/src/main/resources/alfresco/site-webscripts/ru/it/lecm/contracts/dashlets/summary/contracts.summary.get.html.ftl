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
                                if (response.json != null) {
                                    var list = response.json.list;
                                    var members = response.json.members;
                                    var innerHtml,div;
                                    for (var index in list) {
                                        innerHtml = message[list[index].key] +" "+
                                                "<a class=\"status-button text-cropped\" href=\"/share/page/contracts-list?query=" +
                                                list[index].filter +"\">" +list[index].amountContracts + "</a>";
                                        div = createRow(innerHtml);
                                        container.appendChild(div);
                                    }
                                    innerHtml = message[members.key] +" "+
                                                "<a class=\"status-button text-cropped\">" +members.amountMembers + "</a>";
                                    div = createRow(innerHtml);
                                    container.appendChild(div);

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
    }
    YAHOO.util.Event.onDOMReady(init);
})();
//]]>
</script>

<div class="dashlet contracts bordered">
    <div class="title dashlet-title">
        <span>${msg("label.title")}</span>
    </div>
    <div class="body scrollableList dashlet-body" id="${id}_results">
    </div>
</div>