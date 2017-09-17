package com.elephant.common.utils;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;

import com.google.common.base.Throwables;

public class JAXBUtil {

	private static final String MARSHAL_ERROR_MSG_FORMAT = "Cannot Marshal this object.Reason %s";
    private static Map<Class<?>, JAXBContext> contextCache = new HashMap<Class<?>, JAXBContext>();
    private static Map<Class<?>[], JAXBContext> contextsCache = new HashMap<Class<?>[], JAXBContext>();

	public static String toJson(Object object) {
		StringWriter writer;
		try {
			JAXBContext jc = getJAXBContext(object.getClass());
			System.out.println(object.getClass().getName());
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
			marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
			marshaller.setProperty(MarshallerProperties.JSON_MARSHAL_EMPTY_COLLECTIONS, false);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			writer = new StringWriter();
			marshaller.marshal(object, writer);
		} catch (JAXBException | RuntimeException e) {
			e.printStackTrace();
			return String.format(MARSHAL_ERROR_MSG_FORMAT, Throwables.getStackTraceAsString(e));
		}
		return writer.toString();
	}
	
	public static  String toJson(Object object,Class<?>... clazz) {
		StringWriter writer;
		try {
			System.setProperty("javax.xml.bind.context.factory","org.eclipse.persistence.jaxb.JAXBContextFactory");
			JAXBContext jc = getJAXBContext(clazz);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
			marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
			marshaller.setProperty(MarshallerProperties.JSON_MARSHAL_EMPTY_COLLECTIONS, false);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			writer = new StringWriter();
			marshaller.marshal(object, writer);
		} catch (JAXBException | RuntimeException e) {
			return String.format(MARSHAL_ERROR_MSG_FORMAT, Throwables.getStackTraceAsString(e));
		}
		return writer.toString();
	}
	

	public static <T> T jsonToObject(String json, Class<T> clazz) throws JAXBException {
		JAXBContext jc = getJAXBContext(clazz);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
		unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
		StreamSource ss = new StreamSource(new StringReader(json));
		return unmarshaller.unmarshal(ss, clazz).getValue();
	}

	private static <T> JAXBContext getJAXBContext(Class<?> clazz) throws JAXBException {
		JAXBContext jc = contextCache.get(clazz);
		if (jc == null) {
			jc = JAXBContext.newInstance(new Class[] { clazz });
			contextCache.put(clazz, jc);
		}
		return jc;
	}
	
	private static <T> JAXBContext getJAXBContext(Class<?>... clazz) throws JAXBException {
		JAXBContext jc = contextsCache.get(clazz);
		if (jc == null) {
			jc = JAXBContext.newInstance(clazz);
			contextsCache.put(clazz, jc);
		}
		return jc;
	}
}
