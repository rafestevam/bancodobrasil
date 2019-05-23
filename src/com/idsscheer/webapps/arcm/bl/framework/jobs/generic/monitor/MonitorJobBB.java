package com.idsscheer.webapps.arcm.bl.framework.jobs.generic.monitor;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.idsscheer.webapps.arcm.bl.component.common.command.job.JobHelperBB;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITaskitemAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestcaseAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestdefinitionAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDFactory;
import com.idsscheer.webapps.arcm.config.Metadata;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.EnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IObjectType;
import com.idsscheer.webapps.arcm.config.metadata.task.ITaskItem;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobAbortException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobWarningException;

public class MonitorJobBB extends MonitorJob {

    /**
	 * 
	 */
	private static final long serialVersionUID = 965861515345722404L;

	public MonitorJobBB(IOVID clientOVID, List<IObjectType> objectTypes, IOVID executingUserOvid, Locale executingUserLocale) {
    	super(clientOVID, objectTypes, executingUserOvid, executingUserLocale);
    }

    public MonitorJobBB(List<IObjectType> objectTypes, IOVID executingUserOvid, Locale executingUserLocale) {
    	super(objectTypes, executingUserOvid, executingUserLocale);
    }

    @Override
    protected void execute() throws JobAbortException, JobWarningException {
    	//if no list of AppObjTypes was specified then use all existing
    	if ((this.objectTypes == null) || (this.objectTypes.isEmpty())) {
    		this.objectTypes = Metadata.getMetadata().getNonVirtualObjectTypes();
    	}
    	  
    	//search all items that must be checked
    	this.appObjQuery = createTaskItemAppObjQuery();
    	IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(this.userContext, ObjectType.TASKITEM);
	  
    	//process each item
    	IAppObjIterator objIterator = this.appObjQuery.getResultIterator();
    	setBaseObjectsToProcessCount(objIterator.getSize());
    	  
        
        boolean isObjectSpecificTestManager = false;
        List<Long> usergroupsIdListForTestManager = null;
        
        if (objIterator.getSize() > 0) {
        	isObjectSpecificTestManager = checkTestManagerObjectLevel();
        	if (isObjectSpecificTestManager) {
        		usergroupsIdListForTestManager = JobHelperBB.getRoleUsergroupsIds(Enumerations.USERROLE_TYPE.TESTMANAGER, Enumerations.USERROLE_LEVEL.OBJECT, userContext);
        	}	
        }

        while (objIterator.hasNext()) {
        	if (getIsMarkedToStop()) {
              return;
            }
            
            IAppObj taskItemAppObj = (IAppObj)objIterator.next();
            

            IObjectType referencedType = Metadata.getMetadata().getObjectType(taskItemAppObj.getRawValue(ITaskitemAttributeType.ATTR_OBJECT_OBJTYPE));
            String taskID = taskItemAppObj.getAttribute(ITaskitemAttributeType.ATTR_TASKCONFIGURATIONID).getRawValue();
            ITaskItem taskItemConfiguration = Metadata.getMetadata().getTaskItem(referencedType.getId(), taskID);
            

            if ((taskItemConfiguration != null) && (taskItemConfiguration.getMonitorStrategy() != null)) {
              checkEscalationLevel(taskItemAppObj, taskItemConfiguration.getMonitorStrategy(), facade);
            }
            
            increaseProgress();
          }

        //write an overview of all new reached escalation to the jobinfo
        createJobInformationLog();
    }

    protected boolean checkTestManagerObjectLevel() {
    	boolean result = false;
    	
        Set<EnumerationItem> roleLevelSet = JobHelperBB.getRoleLevels(Enumerations.USERROLE_TYPE.TESTMANAGER, userContext);
        if (roleLevelSet.contains(Enumerations.USERROLE_LEVEL.OBJECT)) {
        	if (!(roleLevelSet.contains(Enumerations.USERROLE_LEVEL.CLIENT) || 
        			roleLevelSet.contains(Enumerations.USERROLE_LEVEL.CROSSCLIENT)
        		)) {
        		result = true;
        	}
        }
        return result;
    }

    protected boolean checkManagerRightToTestcase(final IAppObj taskItemAppObj, final List<Long> usergroupsIdListForTestManager) {
    	boolean result = false;
    	
    	long objId = taskItemAppObj.getRawValue(ITaskitemAttributeType.ATTR_OBJECT_ID);
    	long versionNumber = taskItemAppObj.getRawValue(ITaskitemAttributeType.ATTR_OBJECT_VERSION_NUMBER);
    	IOVID ovid = OVIDFactory.getOVID(objId, versionNumber);
    	IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(userContext, ObjectType.TESTCASE);
    	try {
			IAppObj testcase = facade.load(ovid, false);
			List<IAppObj> testdefinitionList = testcase.getAttribute(ITestcaseAttributeType.LIST_TESTDEFINITION).getElements(userContext);
			if (testdefinitionList != null && testdefinitionList.size() > 0) {
				IAppObj testdefinition = testdefinitionList.get(0);
				List<IOVID> usergroupOVIDList = testdefinition.getAttribute(ITestdefinitionAttributeTypeBB.LIST_MANAGER_GROUP).getElementIds();
				if (usergroupOVIDList != null && usergroupOVIDList.size() > 0) {
					for (IOVID usergroupOVID : usergroupOVIDList) {
						if (usergroupsIdListForTestManager.contains(usergroupOVID.getId())) {
							result = true;
							break;
						}
					}
				}
			}
		} catch (RightException e) {
			log.error(e);
		}
        return result;
    }
}