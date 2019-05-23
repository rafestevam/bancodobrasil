/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.riskmanagement.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRa_impacttypeAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IObjectType;

/**
 * <p>
 * This command will be performed when the risk owner has defined the owner_status attribute >> NOT POSSIBLE.
 * </p>
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 * <br>
 * @author brmob
 * @since v20151124-MNT-UATEnhancements
 * @see RiskAssessmentResetCommand
 */
public class RiskAssessmentResetCommandBB extends RiskAssessmentResetCommand {
	
	@SuppressWarnings("rawtypes")
	@Override
	protected List<IAttributeType> getAttributes(CommandContext cc,
			IAppObj appObj) {
		IObjectType objectType = appObj.getObjectType();
		if (ObjectType.RA_IMPACTTYPE.equals(objectType)) {
			List<IAttributeType> resetAttrs = super.getAttributes(cc, appObj);
			List<IAttributeType> customResetAttr = new ArrayList<IAttributeType>();
			for (IAttributeType resetAttr : resetAttrs) {
				if (getCustomAttrsToBeRemoved().contains(resetAttr)) {
					continue;
				}
				customResetAttr.add(resetAttr);
			}
			return customResetAttr;
		}
		return Collections.emptyList();
	}
	
	@SuppressWarnings("rawtypes")
	private List<IAttributeType> getCustomAttrsToBeRemoved(){
		return Arrays.asList((IAttributeType)
				IRa_impacttypeAttributeType.ATTR_TYPE);
	}
}
