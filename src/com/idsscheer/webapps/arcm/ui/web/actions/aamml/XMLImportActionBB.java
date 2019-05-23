package com.idsscheer.webapps.arcm.ui.web.actions.aamml;

import java.util.Locale;

import com.idsscheer.batchserver.exception.ServiceException;
import com.idsscheer.batchserver.services.IJobManager;
import com.idsscheer.webapps.arcm.bl.component.testmanagement.commands.job.ControlChecker;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.IARCMJob;

public class XMLImportActionBB extends XMLImportAction {
    public XMLImportActionBB() {
        super();
    }

    protected void addJobToJobManagerQueue(IJobManager jobmanager, IARCMJob xmlImportJob, IOVID clientOvid, IOVID executionUserOvid, Locale executionUserLocale) throws ServiceException {
    	super.addJobToJobManagerQueue(jobmanager, xmlImportJob, clientOvid, executionUserOvid, executionUserLocale);
    	if(clientOvid != null) {
    		jobmanager.add(new ControlChecker(clientOvid, executionUserOvid, executionUserLocale));
    	}
    	else {
    		jobmanager.add(new ControlChecker(executionUserOvid, executionUserLocale));
    	}
    }

}
