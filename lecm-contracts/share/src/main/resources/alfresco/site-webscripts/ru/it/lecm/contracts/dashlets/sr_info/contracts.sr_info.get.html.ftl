<#assign id = args.htmlid>
<#assign containerId = id + "-container">

<div class="dashlet contracts bordered">
    <div class="title dashlet-title">
        <span>${msg("label.title")}</span>
    </div>
    <div class="body scrollableList dashlet-body" id="${id}_results">
        Здесь будет НСИ...
    </div>
</div>