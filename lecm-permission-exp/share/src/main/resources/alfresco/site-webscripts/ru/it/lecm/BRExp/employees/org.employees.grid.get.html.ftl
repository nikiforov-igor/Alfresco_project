<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="orgstructure-employees-grid">
	<div id="yui-main-2">
		<div class="yui-b" id="alf-content" style="margin-left: 0;">
			<!-- include base datagrid markup-->
		<@grid.datagrid id=id showViewForm=true showArchiveCheckBox=true>
			<script type="text/javascript">//<![CDATA[
			function createDatagrid() {

                YAHOO.lang.augmentObject(LogicECM.module.Base.DataGrid.prototype, {
                                        
                                        onShowRolesAction : function loadRoles(items) {
                                            var value = items.nodeRef;
                                           
                                            Alfresco.util.Ajax.request(
                                            {
                                                method: "GET",
                                                url:Alfresco.constants.PROXY_URI + 'lecm/BRExplanation/roles' + '?nodeRef=' + value,
                                                requestContentType: "text/html",
                                                responseContentType: "text/html",
                                                successCallback:{
                                                    fn:function setData(response) {
                                                        Alfresco.util.PopupManager.displayPrompt(
		                                        {
                                                            title:"${msg("title.rolesInfo")}",
                                                            modal:true,
		                                            text:response.serverResponse.responseText,
                                                            noEscape:true
		                                        });
                                                    }
                                                }
                                            });
                                        },

		                	makeJquerySyncRequestForAbsence : function _makeJquerySyncRequestForAbsence(url, payload, showMessage, comment ){
		                        var result = {};

		                        result.hasNoActiveAbsences = false;

		                        // Yahoo UI не умеет синхронный (блокирующий) AJAX. Придется использовать jQuery
		                        jQuery.ajax({
		                            url: Alfresco.constants.PROXY_URI_RELATIVE + url,
		                            type: "POST",
		                            timeout: 30000, // 30 секунд таймаута хватит всем!
		                            async: false, // ничего не делаем, пока не отработал запром
		                            dataType: "json",
		                            contentType: "application/json",
		                            data: YAHOO.lang.JSON.stringify(payload), // jQuery странно кодирует данные. пусть YUI эаймеся преобразованием в JSON
		                            processData: false, // данные не трогать, не кодировать вообще
		                            success: function (response, textStatus, jqXHR) {
		                                if (response && response.hasNoActiveAbsences) {
		                                    result.hasNoActiveAbsences = true;
		                                } else {
		                                    result.hasNoActiveAbsences = false;
		                                    result.reason = response.reason;
		                                }
		                            },
		                            error: function(jqXHR, textStatus, errorThrown) {
		                                result.hasNoActiveAbsences = false;
		                                result.errorText = textStatus;
		                            }
		                        });

		                        if (showMessage){
		                            if (result.errorText){
		                                Alfresco.util.PopupManager.displayMessage(
		                                        {
		                                            text:result.errorText
		                                        });
		                            }else{
		                                if ( !result.hasNoActiveAbsences && result.reason){
		                                    Alfresco.util.PopupManager.displayMessage(
		                                            {
		                                                text:  comment
		                                            });
		                                }
		                            }
		                        }

		                        return result;
		                    },


                        },
                    true
                );

				var datagrid = new LogicECM.module.Base.DataGrid('${id}').setOptions(
						{
							usePagination:true,
							showExtendSearchBlock:true,
                            showActionColumn: LogicECM.module.OrgStructure.IS_ENGINEER ? true : false,
							actions: [
								{
									type:"datagrid-action-link-${bubblingLabel!"employee"}",
									id:"onShowRolesAction",
									permission:"edit",
									label:"${msg("actions.rolesInfo")}"
								}
							],
							bubblingLabel: "${bubblingLabel!"employee"}",
							showCheckboxColumn: false,
							attributeForShow:"lecm-orgstr:employee-last-name"
						}).setMessages(${messages});

                YAHOO.util.Event.onContentReady ('${id}', function () {
                    YAHOO.Bubbling.fire ("activeGridChanged", {
                        datagridMeta: {
                            itemType: LogicECM.module.OrgStructure.EMPLOYEES_SETTINGS.itemType,
                            nodeRef: LogicECM.module.OrgStructure.EMPLOYEES_SETTINGS.nodeRef,
                            actionsConfig:{
                                fullDelete:LogicECM.module.OrgStructure.EMPLOYEES_SETTINGS.fullDelete
                            }
                        },
                        bubblingLabel: "${bubblingLabel!"employee"}"
                    });
                });
			}

			function init() {
				createDatagrid();
			}

			YAHOO.util.Event.onDOMReady(init);
			//]]></script>
		</@grid.datagrid>
		</div>
	</div>
</div>

