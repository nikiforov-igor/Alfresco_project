/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
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

        YAHOO.Bubbling.on(contractorSelectEvent, this.onContractorSelect, this);
        YAHOO.Bubbling.on(organizationSelectEvent, this.onOrganizationSelect, this);

        this.previousSelected = null;

        return this;
    };

    YAHOO.extend(LogicECM.module.DoubleSelectRepresentativeForContractor, Alfresco.component.Base, {
        previousSelectedContr: null,
        previousSelectedOrg: null,

        options: {
            employeesByOrgDS: "lecm/employees/byOrg/{organization}/picker",
            representativesByContrDS: "lecm/representatives/{contractor}/picker",

            employeesType: "lecm-orgstr:employee",
            representativesType: "lecm-representative:representative-type",

            employeesNameSubstitute: "{lecm-orgstr:employee-short-name}",
            representativesSubstitute: "{cm:name}",

            employeesLocation: null,
            representativesLocation: null,

            employeesDefaultValueDS: null,
            representativesDefaultValueDS: null,

            disabled: false,

            currentValue: '',

            autoCompleteJsName: "select-representative-autoComplete",
            treeViewJsName:"select-representative-treeView"
        },

        onReady: function SelectRepresentativeForContractor_onReady() {
        },

        onContractorSelect: function (layer, args) {
            if (!this.options.disabled) {
                var selectedContractors = Object.keys(args[1].selectedItems); // IE 9+
                var selectedContractor = selectedContractors.length > 0? selectedContractors[0] : null;

                var resetValue = false;
                if (this.previousSelectedContr === selectedContractor) {
                    return;
                } else {
                    if (this.previousSelectedOrg != null || this.previousSelectedContr != null) {
                        resetValue = true;  // контагент сменился - прежнее значение неактуально
                    }
                    this.previousSelectedContr = selectedContractor;
                    this.previousSelectedOrg = null;
                }

                this._updateControls(selectedContractor, resetValue, {
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
                        currentValue: resetValue ? "" : this.options.currentValue,
                        selectedValueNodeRef:"",
                        lazyLoading: false
                    },
                    {
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
                        currentValue: resetValue ? "" : this.options.currentValue,
                        selectedValue:null,
                        initialized:false,
                        lazyLoading: false
                    }
                );
            }
        },

        _updateControls: function (selectedContractor, reset, autocompleteConf, treeConf) {
            var control = LogicECM.CurrentModules[this.options.autoCompleteJsName];
            if (control != null) {
                if (reset) {
                    this.options.currentValue = "";
                    control.dataArray = [];
                    control.selectedItems = {};
                    control.defaultValue = null;

                    control.updateSelectedItems();
                    control.updateFormFields();
                    control.updateInputUI();
                }
                control.setOptions({
                    currentValue: this.options.currentValue,
                    selectedValueNodeRef: "",
                    lazyLoading: (selectedContractor != null)
                });
                if (selectedContractor != null) {
                    control.setOptions(autocompleteConf);
                    control.onReady();
                }
            }
            var treeControl = LogicECM.CurrentModules[this.options.treeViewJsName];
            if (treeControl != null) {
                if (reset) {
                    this.options.currentValue = "";
                    treeControl.selectedItems = null;
                    treeControl.defaultValue = null;
                }
                treeControl.setOptions({
                    currentValue: this.options.currentValue,
                    selectedValue: null
                });
                if (selectedContractor != null) {
                    treeControl.setOptions(treeConf);
                    treeControl.init();
                }
            }
        },

        onOrganizationSelect: function (layer, args) {
            if(!this.options.disabled) {
                var selectedContractors = Object.keys(args[1].selectedItems); // IE 9+
                var selectedContractor = selectedContractors.length > 0? selectedContractors[0] : null;

                var resetValue = false;

                if (this.previousSelectedOrg === selectedContractor) {// выбрали того же - пропускаем
                    return;
                } else {
                    if (this.previousSelectedOrg != null || this.previousSelectedContr != null) { // контагент сменился - прежнее значение неактуально
                        resetValue = true;
                    }
                    this.previousSelectedOrg = selectedContractor;
                    this.previousSelectedContr = null;
                }

                this._updateControls(selectedContractor, resetValue, {
                        startLocation: this.options.employeesLocation,
                        itemType: this.options.employeesType,
                        childrenDataSource: YAHOO.lang.substitute(this.options.employeesByOrgDS,
                            {
                                organization: selectedContractor != null ? new Alfresco.util.NodeRef(selectedContractor).uri : ""
                            }),
                        defaultValueDataSource: this.options.employeesDefaultValueDS != null ? YAHOO.lang.substitute(this.options.employeesDefaultValueDS,
                            {
                                organization: selectedContractor
                            }) : null,
                        nameSubstituteString: this.options.employeesNameSubstitute,
                        lazyLoading: false
                    },
                    {
                        rootLocation: this.options.employeesLocation,
                        itemType: this.options.employeesType,
                        childrenDataSource: YAHOO.lang.substitute(this.options.employeesByOrgDS,
                            {
                                organization: selectedContractor != null ? new Alfresco.util.NodeRef(selectedContractor).uri : ""
                            }),
                        defaultValueDataSource: this.options.employeesDefaultValueDS != null ? YAHOO.lang.substitute(this.options.employeesDefaultValueDS,
                            {
                                organization: selectedContractor
                            }) : null,
                        nameSubstituteString: this.options.employeesNameSubstitute,
                        initialized: false,
                        lazyLoading: false
                    }
                );
            }
        }
    });
})();
