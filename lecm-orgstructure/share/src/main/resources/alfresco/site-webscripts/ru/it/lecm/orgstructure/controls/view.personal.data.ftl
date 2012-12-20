<#assign  id = args.htmlid/>

<script type="text/javascript">//<![CDATA[
(function () {
var Dom = YAHOO.util.Dom,
		Connect = YAHOO.util.Connect,
		Event = YAHOO.util.Event;
var employeeRef;
var dataRef;

function drawForm(nodeRef){
	dataRef = nodeRef;
	Alfresco.util.Ajax.request(
			{
				url:Alfresco.constants.URL_SERVICECONTEXT + "components/form",
				dataObj:{
					htmlid:"${id}",
					itemKind:"node",
					itemId:nodeRef,
					<#--formId:"${id}",-->
					mode:"view",
					showSubmitButton:"false"
				},
				successCallback:{
					fn:function(response){
						var formEl = document.getElementById("${id}-contentPersonalData");
						formEl.innerHTML = response.serverResponse.responseText;
					}
				},
				failureMessage:"message.failure",
				execScripts:true
			});
}

function showDialogCreate(nodeRef){
	// Intercept before dialog show
	var doBeforeDialogShow = function BeforeDialogShow(p_form, p_dialog) {
		Alfresco.util.populateHTML(
				[ "${id}-dialogTitle", "Personal Data" ]
		);
	};

	// Using Forms Service, so always create new instance
	var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
			{
				itemKind:"type",
				itemId:"lecm-orgstr:personal-data",
				destination:nodeRef,
				mode:"create",
				submitType:"json"
			});

	// Using Forms Service, so always create new instance
	var createDetails = new Alfresco.module.SimpleDialog("${id}-personalDataDialog");
	createDetails.setOptions(
			{
				width:"50em",
				templateUrl:templateUrl,
				actionUrl:null,
				destroyOnHide:true,
				doBeforeDialogShow:{
					fn:doBeforeDialogShow,
					scope:this
				},
				onSuccess:{
					fn:function on_success(response) {
						Alfresco.util.Ajax.jsonRequest(
								{
									url:Alfresco.constants.PROXY_URI + "lecm/base/createAssoc",
									method:"POST",
									dataObj:{
										source:employeeRef,
										target:response.json.persistedObject,
										assocType:"lecm-orgstr:employee-person-data-assoc"
									},
									successCallback:{
										fn:function(){
											Dom.removeClass("${id}-editPersonalData", 'hidden');
											Dom.addClass("${id}-createPersonalData", 'hidden');
											drawForm(response.json.persistedObject);
										},
										scope:this
									},
									failureCallback:{
										fn: function() {
											alert("Не удалось создать ассоциацию. Попробуйте обновить страницу")},
										scope:this
									}
								});
					},
					scope:this
				},
				onFailure:{
					fn:function DataGrid_onActionCreate_failure(response) {
						Alfresco.util.PopupManager.displayMessage(
								{
									text:"Данные не загружены"
								});
					},
					scope:this
				}
			}).show();
}

function showDialogEdit(nodeRef){
	// Intercept before dialog show
	var doBeforeDialogShow = function BeforeDialogShow(p_form, p_dialog) {
		Alfresco.util.populateHTML(
				[ "${id}-dialogTitle", "Personal Data" ]
		);
	};

	// Using Forms Service, so always create new instance
	var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
			{
				itemKind:"node",
				itemId:nodeRef,
				mode:"edit",
				submitType:"json"
			});

	// Using Forms Service, so always create new instance
	var editDetails = new Alfresco.module.SimpleDialog("${id}-personalDataDialog");
	editDetails.setOptions(
			{
				width:"50em",
				templateUrl:templateUrl,
				actionUrl:null,
				destroyOnHide:true,
				doBeforeDialogShow:{
					fn:doBeforeDialogShow,
					scope:this
				},
				onSuccess:{
					fn:function on_success(response) {
						drawForm(nodeRef)
					},
					scope:this
				},
				onFailure:{
					fn:function DataGrid_onActionCreate_failure(response) {
						Alfresco.util.PopupManager.displayMessage(
								{
									text:"Данные не загружены"
								});
					},
					scope:this
				}
			}).show();
}

    function editPersonalData() {
        showDialogEdit(dataRef);
    }

    function createPersonalData() {
        employeeRef = "${form.arguments.itemId}";
        var  sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/createEmployeePersonData";
        var callback = {
            success:function (oResponse) {
                var oResults = eval("(" + oResponse.responseText + ")");
                if (oResults != null) {
                    showDialogCreate(oResults.nodeRef);
                }
            },
            failure:function (oResponse) {
                alert("Не удалось загрузить персональные данные. Попробуйте обновить страницу.");
            },
            argument:{
            }
        };
        YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
    }

function initialize() {
	employeeRef = "${form.arguments.itemId}";
	var  sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getEmployeePersonalData?nodeRef="+employeeRef;
	new YAHOO.widget.Button("${id}-editPersonalData",{ onclick: { fn: editPersonalData} });
	new YAHOO.widget.Button("${id}-createPersonalData",{ onclick: { fn: createPersonalData} });
	var  sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getEmployeePerson?nodeRef="+employeeRef;
	var callback = {
		success:function (oResponse) {
			var oResults = eval("(" + oResponse.responseText + ")");
			if (oResults != null) {
				if (oResults.nodeRef == null) {
					// скрываем кнопку редактировать
					Dom.removeClass("${id}-createPersonalData", 'hidden');
				} else {
					drawForm(oResults.nodeRef);
					// скрываем кнопку создать
					Dom.removeClass("${id}-editPersonalData", 'hidden');
				}

			}
		},
		failure:function (oResponse) {
			alert("Не удалось загрузить персональные данные. Попробуйте обновить страницу.");
		},
		argument:{
		}
	};
	YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
}

    function OnButtonAvaiable(id) {
        YAHOO.util.Event.onContentReady(id, this.handleOnAvailable, this);
    }
    OnButtonAvaiable.prototype.handleOnAvailable = function (me) {
        initialize();
    };

    var obj = new OnButtonAvaiable("${id}-buttonPersonalData");
})();
//]]></script>

<div id="${id}">
	<div id="${id}-contentPersonalData"></div>
	<div id="${id}-buttonPersonalData">
	<#if form.mode == "edit" || form.mode == "create">
        <span id="${id}-createPersonalData" class="yui-button yui-push-button hidden">
           <span class="first-child">
              <button type="button">${msg('button.create')}</button>
           </span>
        </span>
		<span id="${id}-editPersonalData" class="yui-button yui-push-button hidden">
           <span class="first-child">
              <button type="button">${msg('button.edit')}</button>
           </span>
        </span>
	</#if>
	</div>
</div>