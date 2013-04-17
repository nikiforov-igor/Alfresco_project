<#assign id = args.htmlid>
<#assign containerId = id + "-container">

<script type="text/javascript">
//<![CDATA[
(function() {
    var container;

    function drawForm(){
        Alfresco.util.Ajax.jsonGet(
                {
                    url:Alfresco.constants.PROXY_URI + "lecm/contracts/summary",
                    successCallback:{
                        fn:function(response){
                            if (container != null) {
                                container.innerHTML = "<br/> ${msg("label.info.totalSum")} " + response.json.totalSum;
                            }
                        }
                    },
                    failureMessage:"message.failure"
                });
    }

    function init() {
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