
package ru.it.lecm.typesView.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import ru.it.lecm.base.beans.BaseBean;

/**
 *
 * @author snovikov
 */
public class TypesViewBeanImpl extends BaseBean {

	private DictionaryService dictionaryService;

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	@Override
	public NodeRef getServiceRootFolder() {
		return null;
	}

	public List<Map<String, String>> getAllTypeNames(){
		List<Map<String, String>> typeList = new ArrayList<>();
		Collection<QName> types = dictionaryService.getAllTypes();
		for (QName type : types){
			Map<String, String> typeProps = new HashMap<>();
			ClassDefinition typeDef = dictionaryService.getType(type);
			typeProps.put("name", typeDef.getName().getPrefixString());
			typeProps.put("desc", typeDef.getTitle(dictionaryService));
			typeList.add(typeProps);
		}
		return typeList;
	}

	public List<Map<String, String>> getAllAspectNames(){
		List<Map<String, String>> aspectList = new ArrayList<>();
		Collection<QName> aspects = dictionaryService.getAllAspects();
		for (QName aspect : aspects){
			Map<String, String> aspectProps = new HashMap<>();
			ClassDefinition aspectDef = dictionaryService.getAspect(aspect);
			aspectProps.put("name", aspectDef.getName().getPrefixString());
			aspectProps.put("desc", aspectDef.getTitle(dictionaryService));
			aspectList.add(aspectProps);
		}
		return aspectList;
	}

	private void fillTypePropsMap(QName type, Map<String, Map<String, List<Map<String, String>>>> mapResult, Integer order){
		List<Map<String, String>> properties = new ArrayList<>(); //список пропертей
		Set<QName> aspectNames = new HashSet<>(); //аспекты типа
		List<AspectDefinition> aspectNameDefs = new ArrayList<>(); //аспекты типа (через другой метод)
																  //, так как каждый метод может возвращать не все аспекты
		Set<QName> parentAspectNames = new HashSet<>(); //аспекты родительского типа
		List<AspectDefinition> parentAspectNameDefs = new ArrayList<>(); //аспекты родительского типа (через другой метод)
		ClassDefinition typeDef = dictionaryService.getClass(type);
		QName parentType = typeDef.getParentName();
		ClassDefinition parentTypeDef = null;
		if (null != parentType){
			parentTypeDef = dictionaryService.getClass(parentType);
		}

		//свойства типа (вместе с родительскими)
		Map<QName, PropertyDefinition> propertyMap = typeDef.getProperties();
		aspectNames = typeDef.getDefaultAspectNames();
		aspectNameDefs = typeDef.getDefaultAspects(true);
		for (AspectDefinition aspectNameDef : aspectNameDefs){
			aspectNames.add(aspectNameDef.getName());
		}
		//свойства родительского типа
		Map<QName, PropertyDefinition> parentPropertyMap = null;
		if (null != parentTypeDef){
			parentPropertyMap = parentTypeDef.getProperties();
			parentAspectNames = parentTypeDef.getDefaultAspectNames();
			parentAspectNameDefs = parentTypeDef.getDefaultAspects(true);
			for (AspectDefinition parentAspectNameDef : parentAspectNameDefs){
				parentAspectNames.add(parentAspectNameDef.getName());
			}
		}

		//определим свойства относящиеся только в текущему типу
		Set<Entry<QName, PropertyDefinition>> propertyMapEntry = propertyMap.entrySet();
		//сделаем Set изменяемым, чтобы была возможность убрать свойства родителя
		Set<Entry<QName, PropertyDefinition>> modifPropertyMapEntry = new HashSet<>(propertyMapEntry);
		//добавим в набор свойства из аспектов
		aspectNames.removeAll(parentAspectNames); //приходится так, потому как typeDef.getDefaultAspects()
												 // возвращает не все аспекты
		for (QName aspectName : aspectNames){
			AspectDefinition aspectDef = dictionaryService.getAspect(aspectName);
			Map<QName, PropertyDefinition> aspectPropMap = aspectDef.getProperties();
			modifPropertyMapEntry.addAll(aspectPropMap.entrySet());
		}

		if (null != parentPropertyMap && !parentPropertyMap.isEmpty()){
			Set<Entry<QName, PropertyDefinition>> parentPropertyMapEntry = parentPropertyMap.entrySet();
			modifPropertyMapEntry.removeAll(parentPropertyMapEntry);
		}


		for (Entry property : modifPropertyMapEntry){
			QName propQName = (QName) property.getKey();
			PropertyDefinition propDef = (PropertyDefinition) property.getValue();

			Map<String, String> props = new HashMap<>();
			props.put("name", propQName.getPrefixString());
			props.put("type", propDef.getDataType().getName().getPrefixString());
			props.put("default", propDef.getDefaultValue());
			props.put("mandatory", propDef.isMandatory() ? "true" : "false" );
			props.put("indexed", propDef.isIndexed() ? "true" : "false" );
			props.put("desc", propDef.getTitle(dictionaryService) );
			properties.add(props);
		}

		Map<String, List<Map<String, String>>> map = new HashMap<>();

		Map<String, String> typeMetaMap= new HashMap<>();
		typeMetaMap.put("name", type.getPrefixString());
		typeMetaMap.put("desc", typeDef.getTitle(dictionaryService));
		List<Map<String, String>> typeMetaList = new ArrayList<>();
		typeMetaList.add(typeMetaMap);

		map.put("content", properties);
		map.put("meta", typeMetaList);

		mapResult.put(order.toString(), map);
		if (null != parentType){
			fillTypePropsMap(parentType, mapResult, order + 1);
		}
	}

