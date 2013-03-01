<#if item??>
    <div class="document-header">
        <div class="document-info">
            <h1 class="thin dark">
                ${documentName}
            </h1>
        </div>

        <div class="clear"></div>
    </div>
<#else>
    <div class="document-header">
        <div class="status-banner">
            ${msg("banner.not-found")}
        </div>
    </div>
</#if>