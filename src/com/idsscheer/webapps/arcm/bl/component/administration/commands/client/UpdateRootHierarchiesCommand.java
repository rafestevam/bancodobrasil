package com.idsscheer.webapps.arcm.bl.component.administration.commands.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.common.support.AppObjUtility;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.command.IChainContext;
import com.idsscheer.webapps.arcm.bl.framework.command.ICommand;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObjFacade;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IHierarchyAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.impl.FacadeFactory;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjIterator;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.IAppObjQuery;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.query.QueryRestriction;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IClientAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;

/**
 * add missing root hierarchy elements for existing client after hierarchyType-enumeration configuration was changed.
 * Only adding nodes is supported. Deleting is not supported.
 */
public class UpdateRootHierarchiesCommand extends CreateRootHierarchiesCommand implements ICommand {

    public CommandResult execute(CommandContext cc) throws BLException {
        IChainContext chainCtx = cc.getChainContext();        
        addMissingHierarchyRootNodes(chainCtx);
        return new CommandResult(CommandResult.STATUS.OK);
    }
    
	/**
	 * create missing root hierarchy elements for existing client
	 */
	protected void addMissingHierarchyRootNodes(IChainContext chainCtx) throws BLException {
        IAppObj client = chainCtx.getTriggeringAppObj();

        IUserContext userCtx = ContextFactory.createFullGrantUserContext(chainCtx.getUserContext());
        IAppObjFacade facade = FacadeFactory.getInstance().getAppObjFacade(userCtx, ObjectType.HIERARCHY);
        
        IAppObjQuery query = null;	
        
        try {
        	// check existing root-hierarchies
            query = facade.createQuery();
                query.addRestriction(QueryRestriction.eq(IHierarchyAppObj.ATTR_ISROOT, Boolean.TRUE));
                query.addClientRestriction(client.getAttribute(IClientAttributeType.ATTR_SIGN).getRawValue());
            IAppObjIterator allRootHiearchiesIterator = query.getResultIterator();

            List<IEnumerationItem> alreadyExistingTypes = new ArrayList<>();
            for (IAppObj obj : allRootHiearchiesIterator) {
                IEnumAttribute typeAttr = obj.getAttribute(IHierarchyAppObj.ATTR_TYPE);
                alreadyExistingTypes.addAll(typeAttr.getRawValue());
            }

            // add missing root-hierarchie(s)
            Locale locale = AppObjUtility.getLocale(client);
            for (IEnumerationItem item : Enumerations.HIERARCHY_TYPE.ENUM.getItems()) {
    			if (!item.isVirtual() && !alreadyExistingTypes.contains(item) ) {
                    createHierarchyElement(chainCtx, client, item, locale);
                }
    		}
        } finally {
        	query.release();
        }
        
	}

}
