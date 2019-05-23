/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.deficiencymanagement.commands;

import com.idsscheer.webapps.arcm.bl.component.common.command.SetOperatorCommand;
import com.idsscheer.webapps.arcm.bl.exception.BLException;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandContext;
import com.idsscheer.webapps.arcm.bl.framework.command.CommandResult;

/**
 * <p>
 * This command is used to set the operator of a transactional object.
 * This includes the substitute and the execution date since this is the same for deficiencymanagerl1, deficiencymanagerl2 and deficiencymanagerl3.
 * </p>
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 * <br>
 * <p>
 * command XML parameters (<b>bold</b>=mandatory):
 * <ul>
 *  <li><b>operator</b>: <ul>
 * <li>'deficiencyManagerL1'</li>
 * <li>'deficiencyManagerL2'</li>
 * <li>'deficiencyManagerL3'</li> 
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
 * @since v20160711-UPGD-Upgrade982To985
 * @see SetOperatorCommand
 */
public class DeficiencySetOperatorCommandBB extends SetOperatorCommand {
	
	protected final static String[] deficiencyManagerL1Attributes = new String[]{"manager_group_l1","manager_user_l1","manager_user_l1_subs", "manager_user_l1_date"};
	protected final static String[] deficiencyManagerL2Attributes = new String[]{"manager_group_l2","manager_user_l2","manager_user_l2_subs", "manager_user_l2_date"};
	protected final static String[] deficiencyManagerL3Attributes = new String[]{"manager_group_l3","manager_user_l3","manager_user_l3_subs", "manager_user_l3_date"};
	
	@Override
	public CommandResult execute(CommandContext cc) throws BLException {
		final String operator = getParameterByID(cc, PARAMETER_OPERATOR);
		
		final boolean isDeficencyManagerL1 = "deficiencyManagerL1".equalsIgnoreCase(operator);
		final boolean isDeficencyManagerL2 = "deficiencyManagerL2".equalsIgnoreCase(operator);
		final boolean isDeficencyManagerL3 = "deficiencyManagerL3".equalsIgnoreCase(operator);
		
		if (isDeficencyManagerL1) {
			fillAttributes(cc, deficiencyManagerL1Attributes);
		} else if (isDeficencyManagerL2) {
			fillAttributes(cc, deficiencyManagerL2Attributes);
		} else if (isDeficencyManagerL3) {
			fillAttributes(cc, deficiencyManagerL3Attributes);
		} else {
			throw new IllegalArgumentException();
		}
		
		return new CommandResult(CommandResult.STATUS.OK);
	}
	
	protected String getParameterByID(final CommandContext cc, String parameterID) {
		return cc.getCommandXMLParameter(parameterID);
	}

}
