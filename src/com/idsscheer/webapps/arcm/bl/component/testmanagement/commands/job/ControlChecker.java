package com.idsscheer.webapps.arcm.bl.component.testmanagement.commands.job;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import com.idsscheer.webapps.arcm.bl.dataaccess.query.IViewQuery;
import com.idsscheer.webapps.arcm.bl.dataaccess.query.QueryFactory;
import com.idsscheer.webapps.arcm.bl.datatransport.xml.AAMMLLogger;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.framework.jobs.BaseJob;
import com.idsscheer.webapps.arcm.bl.framework.jobs.util.RightsHelperBB;
import com.idsscheer.webapps.arcm.bl.framework.message.IMessage;
import com.idsscheer.webapps.arcm.bl.framework.message.MessageFactory;
import com.idsscheer.webapps.arcm.bl.framework.message.MessageHandler;
import com.idsscheer.webapps.arcm.bl.framework.transaction.ITransaction;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjVersionData;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IOfflineprocessingentryAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IViewObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.TransactionManager;
import com.idsscheer.webapps.arcm.bl.offlineprocessing.helpers.OfflineProcessingUtility;
import com.idsscheer.webapps.arcm.bl.offlineprocessing.offlinelock.OfflineLockService;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IClientAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.resources.IResourceBundle;
import com.idsscheer.webapps.arcm.common.resources.ResourceBundleFactory;
import com.idsscheer.webapps.arcm.common.util.StringUtility;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDFactory;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.ILongAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IObjectType;
import com.idsscheer.webapps.arcm.dl.framework.DataLayerComparator;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;
import com.idsscheer.webapps.arcm.dl.framework.dllogic.SimpleFilterCriteria;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobAbortException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobException;
import com.idsscheer.webapps.arcm.services.framework.batchserver.services.jobs.JobWarningException;

public class ControlChecker extends BaseJob {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8360037299072252261L;
	protected static final IEnumerationItem JOB_TYPE = EnumerationsBB.JOBS_BB.CONTROLCHECKERJOB;
	protected final String CLASSNAME = this.getClass().getName();
	protected static final String VIEW_CONTROLS_UPDATED = "controlsUpdated";
	protected static final String VIEW_CONTROLS_UPDATED_GROUP_ID = "manager_group_id";

	private static final String KEY_INFO_CONTROLCHECKER_RESULT = "job.controlchecker.job.executed.DBI";
	private static final String KEY_WARN_COULD_NOT_LOAD_CONTROL = "job.warning.could.not.load.control.DBI";
	private static final String KEY_INFO_OBJETCTS_COUNT = "info.controlchecker.job.object.count";
	private static final String KEY_INFO_CONTROLCHECKER_START = "info.controlchecker.job.start";
	private static final String KEY_INFO_CONTROLCHECKER_END = "info.controlchecker.job.end";
	private static final String KEY_INFO_OBJECT_INFO = "info.controlchecker.job.object.info";
	
	protected IResourceBundle bundle;
	protected String logMessages;
	protected long count = 0;
	protected IAppObj clientAppObj = null;
	protected IOVID executingUserOvid;
	
	public ControlChecker(IOVID clientOVID, IOVID executingUserOvid, Locale executingUserLocale) {
		super(clientOVID, executingUserOvid, executingUserLocale);
		this.executingUserOvid = executingUserOvid; 
	}
	
	public ControlChecker(IOVID executingUserOvid, Locale executingUserLocale) {
		super(executingUserOvid, executingUserLocale);
		this.executingUserOvid = executingUserOvid;
	}
	
	@Override
	protected void init() throws JobException {
		super.init();
		this.bundle = ResourceBundleFactory.getBundle(userContext.getLanguage());
		if (isClientbased()) {
			clientAppObj = getClientBasedJobHelper().getClient(); 
		}
	}	
	
