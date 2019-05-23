package com.aris.arcm.transfer.parser.dynamic;

import java.util.ArrayList;
import java.util.List;

import com.aris.arcm.transfer.IPostProcessor.ChangeType;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.framework.message.IMessage;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IQueryRestriction;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryRestriction;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IStringAttributeType;
import com.idsscheer.webapps.arcm.custom.ISendBB;

public abstract class AbstractPostProcessorEngine implements ISendBB {
	protected IUserContext userContext;
	protected String guid;
	protected ChangeType changeType;
	
	protected IAppObjFacade facade;
	
	public AbstractPostProcessorEngine(IUserContext userContext, String guid, ChangeType changeType) {
		this.userContext = userContext;
		this.guid = guid;
		this.changeType = changeType;
		this.facade = getFacade();
	}	

	@Override
	abstract public void addAdditionalParameters(IAppObj appObj, IMessage message);

    protected IAppObj getAppObj(final List<IQueryRestriction> restrictions){
		IAppObjQuery query = null;
		
		query = facade.createQuery();
		query.setHeadRevisionsOnly(true);
		query.setIncludeDeletedObjects(true);

		for (IQueryRestriction restriction : restrictions) {
			query.addRestriction(restriction);
		}	
		return query.getSingleResult();
	} 
    
	protected List<IQueryRestriction> getGuidRestrictions(final String guid) {
		IQueryRestriction restriction = QueryRestriction.eq(getGuidAttribute(), guid);
		List<IQueryRestriction> restrictionList = new ArrayList<IQueryRestriction>();
		restrictionList.add(restriction);
		return restrictionList;
	}    
	
	abstract protected IStringAttributeType getGuidAttribute();

	abstract protected IAppObjFacade getFacade();
}