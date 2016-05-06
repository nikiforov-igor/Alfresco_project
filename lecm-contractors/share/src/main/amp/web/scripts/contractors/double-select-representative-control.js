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
            obj: {
                selectedContractor: null,
                reset: false,
                autocompleteConf: {},
                treeConf: {}
            },
            fn: function (oParams) {
                this._updateControls(oParams.selectedContractor, oParams.reset, oParams.autocompleteConf, oParams.treeConf);
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

            autoCompleteJsName: "select-representative-autoComplete",
            treeViewJsName: "select-representative-treeView",
            fieldHtmlId: "",
            showAssocViewForm: false,
            markers: null
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
            if (!this.options.disabled) {
                var selectedContractors = Object.keys(args[1].selectedItems); // IE 9+
                var selectedContractor = selectedContractors.length == 1 ? selectedContractors[0] : null;

                var resetValue = false;

                if (this.previousSelected === selectedContractor) {
                    return;
                } else {
                    if (this.previousSelected != null) {
                        resetValue = true;  // контагент сменился - прежнее значение неактуально
                    }
                    this.previousSelected = selectedContractor;
                }

                var autocompleteConf, treeConf, marker;
                
                if (args[1].markers && args[1].markers[selectedContractor]) {
                    marker = args[1].markers[selectedContractor]
                } else {
                    marker = layer == this.options.contractorSelectEvent ? 'contractor' : 'organisation'; 
                }

                if (marker == 'contractor') {

                    autocompleteConf = {
                        startLocation: this.options.representativesLocation,
                        itemType: this.options.representativesType,
                        childrenDataSource: YAHOO.lang.substitute(this.options.representativesByContrDS,
                            {
                                contractor: selectedContractor != null ? new Alfresco.util.NodeRef(selectedContractor).uri : ""
                            }),
                        defaultValueDataSource: YAHOO.lang.substitute(this.options.representativesDefaultValueDS,
                            {
                                contractor: selectedContractor
                            }),
                        nameSubstituteString: this.options.representativesSubstitute,
                        selectedValueNodeRef: "",
                        lazyLoading: false,
                        disabled: false,
                        showAssocViewForm: this.options.showAssocViewForm
                    };

                    treeConf = {
                        rootLocation: this.options.representativesLocation,
                        itemType: this.options.representativesType,
                        childrenDataSource: YAHOO.lang.substitute(this.options.representativesByContrDS,
                            {
                                contractor: selectedContractor != null ? new Alfresco.util.NodeRef(selectedContractor).uri : ""
                            }),
                        defaultValueDataSource: YAHOO.lang.substitute(this.options.representativesDefaultValueDS,
                            {
                                contractor: selectedContractor
                            }),
                        nameSubstituteString: this.options.representativesSubstitute,
                        selectedValue: null,
                        initialized: false,
                        lazyLoading: false,
                        disabled: false
                    }
                } else {
                    autocompleteConf = {
                        startLocation: this.options.employeesLocation,
                        itemType: this.options.employeesType,
                        childrenDataSource: YAHOO.lang.substitute(this.options.employeesByOrgDS, {
                            organization: selectedContractor != null ? new Alfresco.util.NodeRef(selectedContractor).uri : ""
                        }),
                        defaultValueDataSource: this.options.employeesDefaultValueDS != null ? YAHOO.lang.substitute(this.options.employeesDefaultValueDS, {
                            organization: selectedContractor
                        }) : null,
                        nameSubstituteString: this.options.employeesNameSubstitute,
                        lazyLoading: false,
                        disabled: false,
                        showAssocViewForm: this.options.showAssocViewForm
                    };

                    treeConf = {
                        rootLocation: this.options.employeesLocation,
                        itemType: this.options.employeesType,
                        childrenDataSource: YAHOO.lang.substitute(this.options.employeesByOrgDS, {
                            organization: selectedContractor != null ? new Alfresco.util.NodeRef(selectedContractor).uri : ""
                        }),
                        defaultValueDataSource: this.options.employeesDefaultValueDS != null ? YAHOO.lang.substitute(this.options.employeesDefaultValueDS, {
                            organization: selectedContractor
                        }) : null,
                        nameSubstituteString: this.options.employeesNameSubstitute,
                        initialized: false,
                        lazyLoading: false,
                        disabled: false
                    }
                }

                this._updateControls(selectedContractor, resetValue, autocompleteConf, treeConf);
            }
        },

        _updateControls: function (selectedContractor, reset, autocompleteConf, treeConf) {
            var control = LogicECM.CurrentModules[this.options.autoCompleteJsName];
            if (control != null) {
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
                if (selectedContractor != null) {
                    control.setOptions(autocompleteConf);
                    control.onReady();
                } else if (!control.selectedItems || Object.keys(control.selectedItems).length == 0) {
                    control.onDisableControl(null, [null, {fieldId: control.options.fieldId, formId: control.options.formId}]);
                }
            }
            var treeControl = LogicECM.CurrentModules[this.options.treeViewJsName];
            if (treeControl != null) {
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
                if (selectedContractor != null && treeConf) {
                    treeControl.setOptions(treeConf);
                    treeControl.init();
                } else if (!treeControl.selectedItems || Object.keys(treeControl.selectedItems).length == 0) {
                    treeControl.onDisableControl(null, [null, {fieldId: treeControl.options.fieldId, formId: treeControl.options.formId}]);
                }
            }
        }
    });
})();
