<div class="yui-main">
    <br/>
    <div id="contracts"></div>
</div>

<script type="text/javascript">//<![CDATA[

(function() {
    function init() {
        var contracts = new LogicECM.module.Contracts("contracts");
        contracts.setMessages(${messages});
        contracts.draw();
    }
    YAHOO.util.Event.onDOMReady(init);
})();
//]]>
</script>