/**
 * 
 */
package com.idsscheer.webapps.arcm.ui.components.testmanagement.actioncommands;

import com.idsscheer.webapps.arcm.bl.common.support.AppObjUtility;
import com.idsscheer.webapps.arcm.bl.exception.ObjectRelationException;
import com.idsscheer.webapps.arcm.bl.exception.RightException;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRiskAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestdefinitionAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.ActionCommandException;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.object.BaseCacheActionCommand;

/**
 * @author Administrator
 *
 */
public class TestdefinitionCacheActionCommandBB extends BaseCacheActionCommand {
	
	@Override
	protected void afterExecute() {
		
		/*
		 * The attribute RISK.process.bb_deptManager has to be copied to TESTDEFINITION.orgunit:
		 * 
		 * TESTDEFINITION has the CONTROL object as its parent.
		 * CONTROL has the RISK object as its parent.
		 * RISK object has an attribute process that is a HIERARCHY
		 * HIERARCHY object has an attribute bb_deptManager that should be copied
		 */
		
		IAppObj testDefinitionAppObj = formModel.getAppObj();
		
		if (!testDefinitionAppObj.getVersionData().isInitialVersion()) {
			IAppObj controlAppObj = testDefinitionAppObj.getParentAppObj(getFullGrantUserContext(), IControlAttributeTypeBB.LIST_TESTDEFINITIONS);
			
			if (controlAppObj != null) {
				IAppObj riskAppObj = controlAppObj.getParentAppObj(getFullGrantUserContext(), IRiskAttributeTypeBB.LIST_CONTROLS);
				IListAttribute processAttr = riskAppObj.getAttribute(IRiskAttributeTypeBB.LIST_PROCESS);
				
				IAppObj processAppObj = ARCMCollections.extractSingleEntry(processAttr.getElements(getFullGrantUserContext()), true);
				
				AppObjUtility.copyAttributeValue(processAppObj, testDefinitionAppObj, IHierarchyAttributeTypeBB.LIST_DEPTMANAGER, ITestdefinitionAttributeTypeBB.LIST_ORGUNIT);
			} else {
				try {
					testDefinitionAppObj.getAttribute(ITestdefinitionAttributeTypeBB.LIST_ORGUNIT).removeAllElements(getFullGrantUserContext());
				} catch (RightException e) {
					throw new ActionCommandException(e);
				} catch (ObjectRelationException e) {
					throw new ActionCommandException(e);
				}
			}
		}
		
		super.afterExecute();
	}

}
