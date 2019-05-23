package com.aris.arcm.transfer.parser.dynamic;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aris.arcm.transfer.IPostProcessor.ChangeType;
import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.framework.message.IMessage;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryRestriction;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ILossAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeType;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ovid.OVIDUtility;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IStringAttributeType;
import com.idsscheer.webapps.arcm.custom.SendHelperBB;


public class LossPostProcessorEngine extends AbstractPostProcessorEngine {
	protected final Log log = LogFactory.getLog(LossPostProcessorEngine.class);
	
	protected IAppObj lossAppObj = null;

	public LossPostProcessorEngine(IUserContext userContext, String guid, ChangeType changeType) {
		super(userContext, guid, changeType);
	}
	
    protected void execute() throws Exception {
    	if (changeType.equals(ChangeType.created)) {
    		lossAppObj = getAppObj(getGuidRestrictions(guid));

    		final List<Long> riskIDs = OVIDUtility.toSimpleIds(lossAppObj.getAttribute(ILossAttributeType.LIST_RELRISKS).getRawValue());
            final IUserContext fullGrant = ContextFactory.createFullGrantUserContext(userContext);
            IAppObjQuery query = null;
            final Set<IAppObj> toGroups = ARCMCollections.createSet();
            try {
            	IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(userContext, ObjectType.RISK);
        		query = facade.createQuery();
        		query.setHeadRevisionsOnly(true);
        		query.addRestriction(QueryRestriction.in(IRiskAttributeType.ATTR_OBJ_ID, riskIDs));

                for (final IAppObj riskObj : query.getResultIterator()) {
                    final List<IAppObj> ownerGroups = riskObj.getAttribute(IRiskAttributeType.LIST_OWNER_GROUP).getElements(fullGrant);
                    toGroups.addAll(ownerGroups);
                    final List<IAppObj> managerGroups = riskObj.getAttribute(IRiskAttributeType.LIST_MANAGER_GROUP).getElements(fullGrant);
                    toGroups.addAll(managerGroups);
                }
            } finally {
                if (query != null) {query.release();}
            }
            for (IAppObj group : toGroups) {
            	SendHelperBB.send(group, EnumerationsBB.INITIATORS_BB.LOSSDB_LOSS_ACCEPTED, userContext, this, false);
			}
    	}
    }

	@Override
	public void addAdditionalParameters(IAppObj appObj, IMessage message) {
		// TODO Auto-generated method stub	
	}	
	
	@Override
	protected IStringAttributeType getGuidAttribute() {
		return ILossAttributeType.BASE_ATTR_GUID;
	}

	@Override
	protected IAppObjFacade getFacade() {
		return FacadeFactory.getInstance().getAppObjFacade(userContext, ObjectType.LOSS);
	}

	@Override
	public IAppObj getAppObjToAdd() {
		return lossAppObj;
	}

}