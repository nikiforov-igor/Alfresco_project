(function () {
    LogicECM.module.Base.Util.loadCSS([
        'css/lecm-errands/errands-create-form.css'
    ]);

    var Dom = YAHOO.util.Dom,
        Selector = YAHOO.util.Selector,
        Bubbling = YAHOO.Bubbling,
        Substitute = YAHOO.lang.substitute;

    var formButtons;
    var baseDocExecutionDate = null;
    var currentUserIsErrandsInitiator = false;
    var baseDocHasReview = false;
    var isCreateForm = true;
    var initialized = false;

    Bubbling.on('saveDraftResolutionButtonClick', saveDraft);
    Bubbling.on('sendResolutionButtonClick', sendResolutionClick);
    Bubbling.on('sendResolution', sendResolution);
    Bubbling.on('resolutionCreateFormScriptLoaded', init);
    Bubbling.on('resolutionEditFormScriptLoaded', init);

    function saveDraft(layer, args) {
        var form;
        if (args[1] && args[1].formId) {
            form = Dom.get(args[1].formId + "-form");
            if (form && form["prop_lecm-resolutions_is-draft"]) {
                form["prop_lecm-resolutions_is-draft"].value = true;
            }
        }
        submitResolutionForm(true, form);
    }

    function sendResolutionClick(layer, args) {
        var form;
        if (args[1] && args[1].formId) {
            form = Dom.get(args[1].formId + "-form");
            if (form && form["prop_lecm-resolutions_is-draft"]) {
                form["prop_lecm-resolutions_is-draft"].value = false;
            }
        }

        if (currentUserIsErrandsInitiator) {
            var errandsCount = Dom.get(args[1].formId + '_prop_lecm-resolutions_errands-json-count');
            var reviewers = Dom.get(args[1].formId + '_assoc_lecm-resolutions_reviewers-assoc');
            if (!isCreateForm || (errandsCount && parseInt(errandsCount.value)) || (reviewers && reviewers.value && reviewers.value.length)) {
                submitResolutionForm(true, form);
            } else {
                if (baseDocHasReview) {
                    Alfresco.util.PopupManager.displayMessage(
                        {
                            text: Alfresco.util.message('title.resolution.errands.reviewers.empty')
                        });
                } else {
                    Alfresco.util.PopupManager.displayMessage(
                        {
                            text: Alfresco.util.message('title.resolution.errands.empty')
                        });
                }
            }
        } else {
            Alfresco.util.PopupManager.displayMessage(
                {
                    text: Alfresco.util.message('title.resolution.errands.isNotStarter')
                });
        }
    }

    function submitResolutionForm(checkExecutionDate, form) {
        if (checkExecutionDate && form) {
            if (baseDocExecutionDate) {
                checkBaseDocExecutionDate(form);
            } else if (form["prop_lecm-resolutions_base-doc-execution-date-attr-name"]
                && form["prop_lecm-resolutions_base-doc-execution-date-attr-name"].value
                && form["assoc_lecm-resolutions_base-document-assoc"]
                && form["assoc_lecm-resolutions_base-document-assoc"].value) {

                Alfresco.util.Ajax.jsonPost(
                    {
                        url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                        dataObj: {
                            nodeRef: form["assoc_lecm-resolutions_base-document-assoc"].value,
                            substituteString: "{" + form["prop_lecm-resolutions_base-doc-execution-date-attr-name"].value + "?yyyy-MM-dd'T'HH:mm:ss}"
                        },
                        successCallback: {
                            fn: function (response) {
                                if (response.json != null && response.json.formatString != null) {
                                    baseDocExecutionDate = response.json.formatString;
                                    checkBaseDocExecutionDate(form);
                                } else {
                                    doSubmitResolutionForm();
                                }
                            },
                            scope: this
                        }
                    });
            } else {
                doSubmitResolutionForm();
            }
        } else {
            doSubmitResolutionForm();
        }
    }

    function checkBaseDocExecutionDate(form) {
        var parentExecutionDate = Alfresco.util.fromISO8601(baseDocExecutionDate);

        var executionDateRadio = form["prop_lecm-resolutions_limitation-date-radio"];
        if (executionDateRadio && executionDateRadio.value) {
            if (executionDateRadio.value == "DATE") {
                var executionDateDate = form["prop_lecm-resolutions_limitation-date"];
                compareExecutionDate(parentExecutionDate, Alfresco.util.fromISO8601(executionDateDate.value));
            } else if (executionDateRadio.value == "DAYS") {
                var executionDateDays = form["prop_lecm-resolutions_limitation-date-days"];
                var executionDateType = form["prop_lecm-resolutions_limitation-date-type"];
                if (executionDateDays && executionDateDays.value && executionDateType && executionDateType.value) {
                    var days = parseInt(executionDateDays.value);

                    executionDateDate = new Date();
                    executionDateDate.setHours(12);
                    executionDateDate.setMinutes(0);
                    executionDateDate.setSeconds(0);
                    executionDateDate.setMilliseconds(0);

                    if (executionDateType.value == "CALENDAR") {
                        executionDateDate.setDate(executionDateDate.getDate() + days);
                        compareExecutionDate(parentExecutionDate, executionDateDate);
                    } else if (executionDateType.value == "WORK") {
                        Alfresco.util.Ajax.jsonPost(
                            {
                                url: Alfresco.constants.PROXY_URI + "lecm/wcalendar/workCalendar/getNextWorkingDate",
                                dataObj: {
                                    startDate: Alfresco.util.toISO8601(executionDateDate, {"milliseconds": true}),
                                    offset: days,
                                    type: "hours"
                                },
                                successCallback: {
                                    fn: function (response) {
                                        if (response.json && response.json.date) {
                                            compareExecutionDate(parentExecutionDate, new Date(response.json.date));
                                        }
                                    }
                                }
                            });
                    }
                }
            } else {
                doSubmitResolutionForm();
            }
        }
    }

    function compareExecutionDate(parentDocExecutionDate, resolutionExecutionDate) {
        if (parentDocExecutionDate && resolutionExecutionDate && (resolutionExecutionDate > parentDocExecutionDate)) {
            Alfresco.util.PopupManager.displayPrompt({
                title: Alfresco.util.message('title.resolution.executionDate.later.baseDoc'),
                text: Alfresco.util.message('message.resolution.executionDate.later.baseDoc'),
                close: false,
                modal: true,
                buttons: [
                    {
                        text: Alfresco.util.message('button.yes'),
                        handler: function () {
                            doSubmitResolutionForm();
                            this.destroy();
                        }
                    }, {
                        text: Alfresco.util.message('button.no'),
                        handler: function () {
                            this.destroy();
                        },
                        isDefault: true
                    }]
            });
        } else {
            doSubmitResolutionForm();
        }
    }

    function doSubmitResolutionForm() {
        var routeButton = Selector.query(".yui-submit-button", formButtons, true);
        routeButton.click();
    }

    function sendResolution(layer, args) {
        if (args[1] && args[1].form) {
            var formData = args[1].form.getFormData();
            if (formData) {
                var resolutionExecutionDate = {
                    "date-radio": formData["prop_lecm-resolutions_limitation-date-radio"],
                    "date-days": formData["prop_lecm-resolutions_limitation-date-days"],
                    "date-type": formData["prop_lecm-resolutions_limitation-date-type"],
                    "date": formData["prop_lecm-resolutions_limitation-date"]
                };

                var errandsJson = eval(formData["prop_lecm-resolutions_errands-json"]);
                var errandsExecutionDates = [];
                if (errandsJson && errandsJson.length) {
                    errandsJson.forEach(function (obj) {
                        errandsExecutionDates.push({
                            "date-radio": obj["prop_lecm-errands_limitation-date-radio"],
                            "date-days": obj["prop_lecm-errands_limitation-date-days"],
                            "date-type": obj["prop_lecm-errands_limitation-date-type"],
                            "date": obj["prop_lecm-errands_limitation-date"]
                        });
                    })
                }

                Alfresco.util.Ajax.jsonPost(
                    {
                        url: Alfresco.constants.PROXY_URI + "lecm/resolutions/checkExecutionDates",
                        dataObj: {
                            resolutionExecutionDate: resolutionExecutionDate,
                            errandsExecutionDates: errandsExecutionDates
                        },
                        successCallback: {
                            fn: function (response) {
                                if (response.json && response.json.errands) {
                                    if (response.json.total) {
                                        if (YAHOO.lang.isFunction(args[1].submitFunction) && args[1].submitElement) {
                                            args[1].submitFunction.call(args[1].submitElement);
                                        }
                                    } else {
                                        var formId = args[1].form.formId.replace("-form", "") + "_prop_lecm-resolutions_errands-json-line-";
                                        var hasInvalidErrands = false;
                                        for (var i = 0; i < errandsExecutionDates.length; i++) {
                                            if (!response.json.errands[i]) {
                                                var inputId;
                                                if (errandsExecutionDates[i]["date-radio"] == "DAYS") {
                                                    inputId = formId + (i + 1) + "_prop_lecm-errands_limitation-date-days";
                                                } else if (errandsExecutionDates[i]["date-radio"] == "DATE") {
                                                    inputId = formId + (i + 1) + "_prop_lecm-errands_limitation-date";
                                                }

                                                if (inputId) {
                                                    Dom.addClass(inputId, "execution-date-invalid");
                                                    hasInvalidErrands = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (hasInvalidErrands) {
                                            submitResolutionForm(false);
                                        }
                                    }
                                }
                            }
                        }
                    });
            }
        }
    }

    function init(layer, args) {
        if (initialized) {
            return;
        }
        isCreateForm = layer == "resolutionCreateFormScriptLoaded";
        var formId = args[1].formId;
        formButtons = Dom.get(formId + "-form-buttons");

        Alfresco.util.Ajax.jsonGet({
            url: Alfresco.constants.PROXY_URI + "lecm/orgstructure/isCurrentEmployeeHasBusinessRole",
            dataObj: {
                roleId: "ERRANDS_INITIATOR"
            },
            successCallback: {
                fn: function (response) {
                    currentUserIsErrandsInitiator = response.json;
                }
            },
            failureMessage: Alfresco.util.message("message.failure")
        });


        var form = Dom.get(formId + "-form");
        var value =(form && form["assoc_lecm-resolutions_base-document-assoc"] && form["assoc_lecm-resolutions_base-document-assoc"].value) || (args[1].fieldId && args[1].fieldId === "lecm-resolutions:base-document-assoc" && args[1].items);
        if (value) {
            initialized = true;
            Alfresco.util.Ajax.jsonPost(
                {
                    url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                    dataObj: {
                        nodeRef: value,
                        substituteString: "{@doc.hasAspect('lecm-review-ts:review-aspect')}"
                    },
                    successCallback: {
                        fn: function (response) {
                            if (response.json && response.json.formatString && response.json.formatString == "true") {
                                baseDocHasReview = true;
                                var queryTemplate = 'div[class^=\"{formId}-form-panel {targetClass}\"]';
                                var sets = Selector.query(Substitute(queryTemplate, {
                                    targetClass: 'reviewers-hidden',
                                    formId: formId
                                }));

                                if (sets && sets.length) {
                                    Dom.removeClass(sets[0], 'hidden1');
                                }
                            }
                        },
                        scope: this
                    }
                });
        }
    }
})();