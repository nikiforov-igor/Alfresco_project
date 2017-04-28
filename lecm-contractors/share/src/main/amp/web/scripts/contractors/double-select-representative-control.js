/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};

(function () {
    LogicECM.module.DoubleSelectRepresentativeForContractor = function LogicECM_module_SelectRepresentativeForContractor(controlId, contractorSelectEvent, organizationSelectEvent) {
        LogicECM.module.DoubleSelectRepresentativeForContractor.superclass.constructor.call(this, "LogicECM.module.DoubleSelectRepresentativeForContractor", controlId, []);

        YAHOO.Bubbling.on(contractorSelectEvent, this.onSelect, this);
        YAHOO.Bubbling.on(organizationSelectEvent, this.onSelect, this);
        YAHOO.Bubbling.on('formValueChanged', this.onControlRegistered, this);

        this.previousSelected = null;
        this.controlsDeferred = new Alfresco.util.Deferred(["AssociationTreeViewer"], {
            scope: this,
            fn: function (oParams) {
                this._updateControlsOnDeferred();
            }
        });
        this.options.contractorSelectEvent = contractorSelectEvent;
        this.options.organizationSelectEvent = organizationSelectEvent;

        return this;
    };

    YAHOO.extend(LogicECM.module.DoubleSelectRepresentativeForContractor, Alfresco.component.Base, {
        previousSelected: null,
        controlsDeferred: null,

        options: {
            employeesByOrgDS: "lecm/employees/byOrg/{organization}/picker",
            representativesByContrDS: "lecm/representatives/{contractor}/picker",

            employeesType: "lecm-orgstr:employee",
            representativesType: "lecm-representative:representative-type",

            employeesNameSubstitute: "{lecm-orgstr:employee-short-name}",
            representativesSubstitute: "{lecm-representative:surname} {lecm-representative:firstname} {lecm-representative:propMiddlename}",

            employeesLocation: null,
            representativesLocation: null,

            employeesDefaultValueDS: null,
            representativesDefaultValueDS: null,

            disabled: false,

            currentValue: '',
            defaultValue: '',
            defaultValueLoaded: false,

            autoCompleteJsName: "select-representative-autoComplete",
            treeViewJsName: "select-representative-treeView",
            fieldHtmlId: "",
            showAssocViewForm: false,
            markers: null,
	        defaultValueUseOnce: false,
            doNotCheckAccess: false
        },

        onReady: function SelectRepresentativeForContractor_onReady() {
        },

        onControlRegistered: function (layer, args) {
            var obj = args[1];
            var control = obj.eventGroup;
            if (control.id === this.options.fieldHtmlId) {
                this.controlsDeferred.fulfil(control.name);
            }
        },

        onSelect: function (layer, args) {
            var initDatatasources = function(scope) {
                if (marker == 'contractor') {
                    var autocompleteConf,
                        treeConf;
                    autocompleteConf = {
                        startLocation: scope.options.representativesLocation,
                        itemType: scope.options.representativesType,
                        childrenDataSource: YAHOO.lang.substitute(scope.options.representativesByContrDS,
                            {
                                contractor: selectedContractor != null ? new Alfresco.util.NodeRef(selectedContractor).uri : ""
                            }),
                        defaultValueDataSource: YAHOO.lang.substitute(scope.options.representativesDefaultValueDS,
                            {
                                contractor: selectedContractor
                            }),
                        nameSubstituteString: scope.options.representativesSubstitute,
                        selectedValueNodeRef: "",
                        lazyLoading: false,
                        disabled: false,
                        showAssocViewForm: scope.options.showAssocViewForm,
                        doNotCheckAccess: scope.options.doNotCheckAccess
                    };

                    treeConf = {
                        rootLocation: scope.options.representativesLocation,
                        itemType: scope.options.representativesType,
                        childrenDataSource: YAHOO.lang.substitute(scope.options.representativesByContrDS,
                            {
                                contractor: selectedContractor != null ? new Alfresco.util.NodeRef(selectedContractor).uri : ""
                            }),
                        defaultValueDataSource: YAHOO.lang.substitute(scope.options.representativesDefaultValueDS,
                            {
                                contractor: selectedContractor
                            }),
	                    defaultValueUseOnce: scope.options.defaultValueUseOnce,
                        nameSubstituteString: scope.options.representativesSubstitute,
                        selectedValue: null,
                        initialized: false,
                        lazyLoading: false,
                        disabled: false,
                        doNotCheckAccess: scope.options.doNotCheckAccess
                    }
                    scope._updateControls(selectedContractor, resetValue, autocompleteConf, treeConf);
                } else {
                    scope.isCurrentUserFromSelectedOrgUnit(selectedContractor, updateControls);
                    function updateControls(showAssocViewForm) {
                        autocompleteConf = {
                            startLocation: scope.options.employeesLocation,
                            itemType: scope.options.employeesType,
                            childrenDataSource: YAHOO.lang.substitute(scope.options.employeesByOrgDS, {
                                organization: selectedContractor != null ? new Alfresco.util.NodeRef(selectedContractor).uri : ""
                            }),
                            defaultValueDataSource: scope.options.employeesDefaultValueDS != null ? YAHOO.lang.substitute(scope.options.employeesDefaultValueDS, {
                                organization: selectedContractor
                            }) : null,
                            nameSubstituteString: scope.options.employeesNameSubstitute,
                            lazyLoading: false,
                            disabled: false,
                            showAssocViewForm: showAssocViewForm,
                            doNotCheckAccess: scope.options.doNotCheckAccess,
                            additionalFilter: '@lecm\-orgstr\-aspects\:linked\-organization\-assoc\-ref:"' + selectedContractor + '\"'
                        };

                        treeConf = {
                            showAssocViewForm: showAssocViewForm,
                            rootLocation: scope.options.employeesLocation,
                            itemType: scope.options.employeesType,
                            childrenDataSource: YAHOO.lang.substitute(scope.options.employeesByOrgDS, {
                                organization: selectedContractor != null ? new Alfresco.util.NodeRef(selectedContractor).uri : ""
                            }),
                            defaultValueDataSource: scope.options.employeesDefaultValueDS != null ? YAHOO.lang.substitute(scope.options.employeesDefaultValueDS, {
                                organization: selectedContractor
                            }) : null,
                            defaultValueUseOnce: scope.options.defaultValueUseOnce,
                            nameSubstituteString: scope.options.employeesNameSubstitute,
                            initialized: false,
                            lazyLoading: false,
                            disabled: false,
                            doNotCheckAccess: scope.options.doNotCheckAccess,
                            additionalFilter: '@lecm\-orgstr\-aspects\:linked\-organization\-assoc\-ref:"' + selectedContractor + '\"'
                        }
                        scope._updateControls(selectedContractor, resetValue, autocompleteConf, treeConf);
                    }
                }
            };

            var resetValue = false,
                marker,
                selectedContractor;

            if (args[1].marker) {
                marker = args[1].marker;
            } else {
                marker = (layer == this.options.contractorSelectEvent) ? 'contractor' : 'organisation';
            }

            if (marker != 'person') {
                var selectedContractors = Object.keys(args[1].selectedItems); // IE 9+
                selectedContractor = selectedContractors.length == 1 ? selectedContractors[0] : null;
            } else {
                selectedContractor = null;
            }

            if (this.previousSelected === selectedContractor) {
                return;
            } else {
                if (this.previousSelected != null) {
                    resetValue = true;  // контагент сменился - прежнее значение неактуально
                }
                this.previousSelected = selectedContractor;
            }
            if (!this.options.disabled) {
                initDatatasources(this, selectedContractor, marker);
            } else {
                if (marker != 'contractor') {
                    var treeControl = LogicECM.CurrentModules[this.options.treeViewJsName];
                    if (treeControl) {
                        this.isCurrentUserFromSelectedOrgUnit(selectedContractor, function(showAssocViewForm){
                            treeControl.options.showAssocViewForm = showAssocViewForm;
                            treeControl.updateFormFields();
                        });
                    }
                }
            }
        },
        isCurrentUserFromSelectedOrgUnit: function(selectedContractor, callback){
            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + '/lecm/orgstructure/api/getCurrentEmployee',
                successCallback: {
                    scope: this,
                    fn: function (response) {
                        if(response.json){
                            var currentEmployeeRef = response.json.nodeRef;
                            Alfresco.util.Ajax.jsonRequest(
                                {
                                    url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                                    method: "POST",
                                    dataObj: {
                                        nodeRef: currentEmployeeRef,
                                        substituteString: "{lecm-orgstr-aspects:linked-organization-assoc-ref}"
                                    },
                                    successCallback: {
                                        scope: this,
                                        fn: function (response) {
                                            var isSameOrg = false;
                                            if (response && response.json.formatString) {
                                                 isSameOrg = selectedContractor && selectedContractor == response.json.formatString
                                            }
                                            if (callback && typeof callback == "function") {
                                                callback.call(this, isSameOrg);
                                            }
                                            return isSameOrg;
                                        }
                                    }
                                });
                        }
                    }
                },
                failureMessage: Alfresco.util.message('message.failure')
            });
        },
        _updateControlsOnDeferred: function() {
            var control = LogicECM.CurrentModules[this.options.autoCompleteJsName];
            var treeControl = LogicECM.CurrentModules[this.options.treeViewJsName];

            if (control && treeControl) {
                var disabled = this.previousSelected == null;
                control.options.disabled = disabled;
                treeControl.options.disabled = disabled;
                control.onReady();
                treeControl.init();
            }
        },

        _updateControls: function (selectedContractor, reset, autocompleteConf, treeConf) {
            var control = LogicECM.CurrentModules[this.options.autoCompleteJsName];
            if (control) {
                if (reset) {
                    control.dataArray = [];
                    control.selectedItems = {};
                    control.defaultValue = null;

                    control.updateSelectedItems();
                    control.updateFormFields();
                    control.updateInputUI();
                    control.setOptions({
                        loadDefault: false,
                        disabled: true
                    });
                    control.onReady();
                }
                control.setOptions({
                    selectedValueNodeRef: "",
                    lazyLoading: (selectedContractor != null)
                });
                
                if (selectedContractor) {
                    if (autocompleteConf) {
                        control.setOptions(autocompleteConf);
                    } else {
                        control.options.disabled = false;
                    }
                    if (!control.options.currentValue && this.options.defaultValue && !this.options.defaultValueLoaded) {
                        control.options.selectedValueNodeRef = this.options.defaultValue;
                    }
                } else {
                    control.options.disabled = true;
                }
                control.onReady();
            }
            var treeControl = LogicECM.CurrentModules[this.options.treeViewJsName];
            if (treeControl) {
                if (reset) {
                    treeControl.selectedItems = null;
                    treeControl.defaultValue = null;
                    treeControl.setOptions({
                        loadDefault: false,
                        disabled: true
                    });
                    treeControl.init();
                }
                treeControl.setOptions({
                    selectedValue: null
                });
                
                if (selectedContractor) {
                    if (treeConf) {
                        treeControl.setOptions(treeConf);
                    } else {
                        treeControl.options.disabled = false;
                    }
                    if (!treeControl.options.currentValue && this.options.defaultValue && !this.options.defaultValueLoaded) {
                        treeControl.options.selectedValue = this.options.defaultValue;
                        this.options.defaultValueLoaded = true;
                    }
                } else {
                    treeControl.options.disabled = true;
                }

                treeControl.init();
            }
        }
    });
})();
