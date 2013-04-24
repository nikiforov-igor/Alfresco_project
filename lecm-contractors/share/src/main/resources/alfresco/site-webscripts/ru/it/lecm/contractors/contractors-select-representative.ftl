<#assign controlId = fieldHtmlId + "-cntrl">
<#assign selectId = fieldHtmlId + "-slct">
<#assign inputId = fieldHtmlId + "-inpt">
<#assign fieldId = fieldHtmlId + "-fld">

<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<#assign fieldValue=field.value!"">
<#if fieldValue?string == "" && field.control.params.defaultValueContextProperty??>
    <#if context.properties[field.control.params.defaultValueContextProperty]??>
        <#assign fieldValue = context.properties[field.control.params.defaultValueContextProperty]>
    <#elseif args[field.control.params.defaultValueContextProperty]??>
        <#assign fieldValue = args[field.control.params.defaultValueContextProperty]>
    </#if>
</#if>

<#if disabled>
<div id="${fieldId}" class="form-field">
    <div class="viewmode-field">
        <#if showViewIncompleteWarning && field.mandatory && !(fieldValue?is_number) && fieldValue?string == "">
        <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
        </#if>
        <span class="viewmode-label">${field.label?html}:</span>
        <span id="${controlId}-currentValueDisplay" class="viewmode-value"></span>
    </div>
</div>
<#else>
<div id="${fieldId}" class="form-field">
    <label for="${selectId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
    <select id="${selectId}" name="${field.name}" style="width: 28em;">
    </select>

    <input type="hidden" id="${controlId}-added" name="${field.name}_added"/>
    <input type="hidden" id="${controlId}-removed" name="${field.name}_removed"/>
</div>
</#if>
<input type="hidden" id="${controlId}" name="${field.name}" value="${field.value?html}"/>

