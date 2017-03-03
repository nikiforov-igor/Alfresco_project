<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/data/surf-doclist.lib.js">

var DocumentUtils = {
	getRootNode: function getRootNode() {
		var rootNode = "alfresco://company/home",
			repoConfig = config.scoped["RepositoryLibrary"]["root-node"];

		if (repoConfig !== null) {
			rootNode = repoConfig.value;
		}
		return rootNode;
	},
	getNodeDetails: function getNodeDetails(nodeRef, site, options) {
		if (nodeRef) {
			var url = '/slingshot/doclib2/node/' + nodeRef.replace('://', '/');
			if (!site) {
				// Repository mode
				url += "?libraryRoot=" + encodeURIComponent(DocumentUtils.getRootNode());
			}
			var result = remote.connect("alfresco").get(url);

			if (result.status == 200) {
				var details = eval('(' + result + ')');
				if (details && (details.item || details.items)) {
					DocList.processResult(details, options);
					return details;
				}
			}
		}
		return null;
	},
	getNodeAccess: function getNodeAccess(nodeRef, login) {
		var url = "/lecm/document/utils/access?nodeRef=" + nodeRef + "&user=" + encodeURI(login);
		if (!nodeRef) {
			return null;
		}
		var result = remote.connect("alfresco").get(url);

		if (result.status == 200) {
			return eval('(' + result + ')');
		} else {
			return null;
		}
	},
	getDependencies: function getDependencies(configFamily) {
		var fnGetConfig = function fnGetConfig(scopedRoot, dependencyType) {
			var dependencies = [], src, configs, dependencyConfig;
			try {
				configs = scopedRoot.getChildren(dependencyType);
				if (configs) {
					for (var i = 0; i < configs.size(); i++) {
						dependencyConfig = configs.get(i);
						if (dependencyConfig) {
							// Get src attribute from each config item
							src = dependencyConfig.attributes["src"];
							if (src) {
								dependencies.push(src.toString());
	}
						}
					}
				}
			} catch (e) {
			}
			return dependencies;
		}

		var scopedRoot = config.scoped[configFamily]["dependencies"];
		return (
		{
			css: fnGetConfig(scopedRoot, "css"),
			js: fnGetConfig(scopedRoot, "js")
		});
	}

};