	@Override
	protected void execute() throws JobAbortException, JobWarningException {
		AAMMLLogger.info(CLASSNAME, bundle.getString(KEY_INFO_CONTROLCHECKER_START));

		try {
			checkObjects(ObjectType.CONTROL, VIEW_CONTROLS_UPDATED, VIEW_CONTROLS_UPDATED_GROUP_ID);
			AAMMLLogger.info(CLASSNAME, bundle.getString(KEY_INFO_CONTROLCHECKER_END));
		} finally {
			logMessages = AAMMLLogger.getLogs();       
			if (!StringUtility.isTrimmedEmpty(AAMMLLogger.getErrorLogs())) {
				addErrorLogging(KEY_INFO_CONTROLCHECKER_RESULT, logMessages);
			} else if (!StringUtility.isTrimmedEmpty(AAMMLLogger.getWarnLogs())) {
				addWarningLogging(KEY_INFO_CONTROLCHECKER_RESULT, logMessages);
			} else {
				addResultLogging(KEY_INFO_CONTROLCHECKER_RESULT, logMessages);
			}
		}	
    }

	@Override
	protected void deallocateResources() {
		AAMMLLogger.clearAllLogs();
	}

	@Override
	public String getJobNameKey() {
		return getJobType().getPropertyKey();
	}

	@Override
	public IEnumerationItem getJobType() {
		return JOB_TYPE;
	}
	
    protected void addConcludingMessagesToMessageHandler(ITransaction transaction) {
    	IMessage message = MessageFactory.createMessage(this.userContext.getUser(), getMessageInitiator(), this.userContext.getUser(), clientAppObj);
        message.addToContext("log", logMessages);
        MessageHandler.getInstance().addMessage( transaction, message );
    }

    protected IEnumerationItem getMessageInitiator() {
        IEnumerationItem initiator;
        if ((Enumerations.JOBENDSTATE.SUCCESS.equals(getJobEndState()))) {
             initiator = EnumerationsBB.INITIATORS_BB.CONTROL_CHECKER_SUCCESSFUL;
        } else {
            initiator = EnumerationsBB.INITIATORS_BB.CONTROL_CHECKER_FAILED;
        }
        return initiator;
    }

	protected void checkObjects(IObjectType objectType, String viewId, String viewGroupId) throws JobAbortException {
		IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(userContext, objectType);
		List<IFilterCriteria> filterCriteria = new ArrayList<IFilterCriteria>();
		
		if (clientAppObj != null) {
			String clientSign = clientAppObj.getRawValue(IClientAttributeType.ATTR_SIGN);
			SimpleFilterCriteria clientFilter = new SimpleFilterCriteria("client_sign", DataLayerComparator.EQUAL, clientSign);
			filterCriteria.add(clientFilter);
		}	
		SimpleFilterCriteria changeTypeFilter = new SimpleFilterCriteria("change_type", DataLayerComparator.EQUAL, Enumerations.CHANGETYPE.XMLCHANGED.getValue());
		filterCriteria.add(changeTypeFilter);


		List<IEnumerationItem> objectSpecificRolesToCheckClientLevel = Collections.unmodifiableList(Arrays.asList(Enumerations.USERROLE_TYPE.SYSADMIN, 
	    		Enumerations.USERROLE_TYPE.CLIENTADMIN, Enumerations.USERROLE_TYPE.CONTROLMANAGER));
	    IEnumerationItem objectSpecificRoleForRestrictions = Enumerations.USERROLE_TYPE.CONTROLMANAGER;	    	    
	    addRoleFilter(objectSpecificRolesToCheckClientLevel, objectSpecificRoleForRestrictions, viewGroupId, filterCriteria);

	    IViewQuery query = null;
	    
	    try{	    	
	        query = QueryFactory.createQuery(userContext, viewId, filterCriteria, null, null);
	        long size = query.getSize();
			AAMMLLogger.info(CLASSNAME, bundle.getString(KEY_INFO_OBJETCTS_COUNT, Long.toString(size)));
	        if (size > 0) {
	            final Iterator<IViewObj> iterator = query.getResultIterator();
				count += size;
				setBaseObjectsToProcessCount(count);
				while (iterator.hasNext()) {
					final IViewObj viewObj = iterator.next();
	            	Long objId = (Long) viewObj.getRawValue("obj_id");
					IOVID currentOvid = OVIDFactory.getOVID(objId);
					IAppObj currentAppObj;
					try {
						currentAppObj = facade.load(currentOvid, false);
						//IOVID priorOvid = OVIDFactory.getOVID(currentOvid.getId(), currentAppObj.getVersionData().getVersionCount()-1);
						IOVID priorOvid = getPriorOvid(currentAppObj);
						if (priorOvid != null) {
							IAppObj priorAppObj = facade.load(priorOvid, false);
							handleObject(currentAppObj, priorAppObj, objectType);
						}	
					} catch (RightException e) {
						addWarningLogging(KEY_WARN_COULD_NOT_LOAD_CONTROL, objId.toString());
					}	
					increaseProgress();
					increaseEditedObjectsCounter(1);
				}
	        } 
	    } catch (Exception e) {
	    	addErrorLogging(e.toString());
        } finally {
            if (query != null) {
                query.release();
            }
        }
	}
	
