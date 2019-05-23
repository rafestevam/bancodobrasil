package com.idsscheer.webapps.arcm.bl.component.common.command.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandException;
import com.idsscheer.webapps.arcm.bl.framework.command.IChainContext;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IOVIDIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryJoin;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryRestriction;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestdefinitionAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IUsergroupAttributeType;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.config.Metadata;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.EnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IObjectType;

public class RecurringObjectSearchCommandBB extends RecurringObjectSearchCommand {

    @SuppressWarnings("unchecked")
	@Override
    public List<IOVID> searchIOVIDs(CommandContext cc) throws CommandException {
	    final String objectTypeId = cc.getCommandXMLParameter(OBJECT_TYPE_ID);
        final IChainContext chainCtx = cc.getChainContext();
        chainCtx.setWorkObject(IJobWorkobjectConstants.WORKOBJECT_SEARCHRESULT_APPOBJTYPE, Metadata.getMetadata().getObjectType(objectTypeId));

        IObjectType objectType = Metadata.getMetadata().getObjectType(objectTypeId);        
        IAppObjQuery appObjQuery = null;
        List<IOVID> searchResultOVIDs = new ArrayList<IOVID>();

        try {
            appObjQuery = chainCtx.createAppObjQuery(objectType);

            //client restriction
            List<String> clientSigns = (List<String>) chainCtx.getContextAttribute(IJobWorkobjectConstants.WORKOBJECT_CLIENTSIGNS);
            appObjQuery.addClientRestriction(clientSigns.toArray(new String[clientSigns.size()]));

            //version active and not deactivated
            appObjQuery.setHeadRevisionsOnly(true);
            appObjQuery.setIncludeDeletedObjects(false);

            //add further restrictions
            addQueryRestrictions(appObjQuery, cc);
            
            // begin BdB
            IUserContext userContext = cc.getChainContext().getUserContext();
            Set<EnumerationItem> roleLevelSet = JobHelperBB.getRoleLevels(Enumerations.USERROLE_TYPE.TESTMANAGER, userContext);
            if (roleLevelSet.contains(Enumerations.USERROLE_LEVEL.OBJECT)) {
            	if (!(roleLevelSet.contains(Enumerations.USERROLE_LEVEL.CLIENT) || 
            			roleLevelSet.contains(Enumerations.USERROLE_LEVEL.CROSSCLIENT)
            		)) {
            		addObjectSpecificQueryRestrictions(appObjQuery, userContext);
            	}
            }
            // end BdB

            //execute AppObjQuery
            for (IOVIDIterator it = appObjQuery.getResultIDIterator(); it.hasNext();) {
                searchResultOVIDs.add( it.next() );
            }
        } finally {
            if (appObjQuery != null) {appObjQuery.release();}
        }

        return searchResultOVIDs;
    }
    
    
    protected void addObjectSpecificQueryRestrictions(IAppObjQuery appObjQuery, final IUserContext userContext) {
    	List<Long> usergroupsIdList = JobHelperBB.getRoleUsergroupsIds(Enumerations.USERROLE_TYPE.TESTMANAGER, Enumerations.USERROLE_LEVEL.OBJECT, userContext);
    	appObjQuery.addRestriction(QueryJoin.inner(ITestdefinitionAttributeTypeBB.LIST_MANAGER_GROUP,
                        QueryRestriction.in(IUsergroupAttributeType.ATTR_OBJ_ID, usergroupsIdList)
                )
        );
    }
    
}
