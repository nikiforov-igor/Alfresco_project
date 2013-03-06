<#assign id = args.htmlid?js_string>

<script type="text/javascript">
    //<![CDATA[
    (function() {
        var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event,
            Selector = YAHOO.util.Selector;
        var container;

        function init() {
            new Alfresco.widget.DashletTitleBarActions("${id}").setOptions({
                actions: []
            });

            container = Dom.get('${id}_results');
            container.innerHTML = 'Участники будут здесь';
        }
        Event.onDOMReady(init);
    })();
    //]]>
</script>

<div class="dashlet document">
    <div class="title">${msg("label.title")}
        <span class="lecm-dashlet-actions">
            <a id="${id}-action-expand" href="javascript:void(0);" onclick="documentMembersComponent.onExpand()" class="expand" title="${msg("dashlet.expand.tooltip")}">&nbsp</a>
         </span>
    </div>
    <div class="body scrollableList" id="${id}_results"></div>
</div>