	/**
     * Получает иерархию свойств  начиная с исходного типа и заканчивая его первым родителем
     * @param typeName - тип документа
     * @return
     */
	public Map<String, Map<String, List<Map<String, String>>>> getTypePropsInfoHierarchy(String typeName){
		Map<String, Map<String, List<Map<String, String>>>> hierarchy = new HashMap<>();
		NamespacePrefixResolver namespacePrefixResolver = serviceRegistry.getNamespaceService();
		QName type = QName.createQName(typeName, namespacePrefixResolver);
		fillTypePropsMap(type, hierarchy, 0);
		return hierarchy;
	}

	private void fillTypeAssocsMap(QName type, Map<String, Map<String, List<Map<String, String>>>> mapResult, Integer order){
		List<Map<String, String>> associations = new ArrayList<>(); //список пропертей
		Set<QName> aspectNames = new HashSet<>(); //аспекты типа
		List<AspectDefinition> aspectNameDefs = new ArrayList<>(); //аспекты типа (через другой метод)
														  //, так как каждый метод может возвращать не все аспекты
		Set<QName> parentAspectNames = new HashSet<>(); //аспекты родительского типа
		List<AspectDefinition> parentAspectNameDefs = new ArrayList<>(); //аспекты родительского типа (через другой метод)
		ClassDefinition typeDef = dictionaryService.getClass(type);
		QName parentType = typeDef.getParentName();
		ClassDefinition parentTypeDef = null;
		if (null != parentType){
			parentTypeDef = dictionaryService.getClass(parentType);
		}

		//свойства типа (вместе с родительскими)
		Map<QName, AssociationDefinition> assocMap = typeDef.getAssociations();
		aspectNames = typeDef.getDefaultAspectNames();
		aspectNameDefs = typeDef.getDefaultAspects(true);
		for (AspectDefinition aspectNameDef : aspectNameDefs){
			aspectNames.add(aspectNameDef.getName());
		}
		//свойства родительского типа
		Map<QName, AssociationDefinition> parentAssocMap = null;
		if (null != parentTypeDef){
			parentAssocMap = parentTypeDef.getAssociations();
			parentAspectNames = parentTypeDef.getDefaultAspectNames();
			parentAspectNameDefs = parentTypeDef.getDefaultAspects(true);
			for (AspectDefinition parentAspectNameDef : parentAspectNameDefs){
				parentAspectNames.add(parentAspectNameDef.getName());
			}
		}

		//определим свойства относящиеся только в текущему типу
		Set<Entry<QName, AssociationDefinition>> assocMapEntry = assocMap.entrySet();
		//сделаем Set изменяемым, чтобы была возможность убрать свойства родителя
		Set<Entry<QName, AssociationDefinition>> modifAssocMapEntry = new HashSet<>(assocMapEntry);
		//добавим в набор свойства из аспектов
		aspectNames.removeAll(parentAspectNames); //приходится так, потому как typeDef.getDefaultAspects()
												 // возвращает не все аспекты
		for (QName aspectName : aspectNames){
			AspectDefinition aspectDef = dictionaryService.getAspect(aspectName);
			Map<QName, AssociationDefinition> aspectPropMap = aspectDef.getAssociations();
			modifAssocMapEntry.addAll(aspectPropMap.entrySet());
		}

		if (null != parentAssocMap && !parentAssocMap.isEmpty()){
			Set<Entry<QName, AssociationDefinition>> parentAssocMapEntry = parentAssocMap.entrySet();
			modifAssocMapEntry.removeAll(parentAssocMapEntry);
		}


		for (Entry association : modifAssocMapEntry){
			QName assocQName = (QName) association.getKey();
			AssociationDefinition assocDef = (AssociationDefinition) association.getValue();

			Map<String, String> props = new HashMap<>();
			props.put("name", assocQName.getPrefixString());
			props.put("target", assocDef.getTargetClass().getName().getPrefixString());
			props.put("targetMandatory", assocDef.isTargetMandatory() ? "true" : "false");
			props.put("targetMany", assocDef.isTargetMany() ? "true" : "false");
			props.put("child", assocDef.isChild() ? "true" : "false");
			props.put("targetDesc", assocDef.getTargetClass().getTitle(dictionaryService) );
			props.put("desc", assocDef.getTitle(dictionaryService) );
			associations.add(props);
		}

		Map<String, List<Map<String, String>>> map = new HashMap<>();

		Map<String, String> typeMetaMap= new HashMap<>();
		typeMetaMap.put("name", type.getPrefixString());
		typeMetaMap.put("desc", typeDef.getTitle(dictionaryService));
		List<Map<String, String>> typeMetaList = new ArrayList<>();
		typeMetaList.add(typeMetaMap);

		map.put("content", associations);
		map.put("meta", typeMetaList);

		mapResult.put(order.toString(), map);
		if (null != parentType){
			fillTypeAssocsMap(parentType, mapResult, order + 1);
		}
	}