	protected IOVID getPriorOvid(IAppObj currentAppObj) {
		IOVID result = null;
		Map<Long, IAppObjVersionData> versionHistoryMap = new TreeMap<Long, IAppObjVersionData>(Collections.reverseOrder());
		
		for (IAppObjVersionData appObjVersionData : currentAppObj.getVersionHistory()) {
			versionHistoryMap.put(appObjVersionData.getOVID().getVersion(), appObjVersionData);
		}
		Iterator<Long> versionHistory = versionHistoryMap.keySet().iterator();
		while (versionHistory.hasNext()) {
			Long key = versionHistory.next();
			IAppObjVersionData appObjVersionData = versionHistoryMap.get(key); 
			IEnumerationItem changeType = appObjVersionData.getChangeType();
			if (changeType.equals(Enumerations.CHANGETYPE.CHANGED) || changeType.equals(Enumerations.CHANGETYPE.DELETED)) {
				// it cannot be CHANGETYPE.CREATED because all control object are created in ARIS (and there is no import ARCM -> ARIS for control objects)
				// customers decided to restore attrs also from deactivated objects
				result = appObjVersionData.getOVID();
				break;
			}
		}
		return result;
	}
	
	protected void handleObject(IAppObj currentAppObj, IAppObj priorAppObj, IObjectType objectType) throws JobAbortException {
		if (objectType.equals(ObjectType.CONTROL)) {
			/*
			attribute.CONTROL.bb_afirmacoesControle.DBI=Afirma��es do controle
			attribute.CONTROL.bb_controlAssertions.DBI=Afirma��es das demonstra��es financeiras
			attribute.CONTROL.bb_evidenciasControle.DBI=Evid�ncias do controle
			attribute.CONTROL.bb_salvaAtivos.DBI=Salvaguarda de ativos
			attribute.CONTROL.bb_controleAntifraude.DBI=Antifraude
			attribute.CONTROL.bb_conformidade.DBI=Conformidade
			attribute.CONTROL.bb_riscoFalha.DBI=Risco de falha		
			*/
			
			currentAppObj.getAttribute(IControlAttributeTypeBB.ATTR_AFIRMACOES_CONTROLE).setRawValue(priorAppObj.getAttribute(IControlAttributeTypeBB.ATTR_AFIRMACOES_CONTROLE).getRawValue());
			currentAppObj.getAttribute(IControlAttributeTypeBB.ATTR_ASSERTIONS).setRawValue(priorAppObj.getAttribute(IControlAttributeTypeBB.ATTR_ASSERTIONS).getRawValue());
			currentAppObj.getAttribute(IControlAttributeTypeBB.ATTR_EVIDENCIAS_CONTROLE).setRawValue(priorAppObj.getAttribute(IControlAttributeTypeBB.ATTR_EVIDENCIAS_CONTROLE).getRawValue());
			currentAppObj.getAttribute(IControlAttributeTypeBB.ATTR_SALVA_ATIVOS).setRawValue(priorAppObj.getAttribute(IControlAttributeTypeBB.ATTR_SALVA_ATIVOS).getRawValue());
			currentAppObj.getAttribute(IControlAttributeTypeBB.ATTR_CONTROLE_ANTIFRAUDE).setRawValue(priorAppObj.getAttribute(IControlAttributeTypeBB.ATTR_CONTROLE_ANTIFRAUDE).getRawValue());
			currentAppObj.getAttribute(IControlAttributeTypeBB.ATTR_CONFORMIDADE).setRawValue(priorAppObj.getAttribute(IControlAttributeTypeBB.ATTR_CONFORMIDADE).getRawValue());
			currentAppObj.getAttribute(IControlAttributeTypeBB.ATTR_RISCO_FALHA).setRawValue(priorAppObj.getAttribute(IControlAttributeTypeBB.ATTR_RISCO_FALHA).getRawValue());
			
			saveAppObj(currentAppObj, objectType, IControlAttributeType.BASE_ATTR_OBJ_ID);
			
			String name = currentAppObj.getAttribute(IControlAttributeType.ATTR_NAME).getRawValue();
			AAMMLLogger.info(CLASSNAME, bundle.getString(KEY_INFO_OBJECT_INFO, name));
		}	
	}
	
