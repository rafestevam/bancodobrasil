package com.idsscheer.webapps.arcm.ui.components.testmanagement.actioncommands.list;

import java.util.List;

import com.idsscheer.webapps.arcm.bl.common.support.AppObjUtility;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IControlAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IHierarchyAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ITestdefinitionAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.list.BaseNewAttachmentActionCommand;

public class Control2TestdefinitionNewAttachmentActionCommandBB extends BaseNewAttachmentActionCommand {

	@Override
	protected boolean beforeExecute() {

		if (!controlIsOk()) {
			return CANCEL_EXECUTION;
		}
		
		return super.beforeExecute();
	}
	
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
		
		IAppObj controlAppObj = appObj;
		IAppObj testDefinitionAppObj = newObj;
		
		IListAttribute processAttr = controlAppObj.getAttribute(IControlAttributeTypeBB.LIST_PROCESS);
		IAppObj processAppObj = ARCMCollections.extractSingleEntry(processAttr.getElements(getFullGrantUserContext()), true);
		
		if (testDefinitionAppObj != null) {
			AppObjUtility.copyAttributeValue(processAppObj, testDefinitionAppObj, IHierarchyAttributeTypeBB.LIST_DEPTMANAGER, ITestdefinitionAttributeTypeBB.LIST_ORGUNIT);
		}
		
		super.afterExecute();
	}

	protected boolean controlIsOk() {
		IAppObj parentAppObj = getAppObject();// OpenParent(ObjectType.CONTROL);

		boolean correctControl = true;
		if (null != parentAppObj) {

			List<IAttribute> mandatoryAttributes = parentAppObj.getMandatoryAttributes(getUserContext());

			for (int i = 0; i < mandatoryAttributes.size(); i++) {
				if (mandatoryAttributes.get(i).isEmpty()) {
					correctControl = false;
					notificationDialog.addErrorKey("error.message.control.not.ok.DBI", getStringRepresentation(formModel.getAppObj()));
					break;
				}
			}
		}
		return correctControl;
	}
}
