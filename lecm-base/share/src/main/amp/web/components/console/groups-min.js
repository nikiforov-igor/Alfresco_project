(function(){var f=YAHOO.util.Dom,j=YAHOO.util.Event,a=YAHOO.util.Element;var d=Alfresco.util.encodeHTML;Alfresco.ConsoleGroups=function(A){this.name="Alfresco.ConsoleGroups";Alfresco.ConsoleGroups.superclass.constructor.call(this,A);Alfresco.util.ComponentManager.register(this);Alfresco.util.YUILoaderHelper.require(["button","container","datasource","datatable","json","history","columnbrowser"],this.onComponentsLoaded,this);YAHOO.Bubbling.on("newGroup",this.onNewGroup,this);YAHOO.Bubbling.on("updateGroup",this.onUpdateGroup,this);var y=this;this.panelHandlers={};SearchPanelHandler=function ae(){SearchPanelHandler.superclass.constructor.call(this,"search")};YAHOO.extend(SearchPanelHandler,Alfresco.ConsolePanelHandler,{_visible:false,isSearching:false,_selectedParentGroupShortName:null,sortBy:"displayName",maxItems:250,onLoad:function C(){var at=this;var ap=new YAHOO.widget.Button(y.id+"-search-button",{});ap.on("click",this.onSearchClick,ap,this);this.widgets.searchButton=ap;this.widgets.columnbrowser=new YAHOO.extension.ColumnBrowser(y.id+"-columnbrowser",{numVisible:3,rootUrl:Alfresco.constants.PROXY_URI+"api/groups?sortBy="+this.sortBy+(f.get(y.id+"-show-all").checked?"":"&zone=APP.DEFAULT")+((y.query&&y.query!=null)?"&shortNameFilter="+encodeURIComponent(y.query):""),pagination:{rowsPerPage:y.options.maxPageSize,rowsPerPageParam:"maxItems",recordOffsetParam:"skipCount",firstPageLinkLabel:y._msg("tinyPagination.firstPageLinkLabel"),lastPageLinkLabel:y._msg("tinyPagination.lastPageLinkLabel"),previousPageLinkLabel:y._msg("tinyPagination.previousPageLinkLabel"),nextPageLinkLabel:y._msg("tinyPagination.nextPageLinkLabel"),pageReportTemplate:y._msg("tinyPagination.pageReportTemplate"),template:y._msg("tinyPagination.template")},columnInfoBuilder:{fn:this.onBuildColumnInfo,scope:this}});this.widgets.breadcrumb=new YAHOO.extension.ColumnBrowserBreadCrumb(y.id+"-breadcrumb",{columnBrowser:this.widgets.columnbrowser,root:y._msg("label.breadcrumb.root")});var ar=new YAHOO.widget.Button(y.id+"-browse-button",{});ar.on("click",this.onBrowseClick,ar,this);var aq=f.get(y.id+"-show-all");j.addListener(aq,"change",function(){var aw={showAll:aq.checked,refresh:this.getParameterValueFromUrl("refresh")};y.refreshUIState(aw)},null,this);this.widgets.dataSource=new YAHOO.util.DataSource(Alfresco.constants.PROXY_URI+"api/groups?",{responseType:YAHOO.util.DataSource.TYPE_JSON,responseSchema:{resultsList:"data",metaFields:{recordOffset:"startIndex",totalRecords:"totalRecords"}}});this.widgets.dataSource.doBeforeParseData=function av(az,ay){var ax=ay;if(ay){var aw=ay.data;ax={data:aw}}if(aw.length==0){at._setResultsMessage("message.noresults")}else{if(aw.length<y.options.maxSearchResults){at._setResultsMessage("message.results",d(y.query),aw.length)}else{at._setResultsMessage("message.maxresults",y.options.maxSearchResults)}}return ax};this._setupDataTable();this.widgets.dataTable.subscribe("theadCellClickEvent",function(az){var aw=null;for(var ax=0,ay=y.panels.length;ax<ay;ax++){if(y.panels[ax].id==="search"){aw=y.panels[ax];break}}if(aw!=null){aw.sortBy=this.getColumn(az.target).key}});var au=f.get(y.id+"-search-text");new YAHOO.util.KeyListener(au,{keys:YAHOO.util.KeyListener.KEY.ENTER},{fn:function(){this.onSearchClick()},scope:this,correctScope:true},"keydown").enable();Alfresco.util.Ajax.request({url:Alfresco.constants.URL_SERVICECONTEXT+"components/people-finder/people-finder",dataObj:{htmlid:y.id+"-search-peoplefinder"},successCallback:{fn:this.onPeopleFinderLoaded,scope:this},failureMessage:"Could not load People Finder component",execScripts:true});Alfresco.util.Ajax.request({url:Alfresco.constants.URL_SERVICECONTEXT+"components/people-finder/group-finder",dataObj:{htmlid:y.id+"-search-groupfinder"},successCallback:{fn:this.onGroupFinderLoaded,scope:this},failureMessage:"Could not load Group Finder component",execScripts:true});this.widgets.deleteGroupPanel=new Alfresco.util.createYUIPanel(y.id+"-deletegroupdialog",{visible:false});this.widgets.deleteGroupCancelButton=new YAHOO.widget.Button(y.id+"-cancel-button",{});this.widgets.deleteGroupCancelButton.on("click",function(){this.widgets.deleteGroupPanel.hide()},null,this);this.widgets.deleteGroupOkButton=Alfresco.util.createYUIButton(y,"remove-button",null)},getParameterValueFromUrl:function ag(av){var ar,ap=null,at=window.location.hash,au=at.replace("#","").split("&");for(var aq=0;aq<au.length;aq++){ar=au[aq].split("=");if(ar[0]===av){ap=ar[1];break}}return ap},onShow:function w(){this._visible=true;f.get(y.id+"-search-text").focus();var ap=f.get(y.id+"-show-all");ap.checked=y.showAll},onUpdate:function ao(){if(y.refresh==undefined||y.query!==undefined){f.addClass(y.id+"-browse-panel","hidden");f.removeClass(y.id+"-search-panel","hidden");if(y.query!==undefined){var ap=f.get(y.id+"-search-text");ap.value=y.query;this.doSearch()}}else{f.addClass(y.id+"-search-panel","hidden");f.removeClass(y.id+"-browse-panel","hidden");var aq=this.widgets.columnbrowser.get("urlPath");if(!aq||aq.length==0){this.widgets.columnbrowser.load()}else{if(y.refresh){this.widgets.columnbrowser.load(aq,true)}}}},onHide:function F(){this._visible=false},onSearchClick:function r(){var ap=YAHOO.lang.trim(f.get(y.id+"-search-text").value);if(ap.replace(/\*/g,"").length<y.options.minSearchTermLength){Alfresco.util.PopupManager.displayMessage({text:y._msg("message.minimum-length",y.options.minSearchTermLength)});return}y.refreshUIState({query:ap})},onBrowseClick:function B(){y.refreshUIState({query:undefined,refresh:"false"});var aq=f.get(y.id+"-show-all").checked;var ar=f.get(y.id+"-search-text").value;var ap=Alfresco.constants.PROXY_URI+"api/groups?sortBy="+this.sortBy+(aq?"":"&zone=APP.DEFAULT")+((ar&&ar!=null)?"&shortNameFilter="+encodeURIComponent(ar):"");y.panelHandlers.searchPanelHandler.widgets.columnbrowser.set("rootUrl",ap);y.panelHandlers.searchPanelHandler.widgets.columnbrowser.load([ap],true)},onConfirmedDeleteGroupClick:function ad(aq,ap){this.widgets.deleteGroupPanel.hide();if(ap.multiParentMode&&f.get(y.id+"-remove").checked){this._removeGroup(ap.fullName,ap.parentShortName,ap.displayName)}else{this._deleteGroup(ap.shortName,ap.displayName)}},onGroupSelected:function O(ar,aq){if(this._visible){var ap=aq[1].displayName;this.widgets.addGroupPanel.hide();this._addToGroup(aq[1].itemName,this._selectedParentGroupShortName,y._msg("message.addgroup-success",ap),y._msg("message.addgroup-failure",ap))}},onPersonSelected:function v(ar,aq){if(this._visible){var ap=aq[1].firstName+" "+aq[1].lastName;this.widgets.addUserPanel.hide();this._addToGroup(aq[1].userName,this._selectedParentGroupShortName,y._msg("message.adduser-success",ap),y._msg("message.adduser-failure",ap))}},onNewGroupClick:function Q(ap){YAHOO.Bubbling.fire("newGroup",{group:ap.parent?ap.parent.shortName:undefined,groupDisplayName:ap.parent?ap.parent.label:y._msg("label.theroot")})},onAddGroupClick:function T(ap){this._selectedParentGroupShortName=ap.parent.shortName;this.modules.searchGroupFinder.clearResults();this.widgets.addGroupPanel.show()},onAddUserClick:function aj(ap){this._selectedParentGroupShortName=ap.parent.shortName;this.modules.searchPeopleFinder.clearResults();this.widgets.addUserPanel.show()},onDeleteClick:function ai(ap){this._confirmDeleteGroup(ap.itemInfo.shortName,ap.itemInfo.fullName,ap.itemInfo.label,ap.columnInfo.parent?ap.columnInfo.parent.shortName:null,ap.columnInfo.parent?ap.columnInfo.parent.label:y._msg("label.theroot"))},onUserRemoveClick:function am(ap){this._confirmRemoveUser(ap.columnInfo.parent.shortName,ap.itemInfo.shortName,ap.itemInfo.label)},onUpdateClick:function ah(ap){YAHOO.Bubbling.fire("updateGroup",{group:ap.itemInfo.shortName,groupDisplayName:ap.itemInfo.label})},onPeopleFinderLoaded:function t(aq){var ap=f.get(y.id+"-search-peoplefinder");ap.innerHTML=aq.serverResponse.responseText;this.widgets.addUserPanel=Alfresco.util.createYUIPanel(y.id+"-peoplepicker");this.modules.searchPeopleFinder=Alfresco.util.ComponentManager.get(y.id+"-search-peoplefinder");this.modules.searchPeopleFinder.setOptions({singleSelectMode:true});YAHOO.Bubbling.on("personSelected",this.onPersonSelected,this)},onGroupFinderLoaded:function z(aq){var ap=f.get(y.id+"-search-groupfinder");ap.innerHTML=aq.serverResponse.responseText;this.widgets.addGroupPanel=Alfresco.util.createYUIPanel(y.id+"-grouppicker");this.modules.searchGroupFinder=Alfresco.util.ComponentManager.get(y.id+"-search-groupfinder");this.modules.searchGroupFinder.setOptions({singleSelectMode:true});YAHOO.Bubbling.on("itemSelected",this.onGroupSelected,this)},onBuildColumnInfo:function s(aB,aq){var ay=[];if(!aq||aq.cssClass=="groups-item-group"){ay.push({title:(aq?y._msg("button.newsubgroup"):y._msg("button.newgroup")),cssClass:"groups-newgroup-button",click:{fn:this.onNewGroupClick,scope:this}})}if(aq&&aq.cssClass=="groups-item-group"){ay.push({title:y._msg("button.addgroup"),cssClass:"groups-addgroup-button",click:{fn:this.onAddGroupClick,scope:this}});ay.push({title:y._msg("button.adduser"),cssClass:"groups-adduser-button",click:{fn:this.onAddUserClick,scope:this}})}var at={parent:aq,header:{buttons:ay},body:{items:[]}};var aw={};if(aB){aw=YAHOO.lang.JSON.parse(aB.responseText);if(aw.paging){at.pagination={totalRecords:aw.paging.totalItems,recordOffset:aw.paging.skipCount}}}var au={title:y._msg("button.updategroup"),cssClass:"groups-update-button",click:{fn:this.onUpdateClick,scope:this}};var ax={title:y._msg("button.deletegroup"),cssClass:"groups-delete-button",click:{fn:this.onDeleteClick,scope:this}};var az={title:y._msg("button.deletegroup"),cssClass:"groups-delete-button-disabled",click:{fn:function(){return false},scope:this}};var aA=[];aA.push(au);aA.push(ax);var ap=[];ap.push(au);ap.push(az);var aD=[{title:y._msg("button.removeuser"),cssClass:"users-remove-button",click:{fn:this.onUserRemoveClick,scope:this}}];for(var av=0;aw.data&&av<aw.data.length;av++){var ar=aw.data[av];var aC=ar.displayName;if(ar.displayName!==ar.shortName){aC+=" ("+ar.shortName+")"}var aE={shortName:ar.shortName,fullName:ar.fullName,url:ar.authorityType=="GROUP"?Alfresco.constants.PROXY_URI+ar.url+"/children?sortBy="+this.sortBy:null,hasNext:ar.groupCount>0||ar.userCount>0,label:aC,next:null,cssClass:ar.authorityType=="GROUP"?"groups-item-group":"groups-item-user",buttons:ar.authorityType=="GROUP"?(Alfresco.util.arrayContains(ar.zones,"APP.SHARE")?ap:aA):aD};at.body.items.push(aE)}return at},doSearch:function L(){if(!this.isSearching&&y.query!==undefined&&y.query.length>=y.options.minSearchTermLength){this.isSearching=true;var ar=this;ar._setDefaultDataTableErrors(ar.widgets.dataTable);ar.widgets.dataTable.set("MSG_EMPTY",y._msg("message.searching"));ar.widgets.dataTable.deleteRows(0,ar.widgets.dataTable.getRecordSet().getLength());var ap=function at(av,aw,ax){ar._enableSearchUI();ar._setDefaultDataTableErrors(ar.widgets.dataTable);ar.widgets.dataTable.onDataReturnInitializeTable.call(ar.widgets.dataTable,av,aw,ax)};var aq=function au(av,aw){ar._enableSearchUI();if(aw.status==401){window.location.reload()}else{try{ar.widgets.dataTable.set("MSG_ERROR",y._msg("message.noresults.short"));ar.widgets.dataTable.showTableMessage(y._msg("message.noresults.short"),YAHOO.widget.DataTable.CLASS_ERROR);ar._setResultsMessage("message.noresults")}catch(ax){ar._setDefaultDataTableErrors(ar.widgets.dataTable)}}};ar.widgets.dataSource.sendRequest(ar._buildSearchParams(y.query),{success:ap,failure:aq,scope:y});ar._setResultsMessage("message.searchingFor",d(y.query));ar.widgets.searchButton.set("disabled",true);YAHOO.lang.later(2000,ar,function(){if(ar.isSearching){if(!ar.widgets.feedbackMessage){ar.widgets.feedbackMessage=Alfresco.util.PopupManager.displayMessage({text:Alfresco.util.message("message.searching",y.name),spanClass:"wait",displayTime:0})}else{if(!ar.widgets.feedbackMessage.cfg.getProperty("visible")){ar.widgets.feedbackMessage.show()}}}},[])}},_enableSearchUI:function ac(){if(this.widgets.feedbackMessage&&this.widgets.feedbackMessage.cfg.getProperty("visible")){this.widgets.feedbackMessage.hide()}this.widgets.searchButton.set("disabled",false);this.isSearching=false},_confirmDeleteGroup:function K(ap,au,aq,at,av){var ar=this;y.getParentGroups(ap,{fn:function(aw){this.widgets.deleteGroupOkButton.removeListener("click",this.onConfirmedDeleteGroupClick);var ay={shortName:ap,fullName:au,displayName:aq,parentShortName:at,parentDisplayName:av};if(!aw||aw.length==0){f.addClass(y.id+"-multiparent","hidden");f.removeClass(y.id+"-singleparent","hidden");f.get(y.id+"-singleparent-message").innerHTML=y._msg("panel.deletegroup.singleparentmessage",d(aq));this.widgets.deleteGroupOkButton.on("click",this.onConfirmedDeleteGroupClick,ay,this)}else{f.addClass(y.id+"-singleparent","hidden");f.removeClass(y.id+"-multiparent","hidden");f.get(y.id+"-multiparent-message").innerHTML=y._msg("panel.deletegroup.oneormultiparentmessage",d(aq));f.get(y.id+"-remove-message").innerHTML=y._msg("panel.deletegroup.removemessage",d(aq),d(av));f.get(y.id+"-delete-message").innerHTML=y._msg("panel.deletegroup.deletemessage",d(aq));f.get(y.id+"-searchdelete-message").innerHTML=y._msg("panel.deletegroup.searchdeletemessage",d(aq));var az="",aA=10;for(var ax=0;ax<aw.length&&ax<aA;ax++){az+=aw[ax].displayName+(ax<aw.length-1?", ":"")}if(ax>=aA){az+=y._msg("label.moregroups",aw.length-aA)}f.get(y.id+"-parents").innerHTML=d(az);if(at){f.get(y.id+"-remove").checked=true;f.removeClass(y.id+"-removerow","hidden");f.removeClass(y.id+"-deleterow","hidden");f.addClass(y.id+"-searchdeleterow","hidden")}else{f.get(y.id+"-delete").checked=true;f.addClass(y.id+"-removerow","hidden");f.addClass(y.id+"-deleterow","hidden");f.removeClass(y.id+"-searchdeleterow","hidden")}ay.multiParentMode=true;this.widgets.deleteGroupOkButton.on("click",this.onConfirmedDeleteGroupClick,ay,this)}this.widgets.deleteGroupPanel.show()},scope:this},"message.delete-failure")},_confirmRemoveUser:function x(at,ar,av){var au=this;Alfresco.util.PopupManager.displayPrompt({title:y._msg("message.confirm.removeuser.title"),text:y._msg("message.confirm.removeuser",av),buttons:[{text:y._msg("button.yes"),handler:function ap(){this.destroy();au._removeUser.call(au,at,ar,av)}},{text:y._msg("button.no"),handler:function aq(){this.destroy()},isDefault:true}]})},_deleteGroup:function D(ap,aq){var ar=Alfresco.constants.PROXY_URI+"api/groups/"+encodeURIComponent(ap.replace(/%/g,"%25"));this._doDeleteCall(ar,aq)},_removeGroup:function aa(at,ar,ap){if(ar==null){Alfresco.util.PopupManager.displayPrompt({title:y._msg("message.failure"),text:y._msg("message.noRemoveGroupFromRootSupport")});return}var aq=Alfresco.constants.PROXY_URI+"api/groups/"+encodeURIComponent(ar.replace(/%/g,"%25"))+"/children/"+encodeURIComponent(at);this._doDeleteCall(aq,ap)},_doDeleteCall:function ak(ar,aq){var ap=aq;Alfresco.util.Ajax.request({method:Alfresco.util.Ajax.DELETE,url:ar,successCallback:{fn:function(au){var at=this.widgets.columnbrowser.get("urlPath");this.widgets.columnbrowser.load(at,true);this.doSearch();Alfresco.util.PopupManager.displayMessage({text:y._msg("message.delete-success",ap)})},scope:this},failureMessage:y._msg("message.delete-failure",ap)})},_removeUser:function J(ar,aq,at){var ap=at;Alfresco.util.Ajax.request({method:Alfresco.util.Ajax.DELETE,url:Alfresco.constants.PROXY_URI+"api/groups/"+encodeURIComponent(ar.replace(/%/g,"%25"))+"/children/"+encodeURIComponent(aq),successCallback:{fn:function(av){var au=this.widgets.columnbrowser.get("urlPath");this.widgets.columnbrowser.load(au,true);Alfresco.util.PopupManager.displayMessage({text:y._msg("message.removeuser-success",ap)})},scope:this},failureMessage:y._msg("message.removeuser-failure",ap)})},_addToGroup:function M(ap,at,aq,ar){Alfresco.util.Ajax.jsonPost({url:Alfresco.constants.PROXY_URI+"api/groups/"+encodeURIComponent(at.replace(/%/g,"%25"))+"/children/"+encodeURIComponent(ap),successCallback:{fn:function(av){var au=this.widgets.columnbrowser.get("urlPath");this.widgets.columnbrowser.load(au,true);Alfresco.util.PopupManager.displayMessage({text:aq})},scope:this},failureMessage:ar})},_setupDataTable:function E(){var ap=this;var at=function at(av,au,aw,ax){av.innerHTML=d(ax)};var aq=function aq(ax,aw,ay,aA){var au=document.createElement("a");f.addClass(au,"update");au.innerHTML="&nbsp;";YAHOO.util.Event.addListener(au,"click",function(aB){YAHOO.Bubbling.fire("updateGroup",{group:aw.getData("shortName"),groupDisplayName:aw.getData("displayName"),query:this.query})},null,y);ax.appendChild(au);if(Alfresco.util.arrayContains(aw.getData("zones"),"APP.SHARE")){var az=document.createElement("a");f.addClass(az,"delete-disabled");az.innerHTML="&nbsp;";ax.appendChild(az)}else{var av=document.createElement("a");f.addClass(av,"delete");av.innerHTML="&nbsp;";YAHOO.util.Event.addListener(av,"click",function(aB){ap._confirmDeleteGroup(aw.getData("shortName"),null,aw.getData("displayName"),null,null)});ax.appendChild(av)}};var ar=[{key:"shortName",label:y._msg("label.shortname"),sortable:true,formatter:at},{key:"displayName",label:y._msg("label.displayname"),sortable:true,formatter:at},{key:"actions",label:y._msg("label.actions"),sortable:false,formatter:aq}];this.widgets.dataTable=new YAHOO.widget.DataTable(y.id+"-datatable",ar,this.widgets.dataSource,{initialLoad:false,renderLoopSize:32,dynamicData:true,sortedBy:{key:"displayName",dir:"asc"},generateRequest:function(av,ax){av=av||{pagination:null,sortedBy:null};var aw=encodeURIComponent((av.sortedBy)?av.sortedBy.key:ax.getColumnSet().keys[0].getKey());var au=(av.sortedBy&&av.sortedBy.dir===YAHOO.widget.DataTable.CLASS_DESC)?"desc":"asc";var ay="sortBy="+aw+"&dir="+au;if(y.query){ay=ay+"&shortNameFilter="+encodeURIComponent(y.query)}return ay},MSG_EMPTY:y._msg("message.empty")})},_setDefaultDataTableErrors:function Z(ap){ap.set("MSG_EMPTY",y._msg("message.empty","Alfresco.ConsoleGroups"));ap.set("MSG_ERROR",y._msg("message.error","Alfresco.ConsoleGroups"))},_buildSearchParams:function W(aq){var aq="shortNameFilter="+encodeURIComponent(aq);var ap=f.get(y.id+"-show-all").checked;return(ap?aq:aq+"&zone=APP.DEFAULT")+"&maxItems="+this.maxItems+"&sortBy="+this.sortBy},_setResultsMessage:function l(at,ar,aq){var ap=f.get(y.id+"-search-bar-text");ap.innerHTML=y._msg(at,ar,aq)}});this.panelHandlers.searchPanelHandler=new SearchPanelHandler();CreatePanelHandler=function n(){CreatePanelHandler.superclass.constructor.call(this,"create")};YAHOO.extend(CreatePanelHandler,Alfresco.ConsolePanelHandler,{_visible:false,_refresh:false,onLoad:function P(){this.widgets.creategroupOkButton=new YAHOO.widget.Button(y.id+"-creategroup-ok-button",{type:"button"});this.widgets.creategroupOkButton.on("click",this.onCreateGroupOKClick,null,this);this.widgets.creategroupAnotherButton=new YAHOO.widget.Button(y.id+"-creategroup-another-button",{type:"button"});this.widgets.creategroupAnotherButton.on("click",this.onCreateGroupAnotherClick,null,this);this.widgets.creategroupCancelButton=new YAHOO.widget.Button(y.id+"-creategroup-cancel-button",{type:"button"});this.widgets.creategroupCancelButton.on("click",this.onCreateGroupCancelClick,null,this);var ap=new Alfresco.forms.Form(y.id+"-create-form");ap.setSubmitElements([this.widgets.creategroupOkButton,this.widgets.creategroupAnotherButton]);ap.addValidation(y.id+"-create-shortname",Alfresco.forms.validation.mandatory,null,"keyup");ap.addValidation(y.id+"-create-shortname",Alfresco.forms.validation.nodeName,null,"keyup");ap.addValidation(y.id+"-create-shortname",Alfresco.forms.validation.length,{max:100,crop:true,includeWhitespace:false},"keyup");ap.addValidation(y.id+"-create-displayname",Alfresco.forms.validation.mandatory,null,"keyup");ap.addValidation(y.id+"-create-displayname",Alfresco.forms.validation.length,{max:255,crop:true,includeWhitespace:false},"keyup");ap.init();this.forms.createForm=ap},onBeforeShow:function u(){f.setStyle(y.id+"-create-main","visibility","hidden");this.clear()},clear:function ab(){var aq=f.get(y.id+"-create-shortname");if(aq.value.length!==0){aq.value=""}var ap=f.get(y.id+"-create-displayname");if(ap.value.length!==0){ap.value=""}if(this.forms.createForm!==null){this.forms.createForm.validate(Alfresco.forms.Form.NOTIFICATION_LEVEL_NONE)}},onShow:function I(){this._visible=true;this._refresh=false;window.scrollTo(0,0);f.setStyle(y.id+"-create-main","visibility","visible");f.get(y.id+"-create-shortname").focus()},onHide:function U(){this._visible=false},onCreateGroupOKClick:function q(ar,aq){var ap=function(at){window.scrollTo(0,0);Alfresco.util.PopupManager.displayMessage({text:y._msg("message.create-success")});y.refreshUIState({panel:"search",refresh:"true"})};this._createGroup(ap)},onCreateGroupCancelClick:function af(aq,ap){y.refreshUIState({panel:"search",refresh:this._refresh?"true":"false"})},onCreateGroupAnotherClick:function H(ar,aq){var ap=function(at){window.scrollTo(0,0);Alfresco.util.PopupManager.displayMessage({text:y._msg("message.create-success")});this._refresh=true;this.clear();f.get(y.id+"-create-shortname").focus()};this._createGroup(ap)},_createGroup:function al(aq){var at=this;var ar=this.forms.createForm;if(!ar.validate()){ar._setAllFieldsAsVisited();return}var ap=YAHOO.lang.trim(f.get(y.id+"-create-shortname").value);y.getParentGroups(ap,{fn:function(au){if(au){var az=false;var aw="";for(var av=0;av<au.length;av++){aw+=au[av].displayName+(av<au.length-1?", ":"")}aw=aw.length>0?aw:y._msg("label.theroot");Alfresco.util.PopupManager.displayPrompt({text:y._msg("message.confirm.add",ap,aw,y.group?y.group:y._msg("label.theroot")),buttons:[{text:y._msg("button.ok"),handler:function ay(){this.destroy();if(y.group){at._createGroupAfterExistCheck.call(at,aq)}else{Alfresco.util.PopupManager.displayPrompt({title:y._msg("message.failure"),text:y._msg("message.noAddGroupFromRootSupport")});return}}},{text:y._msg("button.cancel"),handler:function ax(){this.destroy()},isDefault:true}]})}else{at._createGroupAfterExistCheck.call(at,aq)}},scope:this},"message.create-failure")},_createGroupAfterExistCheck:function V(at){var ap=YAHOO.lang.trim(f.get(y.id+"-create-shortname").value);var aq=YAHOO.lang.trim(f.get(y.id+"-create-displayname").value);aq=aq==""?undefined:aq;var av={};var ar=Alfresco.constants.PROXY_URI+"api/";var au=at;if(y.group&&y.group.length>0){ar+="groups/"+encodeURIComponent(y.group)+"/children/GROUP_"+encodeURIComponent(ap);au=function(aw){if(aq&&ap!=aq){av.displayName=aq;y.panelHandlers.updatePanelHandler.updateGroupRequest(ap,av,{fn:at,scope:this})}else{at.call(this,aw)}}}else{ar+="rootgroups/"+encodeURIComponent(ap);if(aq){av.displayName=aq}}Alfresco.util.Ajax.jsonPost({url:ar,dataObj:av,successCallback:{fn:au,scope:this},failureCallback:{fn:function(ax){var aw=YAHOO.lang.JSON.parse(ax.serverResponse.responseText);Alfresco.util.PopupManager.displayPrompt({title:y._msg("message.failure"),text:y._msg("message.create-failure",aw.message)})},scope:this}})}});this.panelHandlers.createPanelHandler=new CreatePanelHandler();UpdatePanelHandler=function p(){UpdatePanelHandler.superclass.constructor.call(this,"update")};YAHOO.extend(UpdatePanelHandler,Alfresco.ConsolePanelHandler,{_visible:false,onLoad:function S(){this.widgets.updategroupSaveButton=new YAHOO.widget.Button(y.id+"-updategroup-save-button",{type:"button"});this.widgets.updategroupSaveButton.on("click",this.onUpdateGroupOKClick,null,this);this.widgets.updategroupCancelButton=new YAHOO.widget.Button(y.id+"-updategroup-cancel-button",{type:"button"});this.widgets.updategroupCancelButton.on("click",this.onUpdateGroupCancelClick,null,this);var ap=new Alfresco.forms.Form(y.id+"-update-form");ap.setSubmitElements(this.widgets.updategroupSaveButton);ap.addValidation(y.id+"-update-displayname",Alfresco.forms.validation.mandatory,null,"keyup");ap.addValidation(y.id+"-update-displayname",Alfresco.forms.validation.length,{max:255,crop:true,includeWhitespace:false},"keyup");ap.init();this.forms.updateForm=ap},onBeforeShow:function G(){f.setStyle(y.id+"-update-main","visibility","hidden")},onShow:function N(){this._visible=true},onHide:function X(){this._visible=false},onUpdate:function R(){var ap=function(ar){var aq=ar.json.data;f.get(y.id+"-update-title").innerHTML=d(aq.displayName);f.get(y.id+"-update-shortname").innerHTML=d(aq.shortName);f.get(y.id+"-update-displayname").value=aq.displayName;if(this.forms.updateForm){this.forms.updateForm.init()}window.scrollTo(0,0);f.setStyle(y.id+"-update-main","visibility","visible");f.get(y.id+"-update-displayname").focus()};Alfresco.util.Ajax.jsonGet({url:Alfresco.constants.PROXY_URI+"api/groups/"+encodeURIComponent(y.group.replace(/%/g,"%25")),successCallback:{fn:ap,scope:this},failureMessage:y._msg("message.getgroup-failure",d(y.group))})},onUpdateGroupOKClick:function Y(ar,ap){var aq=function(at){window.scrollTo(0,0);Alfresco.util.PopupManager.displayMessage({text:y._msg("message.update-success")});var au={panel:"search",refresh:"true"};if(y.query){au.query=y.query}y.refreshUIState(au)};this._updateGroup(aq)},onUpdateGroupCancelClick:function m(ar,ap){var aq={panel:"search",refresh:"false"};if(y.query){aq.query=y.query}y.refreshUIState(aq)},updateGroupRequest:function an(aq,ar,ap){Alfresco.util.Ajax.jsonPut({url:Alfresco.constants.PROXY_URI+"api/groups/"+encodeURIComponent(aq.replace(/%/g,"%25")),dataObj:ar,successCallback:ap,failureCallback:{fn:function(at){Alfresco.util.PopupManager.displayPrompt({title:y._msg("message.failure"),text:y._msg("message.update-failure",at.json.message?at.json.message:"")})},scope:this}})},_updateGroup:function o(ap){var aq=this.forms.updateForm;if(!aq.validate()){aq._setAllFieldsAsVisited();return}this.updateGroupRequest(y.group,{displayName:YAHOO.lang.trim(f.get(y.id+"-update-displayname").value)},{fn:ap,scope:this})}});this.panelHandlers.updatePanelHandler=new UpdatePanelHandler();return this};YAHOO.extend(Alfresco.ConsoleGroups,Alfresco.ConsoleTool,{query:null,refresh:false,group:null,groupDisplayName:null,options:{minSearchTermLength:1,maxSearchResults:100,maxPageSize:50},onReady:function i(){Alfresco.ConsoleGroups.superclass.onReady.call(this)},onStateChanged:function g(n,l){this.query=undefined;this.refresh=undefined;this.group=undefined;this.groupDisplayName=undefined;var m=this.decodeHistoryState(l[1].state);if(m.query!==undefined){this.query=m.query}if(m.refresh){this.refresh=m.refresh=="true"?true:false}if(m.group){this.group=m.group}if(m.groupDisplayName){this.groupDisplayName=m.groupDisplayName}if(m.showAll){this.showAll=m.showAll=="true"?true:false}if(m.panel){this.showPanel(m.panel)}if(this.currentPanelId==="search"){this.updateCurrentPanel()}else{if(this.currentPanelId==="create"||(m.group&&(this.currentPanelId==="view"||this.currentPanelId==="update"))){this.updateCurrentPanel()}}},onNewGroup:function h(n,l){var m=l[1].group;this.refreshUIState({panel:"create",group:m})},onUpdateGroup:function b(p,l){var o=l[1].group;var n=l[1].query;var m={panel:"update",group:o};if(n){m.query=n}this.refreshUIState(m)},encodeHistoryState:function c(n){var l={};if(this.currentPanelId!==""){l.panel=this.currentPanelId}var m="";if(n.panel||l.panel){m+="panel="+encodeURIComponent(n.panel?n.panel:l.panel)}if(n.group){m+=m.length>0?"&":"";m+="group="+encodeURIComponent(n.group)}if(n.query!==undefined){m+=m.length>0?"&":"";m+="query="+encodeURIComponent(n.query)}if(n.refresh){m+=m.length>0?"&":"";m+="refresh="+encodeURIComponent(n.refresh)}if(n.showAll!==undefined){m+=m.length>0?"&":"";m+="showAll="+encodeURIComponent(n.showAll)}return m},getParentGroups:function e(m,l,n){Alfresco.util.Ajax.jsonGet({url:Alfresco.constants.PROXY_URI+"api/groups/"+encodeURIComponent(m.replace(/%/g,"%25"))+"/parents?level=ALL&maxSize=10",successCallback:{fn:function(q){var p=q.json.data?q.json.data:[];l.fn.call(l.scope?l.scope:this,p)},scope:this},failureCallback:{fn:function(p){if(p.serverResponse.status==404){l.fn.call(l.scope?l.scope:this,null)}else{Alfresco.util.PopupManager.displayPrompt({title:this._msg("message.failure"),text:this._msg(n,p.json.message?p.json.message:"")})}},scope:this}})},_msg:function k(l){return Alfresco.util.message.call(this,l,"Alfresco.ConsoleGroups",Array.prototype.slice.call(arguments).slice(1))}})})();