	protected void saveAppObj(IAppObj appObj, final IObjectType pObjectType, ILongAttributeType attrObjId) throws JobAbortException {
		ITransaction transaction = TransactionManager.getInstance().createTransaction();
		IAppObjFacade facade = null;
        try {
        	Long referencedID = appObj.getAttribute(attrObjId).getRawValue();
            IOVID appObjOVID = OVIDFactory.getOVID(referencedID);
        	releaseOfflineLock(appObjOVID, transaction);
        	
			facade = FacadeFactory.getInstance().getAppObjFacade(userContext, pObjectType);
            facade.allocateWriteLock(appObj.getVersionData().getOVID());
			facade.save(appObj, transaction, true);
		} catch (Exception e) {
			transaction.rollback();
			log.error(e.getMessage(), e);
		    addErrorLogging(KEY_ERR_GENERAL);
			throw new JobAbortException(e, KEY_ERR_GENERAL);
        } finally{
            if(facade != null) {
                facade.releaseLock(appObj.getVersionData().getOVID());
            }
        }    
        transaction.commit();
	}	
	
    protected void releaseOfflineLock(IOVID appObjOVID, ITransaction transaction) {
        if (OfflineLockService.getInstance().isLocked(appObjOVID, null)) {
            IOfflineprocessingentryAppObj offlineEntry = OfflineProcessingUtility.getOfflineProcessingEntry(appObjOVID.getId());
            OfflineLockService.getInstance().releaseLocks(offlineEntry, getJobContext().getUserContext(), transaction);
        }
    }    	
    
    protected void addRoleFilter(final List<IEnumerationItem> objectSpecificRolesToCheckClientLevel, 
    		final IEnumerationItem objectSpecificRoleForRestrictions, final String viewGroupId, List<IFilterCriteria> filters) {
		
    	if (executingUserOvid != null) {
	    	long executingUserId = executingUserOvid.getId();
	    	
			if (!RightsHelperBB.userHasUpperRole(executingUserId, objectSpecificRolesToCheckClientLevel, userContext)) {
				SimpleFilterCriteria ugFilter;
				List<Long> usergroupIds = RightsHelperBB.getObjectSpecificGroups(executingUserId, objectSpecificRoleForRestrictions, userContext);
				if (usergroupIds.size() > 0) {
					ugFilter = new SimpleFilterCriteria(viewGroupId, DataLayerComparator.IN, usergroupIds);
				}
				else {
					// filter condition to not display anything but this situation cannot happen
					ugFilter = new SimpleFilterCriteria(viewGroupId, DataLayerComparator.EQUAL, new Long(-1));
				}
				filters.add(ugFilter);
	    	}
    	}
    	else {
			// filter condition to not display anything
    		filters.add(new SimpleFilterCriteria(viewGroupId, DataLayerComparator.EQUAL, new Long(-1)));
    	}	
    }
}