<div id="orgstructure-menu">
    <br/>
    <div id="button1" class="yui-skin-sam"></div><br/>
    <div id="button2" class="yui-skin-sam"></div><br/>
</div>

<script type="text/javascript">//<![CDATA[

(function() {
    function init() {
        var menu = new LogicECM.module.OrgStructure.Menu("orgstructure-menu");
        menu.setMessages(${messages});
        menu._loadRoots();
    }
    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>
