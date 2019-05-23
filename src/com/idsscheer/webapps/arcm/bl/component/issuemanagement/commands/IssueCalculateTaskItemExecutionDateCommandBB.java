/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.issuemanagement.commands;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.bl.framework.command.ICommand;
import com.idsscheer.webapps.arcm.bl.framework.workflow.WorkflowUtility;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IBooleanAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IDateAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.common.support.ConfigParameter;
import com.idsscheer.webapps.arcm.common.support.DateUtils;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IDateAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.workflow.IStateConditionMetadata;
import com.idsscheer.webapps.arcm.config.metadata.workflow.IStateMetadata;
import com.idsscheer.webapps.arcm.config.metadata.workflow.ITransitionMetadata;

/**
 * @author Administrator
 *
 */
public class IssueCalculateTaskItemExecutionDateCommandBB implements ICommand {

	@ConfigParameter(value = "fromExecutionDateAttr", optional = false)
	public static String PARAMETER_FROMEXECUTIONDATE_ATTR = "fromExecutionDateAttr";
	
	@ConfigParameter(value = "toExecutionDateAttr", optional = false)
	public static String PARAMETER_TOEXECUTIONDATE_ATTR = "toExecutionDateAttr";
	
	@ConfigParameter(value = "daysToTaskItemDeadline", optional = false)
	public static String PARAMETER_DAYSTOTASKITEMDEADLINE = "daysToTaskItemDeadline";
	
	@ConfigParameter(value = "targetStatesToTaskItemDeadline", optional = true)
	public static String PARAMETER_TARGETSTATES_TASKITEMDEADLINE = "targetStatesToTaskItemDeadline";
	
	/* (non-Javadoc)
	 * @see com.idsscheer.webapps.arcm.bl.framework.command.ICommand#execute(com.idsscheer.webapps.arcm.bl.framework.command.CommandContext)
	 */
	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
		
		final String fromExecutionDateParameter = getParameterByID(cc, PARAMETER_FROMEXECUTIONDATE_ATTR);
		final String toExecutionDateParameter = getParameterByID(cc, PARAMETER_TOEXECUTIONDATE_ATTR);
		final String[] daysToTaskItemDeadlineParameter = StringUtils.split(getParameterByID(cc, PARAMETER_DAYSTOTASKITEMDEADLINE),",");
		
		final Integer daysToTaskItemDeadline;
		IAppObj issue = cc.getChainContext().getTriggeringAppObj();
		
		daysToTaskItemDeadline = daysToTaskItemDeadlineParameter.length > 1 ? 
				getDaysToTaskItemDeadline(daysToTaskItemDeadlineParameter, cc) : 
					Integer.valueOf(getParameterByID(cc, PARAMETER_DAYSTOTASKITEMDEADLINE));

		IDateAttributeType fromExecutionDateAttrType = issue.getAttributeType(fromExecutionDateParameter);
		IDateAttribute fromExecutionDateAttr = issue.getAttribute(fromExecutionDateAttrType);
		
		IDateAttributeType toExecutionDateAttrType = issue.getAttributeType(toExecutionDateParameter);
		IDateAttribute toExecutionDateAttr = issue.getAttribute(toExecutionDateAttrType);
		
		fromExecutionDateAttr.setRawValue(DateUtils.today());
		
		Date toExecutionDate = org.apache.commons.lang3.time.DateUtils.addDays(fromExecutionDateAttr.getRawValue(), daysToTaskItemDeadline);
		toExecutionDateAttr.setRawValue(toExecutionDate);
		
		return CommandResult.OK;
	}
	
	protected Integer getDaysToTaskItemDeadline(String[] daysToTaskItemDeadlineParameter, CommandContext cc){
		IAppObj issue = cc.getChainContext().getTriggeringAppObj();
		String[] targetStatesToTaskItemDeadlineParameter = StringUtils.split(getParameterByID(cc, PARAMETER_TARGETSTATES_TASKITEMDEADLINE),",");
		targetStatesToTaskItemDeadlineParameter = StringUtils.stripAll(targetStatesToTaskItemDeadlineParameter);
		
		int index = -1;
		
		List<ITransitionMetadata> transitionMetadatas = WorkflowUtility.getStateMetadata(issue).getTransitions();
		for (ITransitionMetadata transitionMetadata : transitionMetadatas) {
			
			List<IStateMetadata> targetStateMetadatas = transitionMetadata.getTargetStates();
			for (IStateMetadata targetStateMetadata : targetStateMetadatas) {
				
				if (ArrayUtils.contains(targetStatesToTaskItemDeadlineParameter, targetStateMetadata.getStateId())) {
					
					List<IStateConditionMetadata> stateConditionMetadatas = targetStateMetadata.getConditions();
					boolean isFutureAppObjState = true;
					for (IStateConditionMetadata stateConditionMetadata : stateConditionMetadatas) {
						
						@SuppressWarnings("rawtypes")
						IAttributeType attributeType = stateConditionMetadata.getAttributeType();
						String[] stateConditionMetadataValues = StringUtils.split(stateConditionMetadata.getAttributeValue(),",");
						stateConditionMetadataValues = StringUtils.stripAll(stateConditionMetadataValues);
						IAttribute attribute = issue.getAttribute(attributeType);
						
						if (attribute instanceof IEnumAttribute) {
							IEnumAttribute enumAttribute = (IEnumAttribute) attribute;
							IEnumerationItem enumerationItem = ARCMCollections.extractSingleEntry(enumAttribute.getRawValue(), true);
							
							isFutureAppObjState = !ArrayUtils.contains(stateConditionMetadataValues, enumerationItem.getId()) ? false : isFutureAppObjState;
							
						} else if (attribute instanceof IBooleanAttribute) {
							IBooleanAttribute booleanAttribute = (IBooleanAttribute) attribute;
							
							isFutureAppObjState = !ArrayUtils.contains(stateConditionMetadataValues, booleanAttribute.toString()) ? false : isFutureAppObjState;
							
						}
					}
					
					if (!isFutureAppObjState) {
						continue;
					}
					
					index = ArrayUtils.indexOf(targetStatesToTaskItemDeadlineParameter, targetStateMetadata.getStateId());
					break;
				}
			}
		}
		
		if (!(index >= 0)) {
			throw new IllegalArgumentException("The command attributes weren't configured properly");
		}
		
		return Integer.valueOf(daysToTaskItemDeadlineParameter[index]);
		
	}
	
	protected String getParameterByID(final CommandContext cc, String parameterID) {
		return cc.getCommandXMLParameter(parameterID);
	}

}
