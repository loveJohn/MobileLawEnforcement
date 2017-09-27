package com.chinadci.mel.mleo.core;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;

public class OutsideManifestHandler extends DefaultHandler {
	UserMapCentre userMapCentre;
	Context context;

	static OutsideManifestHandler handler;

	public static OutsideManifestHandler readHandler(Context context,
			InputStream stream) {
		try {
			if (handler == null) {
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sax = spf.newSAXParser();
				handler = new OutsideManifestHandler(context);
				sax.parse(stream, handler);
				stream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return handler;
	}

	public static OutsideManifestHandler getHandler(Context context) {
		return handler;
	}

	public OutsideManifestHandler(Context context) {
		this.context = context;
	}

	public UserMapCentre getUserMapCentre() {
		return this.userMapCentre;
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attr) throws SAXException {
		if (localName.equalsIgnoreCase("map-centre")) {
			double x = Double.parseDouble(attr.getValue("X"));
			double y = Double.parseDouble(attr.getValue("Y"));
			double scale = Double
					.parseDouble(attr.getValue("ScaleDenominator"));
			userMapCentre = new UserMapCentre(x, y, scale);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
	};

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}
}
