<#assign fieldValue=field.value!"">

<#if fieldValue?string != "">
<div class="control view-file-link viewmode">
    <div class="label-div">
        <label>${field.label?html}:</label>
    </div>
    <div class="container">
        <div class="value-div">
            <span><a href="${url.context}/page/document-attachment?nodeRef=${fieldValue}" target="_blank">
                <img src="${url.context}/components/images/filetypes/img-file-16.png" width="16"/>${msg("lable.view.file")}
            </a></span>
        </div>
    </div>
</div>
<div class="clear"></div>
</#if>
