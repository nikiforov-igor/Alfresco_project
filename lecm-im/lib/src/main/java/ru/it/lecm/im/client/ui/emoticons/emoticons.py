#!/usr/bin/python
import plistlib

pl = plistlib.readPlist('Emoticons.plist')
emoticons = pl['Emoticons']

print emoticons

print 'add code section start.........................................................'
print '\n'
for name in emoticons:
    texts = "new String[]{"
    textlist = []
    for text in emoticons[name]['Equivalents']:
        textlist.append("\""+text+"\"")
    texts = texts+",".join(textlist)
    texts = texts+"}"
    iconName = emoticons[name]['Name']
    output =  "emoticons.add(new Emoticon(\"%s\",\"%s\",%s));" % (iconName,name,texts)
    print output
    
print '\n'
print 'add code section end.........................................................'


print 'preFormatEmoticons code section start.........................................................'
for name in emoticons:
    texts = "new String[]{"
    textlist = []
    for text in emoticons[name]['Equivalents']:
        text = text.replace('\\','\\\\')
        text = text.replace(')','\\\\)')
        text = text.replace('(','\\\\(')
        text = text.replace('{','\\\\{')
        #text = text.replace('}','\\\\}')
        text = text.replace('[','\\\\[')
        text = text.replace(']','\\\\]')
        text = text.replace('$','\\\\$')
        text = text.replace('^','\\\\^')
        text = text.replace('*','\\\\*')
        textlist.append("\""+text+"\"")
    texts = texts+",".join(textlist)
    texts = texts+"}"
    iconName = emoticons[name]['Name']
    iconName = "KuneProtIni"+''.join(iconName.split())+"KuneProtEnd"
    output = "message = replace(message, %s, \"%s\");" % (texts,iconName)
    print output
    
    
print 'formatEmoticons code section start.........................................................'
for name in emoticons:
    texts = "new String[]{"
    textlist = []
    for text in emoticons[name]['Equivalents']:
        textlist.append("\""+text+"\"")
    texts = texts+",".join(textlist)
    texts = texts+"}"
    iconName = emoticons[name]['Name']
    iconName = "KuneProtIni"+''.join(iconName.split())+"KuneProtEnd"
    output = "message = message.replaceAll(\"%s\", getImgSrcHtml(\"%s\"));" % (iconName,name)
    print output