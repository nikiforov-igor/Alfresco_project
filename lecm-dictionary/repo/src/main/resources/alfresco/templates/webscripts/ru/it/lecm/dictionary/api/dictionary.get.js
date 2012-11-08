var dictionaryName = args["dicName"];

var dictionaries = search.xpathSearch("/app:company_home/lecm-dic:Dictionary/lecm-dic:" + xpathEncode(dictionaryName));
if (dictionaries.length > 0)
{
	var dictionary = dictionaries[0];
}

model.dictionary = dictionary;

function xpathEncode(xpath) {
	var result = xpath;
	result = result.replace(" ", "_x0020_");
	return result;
}