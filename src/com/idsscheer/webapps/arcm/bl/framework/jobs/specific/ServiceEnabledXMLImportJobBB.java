package com.idsscheer.webapps.arcm.bl.framework.jobs.specific;

import java.io.IOException;
import java.util.Locale;

import org.apache.commons.logging.Log;

import com.idsscheer.batchserver.exception.ServiceException;
import com.idsscheer.batchserver.services.IJobManager;
import com.idsscheer.webapps.arcm.bl.component.testmanagement.commands.job.ControlChecker;
import com.idsscheer.webapps.arcm.bl.datatransport.xml.xmlimport.IImportOption;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.services.framework.batchserver.ARCMServiceProvider;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.IARCMServices;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobAbortException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobWarningException;

public class ServiceEnabledXMLImportJobBB extends ServiceEnabledXMLImportJob {	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6319229811498325723L;

	protected static final Log log = org.apache.commons.logging.LogFactory.getLog(ServiceEnabledXMLImportJobBB.class);
	
	protected IOVID clientOvid;
	protected IOVID executingUserOvid;
	protected Locale executingUserLocale;
	
	public ServiceEnabledXMLImportJobBB(IOVID clientOVID,IOVID executingUserOvid, Locale executingUserLocale, IImportOption option, String clientSign, byte[] data,
			String identifier) throws IOException {
		super(clientOVID, executingUserOvid, executingUserLocale, option, clientSign,data, identifier);
		this.clientOvid = clientOVID;
		this.executingUserOvid = executingUserOvid;
		this.executingUserLocale = executingUserLocale;
	}

	protected void afterExecute() throws JobAbortException, JobWarningException {
        super.afterExecute();

        IJobManager jobmanager = null;
        try {
            jobmanager =(IJobManager)ARCMServiceProvider.getInstance().getService(IARCMServices.JOB_SERVICE);
        	if(this.clientOvid != null) {
        		jobmanager.add(new ControlChecker(clientOvid, executingUserOvid, executingUserLocale));
        	}
        	else {
        		jobmanager.add(new ControlChecker(executingUserOvid, executingUserLocale));
        	}            
        } catch (ServiceException e) {
             log.error("Error in afterExecute in class ServiceEnabledXMLImportJobBB: " + e.getMessage());
        }
    }
}