	public Map<String, Map<String, List<Map<String, String>>>> getTypeAssocsInfoHierarchy(String typeName){
		Map<String, Map<String, List<Map<String, String>>>> hierarchy = new HashMap<>();
		NamespacePrefixResolver namespacePrefixResolver = serviceRegistry.getNamespaceService();
		QName type = QName.createQName(typeName, namespacePrefixResolver);
		fillTypeAssocsMap(type, hierarchy, 0);
		return hierarchy;
	}


	private void fillTypeAspectsMap(QName type, Map<String, Map<String, List<Map<String, String>>>> mapResult, Integer order){
		List<Map<String, String>> aspects = new ArrayList<>();

		ClassDefinition typeDef = dictionaryService.getClass(type);
		QName parentType = typeDef.getParentName();
		ClassDefinition parentTypeDef = null;
		if (null != parentType){
			parentTypeDef = dictionaryService.getClass(parentType);
		}

		Set<QName> aspectNames = typeDef.getDefaultAspectNames();
		List<AspectDefinition> aspectNameDefs = typeDef.getDefaultAspects(true); //аспекты типа (через другой метод)
														  //, так как каждый метод может возвращать не все аспекты
		for (AspectDefinition aspectNameDef : aspectNameDefs){
			aspectNames.add(aspectNameDef.getName());
		}
		Set<QName> parentAspectNames = new HashSet<>();
		if (null != parentTypeDef){
			parentAspectNames = parentTypeDef.getDefaultAspectNames();
			List<AspectDefinition> parentAspectNameDefs = parentTypeDef.getDefaultAspects(true);
			for (AspectDefinition parentAspectNameDef : parentAspectNameDefs){
				parentAspectNames.add(parentAspectNameDef.getName());
			}
		}
		aspectNames.removeAll(parentAspectNames);
		for (QName aspectName : aspectNames){
			AspectDefinition aspectDef = dictionaryService.getAspect(aspectName);
			Map<String, String> props = new HashMap<>();
			props.put("name", aspectDef.getName().getPrefixString());
			props.put("desc", aspectDef.getTitle(dictionaryService));
			aspects.add(props);
		}

		Map<String, List<Map<String, String>>> map = new HashMap<>();

		Map<String, String> typeMetaMap= new HashMap<>();
		typeMetaMap.put("name", type.getPrefixString());
		typeMetaMap.put("desc", typeDef.getTitle(dictionaryService));
		List<Map<String, String>> typeMetaList = new ArrayList<>();
		typeMetaList.add(typeMetaMap);

		map.put("content", aspects);
		map.put("meta", typeMetaList);

		mapResult.put(order.toString(), map);
		if (null != parentType){
			fillTypeAspectsMap(parentType, mapResult, order + 1);
		}
	}

	public Map<String, Map<String, List<Map<String, String>>>> getTypeAspectsInfoHierarchy(String typeName){
		Map<String, Map<String, List<Map<String, String>>>> hierarchy = new HashMap<>();
		NamespacePrefixResolver namespacePrefixResolver = serviceRegistry.getNamespaceService();
		QName type = QName.createQName(typeName, namespacePrefixResolver);
		fillTypeAspectsMap(type, hierarchy, 0);
		return hierarchy;
	}

}
