<div class="yui-main">
    <br/>
    <div id="contracts"></div>
</div>
<div id="workflowForm"></div>

<script type="text/javascript">//<![CDATA[

var workflowForm;
(function() {
    function init() {
        var contracts = new LogicECM.module.Contracts("contracts");
        contracts.setMessages(${messages});
        contracts.draw();
        workflowForm = new LogicECM.module.StartWorkflow("workflowForm");
    }
    YAHOO.util.Event.onDOMReady(init);
})();
//]]>
</script>