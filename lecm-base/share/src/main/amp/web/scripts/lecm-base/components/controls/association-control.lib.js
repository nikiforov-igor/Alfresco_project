/* global Alfresco, YAHOO */

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.AssociationComplexControl = LogicECM.module.AssociationComplexControl || {};

(function () {
	var BaseUtil = LogicECM.module.Base.Util;

	LogicECM.module.AssociationComplexControl.Utils = {

		generateChildrenUrlParams: function (options, searchTerm, skipItemsCount, forAutocomplete, exSearchFilter) {
			/* построение параметров для запроса данных датагрида */
			var additionalFilter = options.additionalFilter,
				allowedNodesFilter,
				ignoreNodesFilter,
				notSingleQueryPattern = /^NOT[\s]+.*(?=\sOR\s|\sAND\s|\s\+|\s\-)/i,
				singleNotQuery;

			if (options.allowedNodes) {
				allowedNodesFilter = options.allowedNodes.reduce(function (prev, curr) {
					return prev + (prev.length ? ' OR ' : '') + 'ID:"' + curr + '"';
				}, options.allowedNodes.length ? '' : '(ISNULL:"sys:node-dbid" OR NOT EXISTS:"sys:node-dbid")');

				if (additionalFilter) {
					singleNotQuery = additionalFilter.indexOf("NOT") == 0 && !notSingleQueryPattern.test(additionalFilter);
					additionalFilter = (!singleNotQuery ? "(" : "") + additionalFilter + (!singleNotQuery ? ")" : "") + " AND (" + allowedNodesFilter + ")";
				} else {
					additionalFilter = allowedNodesFilter;
				}
			}

			if (options.ignoreNodes && options.ignoreNodes.length) {
				ignoreNodesFilter = options.ignoreNodes.reduce(function (prev, curr) {
					return prev + ' AND NOT ID:"' + curr + '"';
				}, '(ISNOTNULL:\"cm:name\" AND  @cm\\:name:\"?*\")');

				if (additionalFilter) {
					singleNotQuery = additionalFilter.indexOf("NOT") == 0 && !notSingleQueryPattern.test(additionalFilter);
					additionalFilter = (!singleNotQuery ? "(" : "") + additionalFilter + (!singleNotQuery ? ")" : "") + " AND (" + ignoreNodesFilter + ")";
				} else {
					additionalFilter = ignoreNodesFilter;
				}
			}
			if (exSearchFilter && exSearchFilter.length) {
				if (additionalFilter) {
					singleNotQuery = additionalFilter.indexOf("NOT") == 0 && !notSingleQueryPattern.test(additionalFilter);
					additionalFilter = (!singleNotQuery ? "(" : "") + additionalFilter + (!singleNotQuery ? ")" : "") + " AND (" + exSearchFilter + ")";
				} else {
					additionalFilter = exSearchFilter;
				}
			}
			return Alfresco.util.toQueryString({
				selectableType: options.itemType,
				searchTerm: searchTerm ? searchTerm : '',
				size: LogicECM.module.AssociationComplexControl.Utils.getMaxSearchResult(options, forAutocomplete),
				nameSubstituteString: options.nameSubstituteString,
				sortProp: options.sortProp,
				selectedItemsNameSubstituteString: LogicECM.module.AssociationComplexControl.Utils.getSelectedItemsNameSubstituteString(options),
				additionalFilter: additionalFilter,
				pathRoot: options.rootLocation,
				pathNameSubstituteString: options.treeNodeSubstituteString,
				onlyInSameOrg: (!!options.useStrictFilterByOrg).toString(),
				doNotCheckAccess: (!!options.doNotCheckAccess).toString(),
				useObjectDescription: (!!options.useObjectDescription).toString(),
				rootNodeRef: options.rootNodeRef,
				xpath: forAutocomplete ? options.rootLocation : undefined,
				skipCount: forAutocomplete ? undefined : skipItemsCount.toString()
			});
		},

		getMaxSearchResult: function (options, forAutocomplete) {
			/* определение кол-ва возвращаемых данных за раз */
			var maxSearchResult = 10;
			if (forAutocomplete) {
				maxSearchResult = options.maxSearchAutocompleteResults;
			} else if (options.showSearch && options.plane && options.maxSearchResultsWithSearch) {
				maxSearchResult = options.maxSearchResultsWithSearch;
			} else if (options.maxSearchResults) {
				maxSearchResult = options.maxSearchResults;
			}
			return maxSearchResult.toString();
		},

		getSelectedItemsNameSubstituteString: function (options) {
			/* определение откуда брать форматную строку */
			return options.selectedItemsNameSubstituteString ? options.selectedItemsNameSubstituteString : options.nameSubstituteString;
		},

		canItemBeSelected: function (id, options, selected, parentControl) {
			var canSelect = true, i;
			if (options.endpointMany && parentControl.options.multipleSelectMode) {
				canSelect = !selected.hasOwnProperty(id);
			} else {
                if (!parentControl.options.isComplex) {
                    canSelect = Object.keys(selected).length === 0;
                } else {
                    //проверяем все пикеры, чтобы учесть все выбранные элементы
                    for (i = 0; canSelect && (i < parentControl.options.itemsOptions.length); i++) {
                        canSelect = Object.keys(parentControl.widgets[parentControl.options.itemsOptions[i].itemKey].currentState.temporarySelected).length === 0;
                    }
                }
			}
			return canSelect;
		},

		getEmployeeAbsenceMarkeredHTML: function (nodeRef, displayName, showLinkTitle, employeeAbsenceMarker, employeesAvailabilityInformation) {
			var result = '',
				employeeData,
				absenceEnd,
				title = '',
				nextAbsenceStr,
				nextAbsenceDate;
			if (employeeAbsenceMarker && employeesAvailabilityInformation) {
				employeeData = employeesAvailabilityInformation[nodeRef];
				if (employeeData) {
					if (employeeData.isEmployeeAbsent) {
						absenceEnd = Alfresco.util.fromISO8601(employeeData.currentAbsenceEnd);
						result = BaseUtil.getControlMarkeredEmployeeView(nodeRef, displayName, showLinkTitle, 'employee-unavailable', 'Будет доступен с ' + leadingZero(absenceEnd.getDate()) + '.' + leadingZero(absenceEnd.getMonth() + 1) + '.' + absenceEnd.getFullYear());
					} else {
						nextAbsenceStr = employeeData.nextAbsenceStart;
						if (nextAbsenceStr) {
							nextAbsenceDate = Alfresco.util.fromISO8601(nextAbsenceStr);
							title = 'Будет недоступен с ' + leadingZero(nextAbsenceDate.getDate()) + '.' + leadingZero(nextAbsenceDate.getMonth() + 1) + '.' + nextAbsenceDate.getFullYear();
						}
						result = BaseUtil.getControlMarkeredEmployeeView(nodeRef, displayName, showLinkTitle, 'employee-available', title);
					}
				} else {
					result = BaseUtil.getControlEmployeeView(nodeRef, displayName, showLinkTitle);
				}
			} else {
				result = BaseUtil.getControlEmployeeView(nodeRef, displayName, showLinkTitle);
			}
			return result;

			function leadingZero(value) {
				var valueStr = value + '';
				if (valueStr.length == 1) {
					return '0' + valueStr;
				} else {
					return valueStr;
				}
			}
		},

		getRemoveButtonHTML: function (parentId, node) {
			var id = node.nodeRef.replace(/:|\//g, '_');
			return BaseUtil.getControlItemRemoveButtonHTML(parentId + '-' + id);
		},

		getDefaultView: function (options, displayValue, item) {
			var titleName = (options.plane || !options.showPath) ? item.selectedName : item.path + item.selectedName;
			var title = (options.showAssocViewForm && item.nodeRef != null) ? Alfresco.util.message('title.click.for.extend.info') : titleName;
			var result = '<span class="not-person" title="' + title + '">';
			if (options.showAssocViewForm && item.nodeRef != null) {
				result += "<a href='javascript:void(0);' " + " onclick=\"LogicECM.module.Base.Util.viewAttributes({itemId:\'" + item.nodeRef + "\', title: \'logicecm.view\'})\">" + displayValue + "</a>";
			} else {
				result += displayValue;
			}
			result += '</span>';

			return result;
		},
		
		getItemKeys: function (itemsOptions) {
			return itemsOptions.map(function (item) {
				return item.itemKey;
			});
		},
		
		sortByIndex: function (a, b) {
			return a.index - b.index;
		},

		sortByName: function (a, b) {
			return a.selectedName.localeCompare(b.selectedName);
		},

		getQueryFromForm: function (currentForm) {
			var exSearchFilter = '',
				propNamePrefix = '@',
				first = true;

			for (var i = 0; i < currentForm.elements.length; i++) {
				var element = currentForm.elements[i],
					propName = element.name,
					propValue = YAHOO.lang.trim(element.value);

				if (propName && propValue && propValue.length) {
					if (propName.indexOf("prop_") == 0) {
						propName = propName.substr(5);
						if (propName.indexOf("_") !== -1) {
							propName = propName.replace("_", ":");
							if (propName.match("-range$") == "-range") {
								var from, to, sepindex = propValue.indexOf("|");
								if (propName.match("-date-range$") == "-date-range") {
									propName = propName.substr(0, propName.length - "-date-range".length);
									from = (sepindex === 0 ? "MIN" : propValue.substr(0, 10));
									to = (sepindex === propValue.length - 1 ? "MAX" : propValue.substr(sepindex + 1, 10));
								} else {
									propName = propName.substr(0, propName.length - "-number-range".length);
									from = (sepindex === 0 ? "MIN" : propValue.substr(0, sepindex));
									to = (sepindex === propValue.length - 1 ? "MAX" : propValue.substr(sepindex + 1));
								}
								exSearchFilter += (first ? '' : ' AND ') + propNamePrefix + this.escape(propName) + ':"' + from + '".."' + to + '"';
								first = false;
							} else {
								exSearchFilter += (first ? '' : ' AND ') + propNamePrefix + this.escape(propName) + ':' + this.applySearchSettingsToTerm(this.escape(propValue), 'MATCHES');
								first = false;
							}
						}
					} else if (propName.indexOf("assoc_") == 0) {
						var assocName = propName.substring(6);
						if (assocName.indexOf("_") !== -1) {
							assocName = assocName.replace("_", ":") + "-ref";
							exSearchFilter += (first ? '(' : ' AND (');
							var assocValues = propValue.split(",");
							var firstAssoc = true;
							for (var k = 0; k < assocValues.length; k++) {
								var assocValue = assocValues[k];
								if (!firstAssoc) {
									exSearchFilter += " OR ";
								}
								exSearchFilter += this.escape(assocName) + ':"' + this.applySearchSettingsToTerm(this.escape(assocValue), 'CONTAINS') + '"';
								firstAssoc = false;
							}
							exSearchFilter += ") ";
							first = false;
						}
					}
				}
			}
			return exSearchFilter;
		}
	};
})();
