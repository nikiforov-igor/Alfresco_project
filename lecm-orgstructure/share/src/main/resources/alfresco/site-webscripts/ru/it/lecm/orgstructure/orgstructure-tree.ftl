<script type="text/javascript">//<![CDATA[
(function() {
    function init() {
        var orgStructure = new LogicECM.module.OrgStructure.Tree("orgstructure-tree");
        orgStructure.setMessages(${messages});
        orgStructure.draw();
    }
    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div id="${id}-body" class="datalists">
	<br/>
	<div id="orgstructure-tree" class="ygtv-highlight"></div>
</div>
