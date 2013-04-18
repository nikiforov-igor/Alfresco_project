<#assign id = args.htmlid>
<#assign containerId = id + "-container">

<script type="text/javascript">
    //<![CDATA[
    (function() {
        function init() {
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
    <div class="body scrollableList dashlet-body" id="${id}_results" <#if args.height??>style="height: ${args.height}px;"</#if>>
        Здесь будут задачи...
    </div>
</div>