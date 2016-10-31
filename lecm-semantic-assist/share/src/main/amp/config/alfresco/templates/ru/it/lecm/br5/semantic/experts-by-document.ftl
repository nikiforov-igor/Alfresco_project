<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />

<@templateHeader>
	<#include "/org/alfresco/components/form/form.get.head.ftl"/>
</@>


<script type="text/javascript">//<![CDATA[
    function generateThumbnailUrl(ref, view) {
        if (ref != null && ref != undefined && ref.length > 0) {
            var nodeRef = new Alfresco.util.NodeRef(ref);
            if (!view) {
                return Alfresco.constants.PROXY_URI + "api/node/" + nodeRef.uri + "/content/thumbnails/doclib?c=force&ph=true";
            } else {
                return Alfresco.constants.PROXY_URI + "api/node/" + nodeRef.uri + "/content";
            }
        } else {
            return "";
        }
    }
    function getFoto(fotoID) {
        var imageContainer = YAHOO.util.Dom.get(fotoID);
        var imgRef = generateThumbnailUrl(fotoID, false);
        if (imgRef != "") {
            var imageId = 'foto_'+fotoID;
            imageContainer.innerHTML = '<span class="thumbnail">' + '<a href="' + generateThumbnailUrl(fotoID, true) +'" target="_blank"><img id="' + imageId + '" src="' + imgRef + '" /></a></span>';
        }
    }
//]]></script>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<#assign el="expert-profile"/>

<@bpage.basePage showToolbar=false>
	<div style="float:left; width: 1000px; padding:10px;">
	<#if  dataExperts??>
		<#assign keys = dataExperts?keys>
		<#list keys?sort?reverse as key>
					<#list dataExperts[key] as mapAttrs>
						<div>
							<table>
							<tr>
								<td>
									<#if mapAttrs["fotoRef"] != "">
										<#assign foto_container_id = mapAttrs['fotoRef']>
											<div id="${foto_container_id}">
														${mapAttrs["fotoRef"]}
											</div>
											<script type="text/javascript">//<![CDATA[
													YAHOO.util.Event.onContentReady('${foto_container_id}', function(){getFoto('${foto_container_id}');}, true);
											//]]></script>
									</#if>
								</td>
								<td style="vertical-align: top; padding: 0 0 0 10px">
									<#assign fio = mapAttrs["lastName"] +" "+ mapAttrs["firstName"] +" "+ mapAttrs["middleName"]>
									<p><a href="javascript:void(0);" onclick="LogicECM.module.Base.Util.viewAttributes({itemId:'${mapAttrs["expertRef"]}', title: 'logicecm.employee.view'})">${fio}</a></p>
									<#if mapAttrs["staf"] != "">
										<p>Должность: ${mapAttrs["staf"]}</p>
									</#if>
								</td>

							<tr>
							</table>
						</div>
						<#if mapAttrs_has_next> <br/> </#if>
					</#list>
		<#if key_has_next> <br/> </#if>
		</#list>
	</#if>

	 </div>
</@bpage.basePage>