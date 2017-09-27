package com.chinadci.mel.mleo.core;

import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.content.Context;

public class ManifestHandler {
	HashMap<Integer, Funcation> functionMap;
	HashMap<Integer, Module> moduleMap;

	public ManifestHandler(Context context, InputStream manifest) {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			// /创建解析器
			SAXParser sax = spf.newSAXParser();
			FunctionModuleHandler handler = new FunctionModuleHandler(context);
			sax.parse(manifest, handler);
			manifest.close();
			functionMap = handler.getFunctionMap();
			moduleMap = handler.getModuleMap();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public HashMap<Integer, Funcation> getFunctionMap() {
		return this.functionMap;
	}

	public HashMap<Integer, Module> getModuleMap() {
		return this.moduleMap;
	}
}
