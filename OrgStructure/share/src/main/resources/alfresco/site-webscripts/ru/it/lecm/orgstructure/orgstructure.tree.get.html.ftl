<div class="yui-main">
    <br/>
    <div id="orgstructure-tree" class="ygtv-highlight"></div>
</div>
<hr/>
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
