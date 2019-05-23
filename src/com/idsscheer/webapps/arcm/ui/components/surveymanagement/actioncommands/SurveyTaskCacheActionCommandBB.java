/**
 * 
 */
package com.idsscheer.webapps.arcm.ui.components.surveymanagement.actioncommands;

import java.util.Set;

import com.idsscheer.webapps.arcm.bl.component.common.support.UsergroupUtilityBB;
import com.idsscheer.webapps.arcm.bl.exception.ObjectRelationException;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ISurveytaskAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IUsergroupAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.ActionCommandException;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.object.BaseCacheActionCommand;

/**
 * @author Administrator
 *
 */
public class SurveyTaskCacheActionCommandBB extends BaseCacheActionCommand {
	
	@Override
	protected void execute() {
		super.execute();
		IAppObj surveyTask = formModel.getAppObj();
		
		if (surveyTask.getVersionData().isInitialVersion()) {
			IListAttribute managerGroupAttr = surveyTask.getAttribute(ISurveytaskAttributeType.LIST_MANAGER_GROUP);
			IListAttribute reviewerGroupAttr = surveyTask.getAttribute(ISurveytaskAttributeType.LIST_REVIEWER_GROUP);
			
			if (!managerGroupAttr.isEmpty() && reviewerGroupAttr.isEmpty()) {
				IAppObj managerGroup = ARCMCollections.extractSingleEntry(managerGroupAttr.getElements(getFullGrantUserContext()), true);
				IListAttribute managerGroupOrgUnitAttr = managerGroup.getAttribute(IUsergroupAttributeTypeBB.LIST_BB_ORGUNIT);
				
				if (!managerGroupOrgUnitAttr.isEmpty()) {
					IAppObj managerGroupOrgUnit = ARCMCollections.extractSingleEntry(managerGroupOrgUnitAttr.getElements(getFullGrantUserContext()), true);
					Set<IAppObj> reviewerGroups = UsergroupUtilityBB.getUserGroupsByOrganizationalUnit(managerGroupOrgUnit, Enumerations.USERROLE_TYPE.SURVEYREVIEWER, Enumerations.USERROLE_LEVEL.OBJECT);
					
					if (!reviewerGroups.isEmpty()) {
						
						
						for (IAppObj reviewerGroup : reviewerGroups) {
							try {
								reviewerGroupAttr.addFirstElement(reviewerGroup, getUserContext());
							} catch (RightException e) {
								throw new ActionCommandException(e);
							} catch (ObjectRelationException e) {
								throw new ActionCommandException(e);
							}
						}
					}
					
				}
				
			}
		}
		
	}

}
