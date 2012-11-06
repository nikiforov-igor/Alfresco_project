var dictionaryName = args["dicName"];

var dictionaries = search.xpathSearch("/app:company_home/lecm-dic:Dictionary/lecm-dic:" + dictionaryName);
if (dictionaries.length > 0)
{
	var dictionary = dictionaries[0];
}

model.dictionary = dictionary;