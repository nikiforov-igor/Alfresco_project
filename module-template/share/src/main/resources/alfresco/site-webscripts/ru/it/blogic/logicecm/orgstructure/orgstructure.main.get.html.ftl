<br/>
<div id="orgstructure"></div>
<div id="orgstructure_tree"></div>

<script type="text/javascript">//<![CDATA[

(function() {

    function init() {
        var orgStructure = new LogicECM.module.OrgStructure("orgstructure");
        orgStructure.setMessages(${messages});
        orgStructure.draw();
    }

    YAHOO.util.Event.onDOMReady(init);

})();
//]]></script>