<script>//<![CDATA[
    if (typeof LogicECM === "undefined" || !LogicECM) {
        var LogicECM = {};
    }

    LogicECM.module = LogicECM.module || {};

    (function () {
        "use strict";

        LogicECM.module.SelectRepresentativeForContractor = function LogicECM_module_SelectRepresentativeForContractor( fieldHtmlId ) {

            LogicECM.module.SelectRepresentativeForContractor.superclass.constructor.call(this, "LogicECM.module.SelectRepresentativeForContractor", fieldHtmlId, []);

        <#if disabled>
            // FUTURE: PLAY HARD GO PRO!!
            YAHOO.util.Event.onAvailable( "${controlId}", function() {
                var currentInputEl = YAHOO.util.Dom.get( "${controlId}" );

                if (currentInputEl !== null && currentInputEl.value.length > 0) {
                    Alfresco.util.Ajax.jsonGet({
                        url: Alfresco.constants.PROXY_URI + "slingshot/node/" + currentInputEl.value.replace("://", "/"),
                        successCallback:
                        {
                            fn: function (response) {
                                var currentDisplayValueElement = YAHOO.util.Dom.get( "${controlId}-currentValueDisplay" ),
                                        properties = response.json.properties,
                                        name = this.options.nameSubstituteString,
                                        i, prop, propSubstName;



                                for( i = 0; i < properties.length; i++ ) {
                                    prop = properties[i];
                                    if (prop.name && prop.values[0]) {
                                        propSubstName = this.options.openSubstituteSymbol + prop.name.prefixedName + this.options.closeSubstituteSymbol;
                                        if (name.indexOf(propSubstName) != -1) {
                                            name = name.replace(propSubstName, prop.values[0].value);
                                        }
                                    }
                                    currentDisplayValueElement.innerHTML = name;
                                }
                            },
                            scope: this
                        },
                        failureCallback:
                        {
                            fn: function (response) {
                            },
                            scope: this
                        }
                    });
                }
            }, this, true);

            return this;
        </#if>

            YAHOO.Bubbling.on("${field.control.params.updateOnAction}", this.onUpdateRepresentativesList, this);

            this.previousSelected = null;
            this._firstSelected = null;

            this.onFormFieldReady = function( that ) {

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

            onUpdateRepresentativesList: function( type, args ) {

                var selectElement = YAHOO.util.Dom.get( "${selectId}" ),
                    currentInputEl = YAHOO.util.Dom.get( "${controlId}" ),
                    addedInputEl = YAHOO.util.Dom.get( "${controlId}-added" ),
                    removedInputEl = YAHOO.util.Dom.get( "${controlId}-removed" ),
                    selectedContractors = Object.keys( args[1].selectedItems ), // Chrome, FF 4+, IE 9+, Safari 5+

                    selectedContractor;

                if( selectedContractors.length === 0 ) {

                    selectElement.options.length = 1; // FUTURE: JSHint-friendly.
                    selectElement.options[ 0 ] = new Option( "Без представителя...", "", true );
                    selectElement.disabled = true;

                    this.previousSelected = null;

                    // Помечаем к удалению то, что было выбрано. Если у нас уже есть что-то, помеченное к удалению, значит
                    // мы когда-то сменили контрагента и удалить представителя необходимо только для того, который был
                    // выбран в самом начале. Проще говоря, кого бы мы не выбрали, удалить нам необходимо только
                    // предыдущего.
                    removedInputEl.value = removedInputEl.value || currentInputEl.value;
                    addedInputEl.value = ""; // Кроме того, если мы что-то добавляли, то теперь отменяем добавление.
                    currentInputEl.value = "";

                    return;
                }

                selectElement.disabled = false;
                selectedContractor = selectedContractors[0];

                // Событие на которое мы подписываем этот обработчик вызывается 3+ раз за один "выбор" контрагента, а
                // заполнять список представителей необходимо только один раз.
                //
                // FUTURE: Если YAHOO.util.Dom.get не умеет кэшировать, кэшировать самому (Input-элементы).
                if( this.previousSelected === selectedContractor ) {
                    return;
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
                                mustBeSelected;

                            // Очищаем список.
                            selectElement.options.length = 1; // FUTURE: JSHint-friendly...
                            selectElement.options[ 0 ] = new Option( "Без представителя...", "", true, true );

                            for( i = 0; i < response.json.representatives.length; ++i ) {

                                // Выбираем основного представителя.
                                mustBeSelected = false;
                                if( currentInputEl.value === "" && // Если c сервера ничего не пришло или мы выбрали "Без представителя".
                                     this._firstSelected !== "") { // То уточним: это с сервера не пришло, или мы выбрали "Без представителя"?

                                    // Одновременное выполнение этих условий говорит нам о том, что мы в Edit-режиме,
                                    // что-то уже было выбрано, но мы выбрали "Без представителя", если так, то выбираем
                                    // основного представителя для контрагента (согласно требованиям).
                                    mustBeSelected = response.json.representatives[ i ].isPrimary;

                                    // Если с сервера ничего не приходило ( this._firstSelected === "" ), то мы в Create-
                                    // режиме, следовательно, ничего не делаем. В случае, если у контрагента не будет
                                    // основного представителя, то будет выбран пункт "Без представителя".

                                } else { // Если c сервера что-то пришло.
                                    if( currentInputEl.value === response.json.representatives[ i ].nodeRef ) {
                                        // То выберем его.
                                        mustBeSelected = true;
                                    }
                                }

                                // Если у нас есть Представитель с ( mustBeSelected === true ), то снимаем выделение с
                                // первого элемента "Без представителя...".
                                if( mustBeSelected ) {
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
                        },
                        scope: this
                    },
                    failureCallback: {
                        fn: function () {
                            Alfresco.util.PopupManager.displayMessage({
                                text: "Не удалось получить список представителей для выбранного контрагента. Обновите страницу." // Alfresco.component.Base.prototype.msg("")
                            });
                        }
                    }
                });
            }
        });
    })();

    new LogicECM.module.SelectRepresentativeForContractor("${controlId}");
//]]>
</script>