package com.idsscheer.webapps.arcm.bl.framework.jobs.specific;
import java.lang.reflect.Constructor;
import java.util.Locale;

import org.apache.commons.logging.Log;

import com.idsscheer.webapps.arcm.bl.datatransport.xml.xmlimport.IImportOption;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.IARCMJob;

public class ServiceEnabledXMLImportJobProxy {
	protected static final Log log = org.apache.commons.logging.LogFactory.getLog(ServiceEnabledXMLImportJobProxy.class);

	public static IARCMJob getJob(IOVID clientOVID, IOVID executingUserOvid, Locale executingUserLocale, IImportOption option, String clientSign, byte[] data, String identifier) throws Exception {
		try {
			Class[] parameterTypes = { IOVID.class, IOVID.class, Locale.class, IImportOption.class, String.class, byte[].class, String.class };
			Constructor constructor = getConstructor("com.idsscheer.webapps.arcm.bl.framework.jobs.specific.ServiceEnabledXMLImportJobBB", parameterTypes);
			Object[] args = { clientOVID, executingUserOvid, executingUserLocale, option, clientSign, data, identifier };
			return (IARCMJob)constructor.newInstance(args);
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Failed to create instance of \"com.idsscheer.webapps.arcm.bl.framework.jobs.specific.ServiceEnabledXMLImportJob\".", e);
			}
			throw e;
		}		
	}
	
	protected static Constructor getConstructor(String instanceClassName, Class<?>... parameterTypes) throws NoSuchMethodException, ClassNotFoundException {
		try {
			return getClass(instanceClassName).getConstructor(parameterTypes);
		} catch (NoSuchMethodException e) {
			if (log.isDebugEnabled()) {
				log.debug("Failed to receive constructor for " + instanceClassName, e);
			}
			throw e;
		}
	}	
	
	private static Class<?> getClass(String instanceClassName) throws ClassNotFoundException {
		try {
			return Class.forName(instanceClassName);
		}
		catch (ClassNotFoundException e) {
			if (log.isDebugEnabled()) {
				log.debug("Failed to receive class for " + instanceClassName, e);
			}
			throw e;
		}
	}
}	