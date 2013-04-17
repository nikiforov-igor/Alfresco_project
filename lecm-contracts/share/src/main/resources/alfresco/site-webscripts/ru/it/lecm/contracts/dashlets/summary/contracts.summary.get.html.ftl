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
                                for (var obj in response.json) {
                                    var item = response.json[obj];
                                    var div = createRow();
                                    var detail = document.createElement('span');
                                    detail.innerHTML = item.record;
                                    detail.setAttribute('class', 'detail');
                                    div.appendChild(detail);
                                    div.innerHTML = message[obj] +" "+ response.json[obj];
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
            "allContracts": "${msg("label.info.allContracts")}",
            "inDevelopment": "${msg("label.info.contractsToDevelop")}",
            "isActive": "${msg("label.info.activeContracts")}",
            "isInactive": "${msg("label.info.inactiveContracts")}"
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
    <div class="body scrollableList dashlet-body" id="${id}_results">
    </div>
</div>