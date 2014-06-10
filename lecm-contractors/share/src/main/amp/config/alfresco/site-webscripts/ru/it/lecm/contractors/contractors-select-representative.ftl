<#assign controlId = fieldHtmlId + "-cntrl">
<#assign selectId = fieldHtmlId + "-slct">
<#assign inputId = fieldHtmlId + "-inpt">
<#assign fieldId = fieldHtmlId + "-fld">
<#if form.mode == "view">
    <#assign fieldId = fieldId + "-view-repsesentative">
</#if>

<#assign emptyMessageId = field.control.params.emptyMessageId ! "">
<#assign emptyMessage = (field.control.params.emptyMessage) ! msg(emptyMessageId)>
<#if emptyMessage?length == 0>
	<#assign emptyMessage = "Без адресанта...">
</#if>

<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<#assign defaultValue=field.control.params.defaultValue!"">
<#if form.arguments[field.name]?has_content>
	<#assign defaultValue=form.arguments[field.name]>
</#if>

<#assign fieldValue=field.value!"">
<#if fieldValue?string == "" && field.control.params.defaultValueContextProperty??>
    <#if context.properties[field.control.params.defaultValueContextProperty]??>
        <#assign fieldValue = context.properties[field.control.params.defaultValueContextProperty]>
    <#elseif args[field.control.params.defaultValueContextProperty]??>
        <#assign fieldValue = args[field.control.params.defaultValueContextProperty]>
    </#if>
</#if>

<#if disabled>
<div id="${fieldId}" class="control contractors-select-representative viewmode">
    <div class="label-div">
        <#if showViewIncompleteWarning && field.mandatory && !(fieldValue?is_number) && fieldValue?string == "">
            <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}"/><span>
        </#if>
        <label>${field.label?html}:</label>
    </div>
    <div class="container">
        <div class="value-div">
            <input type="hidden" id="${controlId}" name="${field.name}" value="${field.value?html}"/>
            <span id="${controlId}-currentValueDisplay" class="mandatory-highlightable"></span>
        </div>
    </div>
</div>
<#else>
<div id="${fieldId}" class="control contractors-select-representative editmode">
    <div class="label-div">
        <label for="${selectId}">
            ${field.label?html}:
            <#if field.mandatory>
                <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
            </#if>
        </label>
        <input type="hidden" id="${controlId}-removed" name="${field.name}_removed"/>
        <input type="hidden" id="${controlId}-added" name="${field.name}_added"/>
    </div>
    <div class="container">
        <div class="buttons-div">
            <span class="create-new-button">
                <input type="button" id="${controlId}-add-new-representative-button"/>
            </span>
        </div>
        <div class="value-div">
            <input type="hidden" id="${controlId}" name="${field.name}" value="${field.value?html}"/>
            <select id="${selectId}" name="${field.name}" class="mandatory-highlightable"></select>
        </div>
    </div>
</div>
</#if>
<div class="clear"></div>

