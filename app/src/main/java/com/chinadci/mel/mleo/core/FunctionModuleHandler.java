package com.chinadci.mel.mleo.core;

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.util.Log;

public class FunctionModuleHandler extends DefaultHandler {
	Context context;
	final int TAG_NULL = -0x000001;
	final int TAG_FUNCTION = 0x00001;
	final int TAG_MODULE = 0x000002;

	HashMap<Integer, Funcation> functionMap;
	HashMap<Integer, Module> moduleMap;
	String apackage;
	int ptag = TAG_NULL;

	public FunctionModuleHandler(Context context) {
		this.context = context;
	}

	public HashMap<Integer, Funcation> getFunctionMap() {
		return this.functionMap;
	}

	public HashMap<Integer, Module> getModuleMap() {
		return this.moduleMap;
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		functionMap = new HashMap<Integer, Funcation>();
		moduleMap = new HashMap<Integer, Module>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attr) throws SAXException {
		super.startElement(uri, localName, qName, attr);
		if (localName.equalsIgnoreCase("chinadci-manifest")) {
			apackage = attr.getValue("package");
		}

		if (localName.equalsIgnoreCase("functions")) {
			ptag = TAG_FUNCTION;
		}

		if (localName.equalsIgnoreCase("modules")) {
			ptag = TAG_MODULE;
		}

		if (localName.equalsIgnoreCase("item")) {
			if (ptag == TAG_FUNCTION) {
				int id = Integer.parseInt(attr.getValue("id"));
				String command = attr.getValue("command");
				String icon = attr.getValue("icon");
				String label = attr.getValue("label");
				boolean hasModule = Boolean.parseBoolean(attr
						.getValue("hasModule"));
				int iconRes = getResId(icon);
				int labelRes = getResId(label);
				Funcation function = new Funcation(id, iconRes, labelRes, command);
				if (hasModule) {
					int moduleId = Integer.parseInt(attr.getValue("module"));
					function.setModule(moduleId);
				}
				functionMap.put(id, function);
			} else if (ptag == TAG_MODULE) {
				int id = Integer.parseInt(attr.getValue("id"));
				String tool = attr.getValue("tool");
				String content = attr.getValue("content");
				String title = attr.getValue("title");
				int titleRes = getResId(title);
				Module module = new Module(id, titleRes, content, tool);
				moduleMap.put(id, module);
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		if (localName.equalsIgnoreCase("functions")) {
			ptag = TAG_NULL;
		}

		if (localName.equalsIgnoreCase("modules")) {
			ptag = TAG_NULL;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
	}

	int getResId(String res) {
		try {
			return context.getResources().getIdentifier(
					this.context.getPackageName() + ":"
							+ res.substring(res.indexOf("@") + 1), null, null);
		} catch (Exception e) {
			Log.w("manifest.xml", "no resource " + res);
			return -1;
		}
	}
}
