<#assign id = args.htmlid?js_string>
<#assign ownerRef = args.owner?string>
<div id="${id}_delegation-content">
    <div class="dashlet bordered delegation-content">
        <div class="title dashlet-title">
            <span>${msg("label.owner-delegation.dashlet.title")}</span>
        </div>
        <div class="body dashlet-body" id="${id}_results">
            <div id="main-region" class="yui-g">
                <div id="${id}_owner-content" class="yui-u first delegation-owner-content">
                    <div id="${id}_owner-foto" class="owner-foto thumbnail-container">
                    <#if imgRef?? && imageId??>
                        <span class="thumbnail-view">
                            <a href="${imgRef}" target="_blank"><img id="${imageId}" src="${imgRef}"/></a>
                        </span>
                    <#else>
                        <span class="thumbnail-view-text">${msg('message.upload.not-loaded')}</span>
                    </#if>
                    </div>
                    <div id="${id}_owner-positions" class="owner-employee-positions">
                        <br/>
                    <#if fio??>
                        <p class="owner-fio">${fio}</p>
                    </#if>
                    <#if positionsObjects?exists && positionsObjects?size &gt; 0>
                        <#list positionsObjects as position>
                            <p class="owner-position">
                            ${position.position},
                            ${position.department}</p>
                        </#list>
                    </#if>
                    </div>
                </div>
                <div class="delegation-info yui-u">
                    <div id="${id}_container">
                        <br/>
                    <#if secretaryText??>
                        <p>${secretaryText}</p>
                    </#if>
                    <#if delegationText??>
                        <p>${delegationText}</p>
                    </#if>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<p class="delegation-page-title">
${msg("label.delegation.message.advanced-info")}
</p>