<#assign id = args.htmlid?js_string>
<div class="metadata-form">
    <div class="lecm-dashlet-actions">
        <a id="${id}-action-collapse" class="collapse" title="${msg("btn.collapse")}"></a>
    </div>
</div>
<div class="list-container">

    <div class="body scrollableList" id="${id}_results">

        <div id="${id}_myErrandsList"></div>

        <div id="${id}_errandsIssuedByMeList"></div>

    </div>

</div>