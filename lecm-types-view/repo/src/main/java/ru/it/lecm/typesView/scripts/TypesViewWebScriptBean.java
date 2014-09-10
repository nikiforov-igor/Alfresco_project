
package ru.it.lecm.typesView.scripts;

import java.util.List;
import java.util.Map;
import ru.it.lecm.base.beans.BaseWebScript;
import ru.it.lecm.typesView.beans.TypesViewBeanImpl;

/**
 *
 * @author snovikov
 */
public class TypesViewWebScriptBean extends BaseWebScript{
	private TypesViewBeanImpl typesViewService;

	public void setTypesViewService(TypesViewBeanImpl typesViewService){
		this.typesViewService = typesViewService;
	}

	public List<Map<String, String>> getAllTypeNames(){
		return typesViewService.getAllTypeNames();
	}

	public Map<String, Map<String, List<Map<String, String>>>> getTypePropsInfoHierarchy(String typeName){
		return typesViewService.getTypePropsInfoHierarchy(typeName);
	}

	public Map<String, Map<String, List<Map<String, String>>>> getTypeAspectsInfoHierarchy(String typeName){
		return typesViewService.getTypeAspectsInfoHierarchy(typeName);
	}

	public Map<String, Map<String, List<Map<String, String>>>> getTypeAssocsInfoHierarchy(String typeName){
		return typesViewService.getTypeAssocsInfoHierarchy(typeName);
	}

}