<script>//<![CDATA[
    (function () {
        "use strict";

        var globCurrentContractor = null; // Глобальная переменная модуля для сохранения выбранного контрагента.

        LogicECM.module.SelectRepresentativeForContractor = function LogicECM_module_SelectRepresentativeForContractor( fieldHtmlId ) {

            LogicECM.module.SelectRepresentativeForContractor.superclass.constructor.call(this, "LogicECM.module.SelectRepresentativeForContractor", fieldHtmlId, []);

        <#if disabled>
            // FUTURE: PLAY HARD GO PRO!!
            YAHOO.util.Event.onAvailable( "${controlId}", function() {
                var currentInputEl = YAHOO.util.Dom.get( "${controlId}" );

                if (currentInputEl !== null && currentInputEl.value.length > 0) {
                    Alfresco.util.Ajax.jsonGet({
                        url: Alfresco.constants.PROXY_URI + "slingshot/doclib2/node/" + currentInputEl.value.replace("://", "/"),
                        successCallback:
                        {
                            fn: function (response) {
                                var currentDisplayValueElement = YAHOO.util.Dom.get( "${controlId}-currentValueDisplay" ),
                                    properties = response.json.item.node.properties,
                                    name = this.options.nameSubstituteString,

                                    propSubstName,
                                    prop;

                                for ( prop in properties ) {
                                    propSubstName = this.options.openSubstituteSymbol + prop + this.options.closeSubstituteSymbol;

                                    if ( name.indexOf( propSubstName ) != -1 ) {
                                        name = name.replace( propSubstName, properties[ prop ] );
                                    }
                                }

                                currentDisplayValueElement.innerHTML = name;
                            },
                            scope: this
                        }
                    });
                }
            }, this, true);

            return this;
        </#if>

            YAHOO.Bubbling.on("${field.control.params.updateOnAction}", this.onUpdateRepresentativesList, this);

			this._showAddRepresentativeDialog = function(response) {
                var isPrimaryCheckboxChecked,
					templateRequestParams = {
						itemKind: "type",
						itemId: "lecm-contractor:link-representative-and-contractor",
						destination: globCurrentContractor,
						mode: "create",
						submitType: "json",
						ignoreNodes: response.json.join(),
						showCancelButton: "true"
					},
					// Создание формы добавления адресанта.
					addRepresentativeForm = new Alfresco.module.SimpleDialog("${fieldHtmlId}-add-representative-form");

                addRepresentativeForm.setOptions({
                    width: "50em",
                    templateUrl: "components/form",
					templateRequestParams: templateRequestParams,
                    destroyOnHide: true,
                    doBeforeFormSubmit: {
                        fn: function() {
                            isPrimaryCheckboxChecked = YAHOO.util.Dom.get( "${fieldHtmlId}-add-representative-form_prop_lecm-contractor_link-to-representative-association-is-primary-entry" ).checked;
                        },
                        scope: this
                    },
					doBeforeDialogShow: {
						fn: function (p_form, p_dialog) {
							var message;
							if (this.options.createNewMessage) {
								message = this.options.createNewMessage;
							} else {
								message = this.msg("dialog.createNew.title");
							}
							p_dialog.dialog.setHeader(message);
						},
						scope: this
					},

                    onSuccess: {
                        fn: function( response ) {

                            var addedLinkRef = response.json.persistedObject, // persistedObject это [link-representative-and-contractor], НЕ [representative-type]
                                fakeObject = {};

                            fakeObject[ globCurrentContractor ] = null;
                            YAHOO.util.Dom.get( "${controlId}" ).value = "";

                            if( isPrimaryCheckboxChecked ) {
                                Alfresco.util.Ajax.request({
                                    method: "POST",
                                    url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/contractors/representatives/reassign",
                                    dataObj: { "representativeToAssignAsPrimary": addedLinkRef },
                                    requestContentType: "application/json",
                                    responseContentType: "application/json",
                                    successCallback: {
                                        fn: function() {
                                            this.onUpdateRepresentativesList( null, [ null, { selectedItems: fakeObject } ], /* force */ true, addedLinkRef );
                                        },
                                        scope: this
                                    },
                                    failureCallback: {
                                        fn: function() {
                                            Alfresco.util.PopupManager.displayMessage({
                                                text: Alfresco.component.Base.prototype.msg("message.reassign-representative.failure")
                                            });
                                        }
                                    }
                                });
                            } else {
                                this.onUpdateRepresentativesList( null, [ null, { selectedItems: fakeObject } ], /* force */ true, addedLinkRef );
                            }

                            Alfresco.util.PopupManager.displayMessage({
                                text: Alfresco.component.Base.prototype.msg("message.add-representative.success")
                            });
                        },
                        scope: this
                    },
                    onFailure: {
                        fn: function() {
                            Alfresco.util.PopupManager.displayMessage({
                                text: Alfresco.component.Base.prototype.msg("message.add-representative.failure")
                            });
                        },
                        scope: this
                    }
                });

                addRepresentativeForm.show();
			};

            this._showAddRepresentativeForm = function() {
                if( globCurrentContractor === null ) {
                    window.alert( "Необходимо выбрать контрагента" );
                    return false;
                }

                // Спасаем "тонущие" всплывающие сообщения.
                Alfresco.util.PopupManager.zIndex = 9000;

				//дергаем сервис который получает список адресантов, которые связаны с контрагентом
				var that = this;
				Alfresco.util.Ajax.request({
					method: "GET",
					url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/contractors/representatives/busy",
					responseContentType: "application/json",
					successCallback: {
						fn: this._showAddRepresentativeDialog,
						scope: this
					},
					failureCallback: {
						fn: function () {
							Alfresco.util.PopupManager.displayMessage({
								text: "Не удалось получить список адресантов, уже привязанных к контрагенту."
							});
						}
					}
				});
                return true;
			};

            this.previousSelected = null;
            this._firstSelected = null;

            this.onFormFieldReady = function( that ) {

                var addRepresentativeButton = new YAHOO.widget.Button( "${controlId}-add-new-representative-button", { onclick: { fn: that._showAddRepresentativeForm, scope: that } } );
                    window.arb = addRepresentativeButton;

                // Собираем Input-элементы.
                var currentInputEl = YAHOO.util.Dom.get( "${controlId}" ),
                    addedInputEl = YAHOO.util.Dom.get( "${controlId}-added" ),
                    removedInputEl = YAHOO.util.Dom.get( "${controlId}-removed" );

                // Необходимо для распознования "подёргивания".
                that._firstSelected = currentInputEl.value;

                YAHOO.util.Event.on("${selectId}", "change", function( /*event, that*/ ) {

                    if ( that._firstSelected === this.value ) {
                        addedInputEl.value = "";
                        removedInputEl.value = "";
                    } else {
                        addedInputEl.value = this.value;
                        removedInputEl.value = that._firstSelected;
                    }

                    currentInputEl.value = this.value;
                });
            };

            YAHOO.util.Event.onContentReady( "${fieldId}", this.onFormFieldReady, this );

            return this;
        };

        YAHOO.extend(LogicECM.module.SelectRepresentativeForContractor, Alfresco.component.Base, {

            options: {
                nameSubstituteString: "{lecm-representative:surname} {lecm-representative:firstname}",

                openSubstituteSymbol:  "{",
                closeSubstituteSymbol: "}"
            },

            previousSelected: null,
            _firstSelected: null,

            onUpdateRepresentativesList: function( type, args, force, representativeToSelect ) {

                var selectElement = YAHOO.util.Dom.get( "${selectId}" ),
                    currentInputEl = YAHOO.util.Dom.get( "${controlId}" ),
                    addedInputEl = YAHOO.util.Dom.get( "${controlId}-added" ),
                    removedInputEl = YAHOO.util.Dom.get( "${controlId}-removed" ),
					selectedContractors = Object.keys( args[1].selectedItems ), // IE 9+

                    selectedContractor;

                if( selectedContractors.length === 0 ) {

                    selectElement.options.length = 1; // FUTURE: JSHint-friendly.
					selectElement.options[ 0 ] = new Option( this.options.emptyMessage, "", true );
                    selectElement.disabled = true;

                    this.previousSelected = null;

                    // Помечаем к удалению то, что было выбрано. Если у нас уже есть что-то, помеченное к удалению, значит
					// мы когда-то сменили контрагента и удалить адресанта необходимо только для того, который был
                    // выбран в самом начале. Проще говоря, кого бы мы не выбрали, удалить нам необходимо только
                    // предыдущего.
                    removedInputEl.value = removedInputEl.value || currentInputEl.value;
                    addedInputEl.value = ""; // Кроме того, если мы что-то добавляли, то теперь отменяем добавление.
                    currentInputEl.value = "";
                    globCurrentContractor = null;

                    return;
                }

                selectElement.disabled = false;
                selectedContractor = globCurrentContractor = selectedContractors[0];

                // Событие на которое мы подписываем этот обработчик вызывается 3+ раз за один "выбор" контрагента, а
				// заполнять список адресантов необходимо только один раз.
                //
                // FUTURE: Если YAHOO.util.Dom.get не умеет кэшировать, кэшировать самому (Input-элементы).
                if( this.previousSelected === selectedContractor ) {
                    if( !force ) {
                        return;
                    }
                } else {
                    this.previousSelected = selectedContractor;
                }

                Alfresco.util.Ajax.request({
                    method: "POST",
                    url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/contractors/getrepresentatives",
                    dataObj: { targetContractor: selectedContractor },
                    requestContentType: "application/json",
                    responseContentType: "application/json",
                    successCallback: {
                        fn: function ( response ) {
                            var i,
                                shortName,
								mustBeSelected,
								wasNotSelected = true,
								lg = response.json.representatives.length;

                            // Очищаем список.
                            selectElement.options.length = 1; // FUTURE: JSHint-friendly...
							selectElement.options[ 0 ] = new Option( this.options.emptyMessage, "", true, true );

							// Если представители отсутствуют.
							if( lg == 0 ) {
								addedInputEl.value = "";
								removedInputEl.value = removedInputEl.value || currentInputEl.value;
							}

							for( i = 0; i < lg; ++i ) {

								// Выбираем основного адресанта.
                                mustBeSelected = false;

                                if( representativeToSelect ) {
                                    // representativeToSelect это [link-representative-and-contractor], НЕ [representative-type]
                                    mustBeSelected = response.json.representatives[ i ].linkRef === representativeToSelect;
								} else if (this.options.defaultValue) { // нам было передано значение по умолчанию
									mustBeSelected = this.options.defaultValue === response.json.representatives[ i ].nodeRef;
								} else if( currentInputEl.value === "" ) { // Если c сервера ничего не пришло или мы выбрали "Без адресанта".
									// То выбираем основного адресанта (согласно требованиям).
                                    mustBeSelected = response.json.representatives[ i ].isPrimary;
                                } else { // Если c сервера что-то пришло, то мы в Edit-режиме, тогда...
                                    if( currentInputEl.value === response.json.representatives[ i ].nodeRef ) {
                                        // Выбираем того, кто "пришёл с сервера".
                                        mustBeSelected = true;
                                    }
                                }

								// Если у нас есть Адресант с ( mustBeSelected === true ), то снимаем выделение с
								// первого элемента "Без адресанта...".
                                if( mustBeSelected ) {
									wasNotSelected = false;

                                    selectElement.options[ 0 ].selected = false;
                                    selectElement.options[ 0 ].defaultSelected = false;

                                    // Обновление Input-элементов из обработчика события 'change' для выпадающего списка.
                                    // FUTURE: Вынести в отдельный метод.
                                    if ( this._firstSelected === response.json.representatives[ i ].nodeRef ) {
                                        addedInputEl.value = "";
                                        removedInputEl.value = "";
                                    } else {
                                        addedInputEl.value = response.json.representatives[ i ].nodeRef;
                                        removedInputEl.value = this._firstSelected;
                                    }

                                    currentInputEl.value = response.json.representatives[ i ].nodeRef;
                                }

                                shortName = response.json.representatives[ i ].shortName;
                                selectElement.options[ selectElement.options.length ] = new Option(
                                    response.json.representatives[ i ].isPrimary ? "[" + shortName + "]" : shortName,
                                    response.json.representatives[ i ].nodeRef,
                                    mustBeSelected,
                                    mustBeSelected
                                );
                            }

							// Если никто из Адресантов не был выбран автоматически, значит - отсутствует Основной контакт,
							// в этом случае, очищаем всё, что было до этого.
							if(wasNotSelected) {
								addedInputEl.value = "";
								removedInputEl.value = removedInputEl.value || currentInputEl.value;
							}
                        },
                        scope: this
                    },
                    failureCallback: {
                        fn: function () {
                            Alfresco.util.PopupManager.displayMessage({
								text: "Не удалось получить список адресантов для выбранного контрагента. Обновите страницу." // Alfresco.component.Base.prototype.msg("")
                            });
                        }
                    }
                });
            }
        });
    })();

	new LogicECM.module.SelectRepresentativeForContractor("${controlId}").setOptions({
		<#if defaultValue?has_content>
			defaultValue: "${defaultValue?string}",
		</#if>
		emptyMessage: '${emptyMessage}',
		<#if field.control.params.createNewMessage??>
			createNewMessage: "${field.control.params.createNewMessage}"
		<#elseif field.control.params.createNewMessageId??>
			createNewMessage: "${msg(field.control.params.createNewMessageId)}"
		</#if>
	});
//]]>
</script>