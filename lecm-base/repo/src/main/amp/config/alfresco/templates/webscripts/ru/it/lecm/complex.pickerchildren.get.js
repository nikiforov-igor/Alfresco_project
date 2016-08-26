<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

function main() {
	args['selectableType'] = 'lecm-contractor:contractor-type,lecm-contractor:physical-person-type';
	args['nameSubstituteString'] = '{lecm-contractor:shortname}';
	args['nameSubstituteString'] = '{lecm-contractor:shortname}';
	args['titleNameSubstituteString'] = '{lecm-contractor:shortname}';
	args['pathRoot'] = '/app:company_home/cm:Business_x0020_platform/cm:LECM/cm:Сервис_x0020_Справочники';
	args['xpath'] = '/app:company_home/cm:Business_x0020_platform/cm:LECM/cm:Сервис_x0020_Справочники';
	var data = getPickerChildrenItems('ISNOTNULL:"sys:node-dbid"');
	var result;
	if (data.results && data.results.length) {
		for each(result in data.results) {
			switch ('' + result.item.typeShort) {
				case 'lecm-contractor:contractor-type':
					result.selectedVisibleName = substitude.formatNodeTitle(result.item.nodeRef, '{lecm-contractor:shortname}<div class="control-small-font">{lecm-contractor:postal-code}, {lecm-contractor:physical-address}</div>');
					break;
				case 'lecm-contractor:physical-person-type':
					result.selectedVisibleName = substitude.formatNodeTitle(result.item.nodeRef, '{lecm-contractor:fullname}<div class="control-small-font">{lecm-contractor:postal-code}, {lecm-contractor:physical-address}</div>');
					break;
			}
		}
	}

	model.parent = data.parent;
	model.rootNode = data.rootNode;
	model.results = data.results;
	model.additionalProperties = data.additionalProperties;
}

main();
