/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.issuemanagement.commands;

import com.idsscheer.webapps.arcm.bl.component.common.command.ClearOperatorCommand;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;
import com.idsscheer.webapps.arcm.common.support.ConfigParameter;

/**
 * <p>
 * This command is used to clear the operator of a transactional object.
 * This includes the substitute and the execution date since this is the same for owner and reviewer.
 * </p>
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 * * <p>
 * It has to be specified if operator and phase parameters should be clear.
 * There is no default value!
 * </p>
 * <br>
 * <p>
 * command XML parameters (<b>bold</b>=mandatory):
 * <ul>
 *  <li><b>operator</b>: <ul>
 * <li>'owner' or 'reviewer'</li>
 * <li>'mgmtApproverL1' or 'mgmtApproverL2'</li> 
 * <li>'implApproverL1' or 'implApproverL2'</li>
 * <li>'techApproverL1' or 'techApproverL2'</li>
 * </ul>
 * </li>
 * <li><b>phase</b>: <ul>
 * <li>'creation' or 'execution'</li>
 * </ul>
 * </li>
 * </ul>
 * </p>
 * <p>
 * input work objects (<b>bold</b>=mandatory): none
 * </p>
 * <p>
 * output work objects (<b>bold</b>=mandatory): none
 * </p>
 * @author brmob
 * @since v20150302-CUST-IssueMgmt
 * @see ClearOperatorCommand
 */
public class IssueClearOperatorCommandBB extends ClearOperatorCommand {
	
	@ConfigParameter(value = "phase", optional = false)
	public static String PARAMETER_PHASE = "phase";
	
	/* for creation phase */
	protected final static String[] mgmtApproverL1Attributes = new String[]{"bb_mgmtApproverL1_group","bb_mgmtApproverL1","bb_mgmtApproverL1_subs", "bb_mgmtApproverL1_date"};
	protected final static String[] mgmtApproverL2Attributes = new String[]{"bb_mgmtApproverL2_group","bb_mgmtApproverL2","bb_mgmtApproverL2_subs", "bb_mgmtApproverL2_date"};
	protected final static String[] implApproverL1Attributes = new String[]{"bb_implApproverL1_group","bb_implApproverL1","bb_implApproverL1_subs", "bb_implApproverL1_date"};
	protected final static String[] implApproverL2Attributes = new String[]{"bb_implApproverL2_group","bb_implApproverL2","bb_implApproverL2_subs", "bb_implApproverL2_date"};
	protected final static String[] techApproverL1Attributes = new String[]{"bb_techApproverL1_group","bb_techApproverL1","bb_techApproverL1_subs", "bb_techApproverL1_date"};
	protected final static String[] techApproverL2Attributes = new String[]{"bb_techApproverL2_group","bb_techApproverL2","bb_techApproverL2_subs", "bb_techApproverL2_date"};
	
	/* for execution phase */
	protected final static String[] execMgmtApprL1Attributes = new String[]{"bb_mgmtApproverL1_group","bb_execMgmtApprL1","bb_execMgmtApprL1_subs", "bb_execMgmtApprL1_date"};
	protected final static String[] execMgmtApprL2Attributes = new String[]{"bb_mgmtApproverL2_group","bb_execMgmtApprL2","bb_execMgmtApprL2_subs", "bb_execMgmtApprL2_date"};
	protected final static String[] execImplApprL1Attributes = new String[]{"bb_implApproverL1_group","bb_execImplApprL1","bb_execImplApprL1_subs", "bb_execImplApprL1_date"};
	protected final static String[] execImplApprL2Attributes = new String[]{"bb_implApproverL2_group","bb_execImplApprL2","bb_execImplApprL2_subs", "bb_execImplApprL2_date"};

	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
		final String operator = getParameterByID(cc, PARAMETER_OPERATOR);
		final String phase = getParameterByID(cc, PARAMETER_PHASE);
		
		final boolean isOwner = "owner".equalsIgnoreCase(operator);
		final boolean isReviewer = "reviewer".equalsIgnoreCase(operator);
		final boolean isMgmtApproverL1 = "mgmtApproverL1".equalsIgnoreCase(operator);
		final boolean isMgmtApproverL2 = "mgmtApproverL2".equalsIgnoreCase(operator);
		final boolean isImplApproverL1 = "implApproverL1".equalsIgnoreCase(operator);
		final boolean isImplApproverL2 = "implApproverL2".equalsIgnoreCase(operator);
		final boolean isTechApproverL1 = "techApproverL1".equalsIgnoreCase(operator);
		final boolean isTechApproverL2 = "techApproverL2".equalsIgnoreCase(operator);
		
		final boolean isCreationPhase = "creation".equalsIgnoreCase(phase);
		final boolean isExecutionPhase = "execution".equalsIgnoreCase(phase);
		
		if (isCreationPhase) {
			if (isMgmtApproverL1) {
				clearAttributes(cc, mgmtApproverL1Attributes);
			} else if (isMgmtApproverL2) {
				clearAttributes(cc, mgmtApproverL2Attributes);
			} else if (isImplApproverL1) {
				clearAttributes(cc, implApproverL1Attributes);
			} else if (isImplApproverL2) {
				clearAttributes(cc, implApproverL2Attributes);
			} else if (isTechApproverL1) {
				clearAttributes(cc, techApproverL1Attributes);
			} else if (isTechApproverL2) {
				clearAttributes(cc, techApproverL2Attributes);
			} else {
				throw new IllegalArgumentException();
			}
		} else if (isExecutionPhase) {
			if(isOwner) {
				clearAttributes(cc, ownerAttributes);
			} else if(isReviewer) {
				clearAttributes(cc, reviewerAttributes);
			} else if (isMgmtApproverL1) {
				clearAttributes(cc, execMgmtApprL1Attributes);
			} else if (isMgmtApproverL2) {
				clearAttributes(cc, execMgmtApprL2Attributes);
			} else if (isImplApproverL1) {
				clearAttributes(cc, execImplApprL1Attributes);
			} else if (isImplApproverL2) {
				clearAttributes(cc, execImplApprL2Attributes);
			} else {
				throw new IllegalArgumentException();
			}
		} else {
			throw new IllegalArgumentException();
		}
		
		return new CommandResult(CommandResult.STATUS.OK);
	}
	
	protected String getParameterByID(final CommandContext cc, String parameterID) {
		return cc.getCommandXMLParameter(parameterID);
	}
}
