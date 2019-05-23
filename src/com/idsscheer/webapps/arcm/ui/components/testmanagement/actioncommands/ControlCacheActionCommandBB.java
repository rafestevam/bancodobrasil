package com.idsscheer.webapps.arcm.ui.components.testmanagement.actioncommands;

import java.util.List;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IAttribute;

import com.idsscheer.webapps.arcm.ui.framework.actioncommands.object.BaseCacheActionCommand;

public class ControlCacheActionCommandBB extends BaseCacheActionCommand {

	protected void execute() {
		super.execute();
		IAppObj control = formModel.getAppObj();
		
		executeActionRules(control);
		
	}
	
	private void executeActionRules(IAppObj control) {

		List<IAttribute> mandatoryAttributes = control.getMandatoryAttributes(getUserContext());
		
		for (IAttribute mandatoryAttribute : mandatoryAttributes) {
			if (mandatoryAttribute.isEmpty()) {
				formModel.enableErrorMessages();
				break;
			}
		}
		
	}

}
