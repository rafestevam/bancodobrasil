/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.component.deficiencymanagement.re;

import java.util.List;

import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.bl.re.REKey;
import com.idsscheer.webapps.arcm.bl.re.ext.RuleAppObj;
import com.idsscheer.webapps.arcm.bl.re.ext.StateInformation;
import com.idsscheer.webapps.arcm.bl.re.impl.REEnvironment;


/**
 * <p>
 * Used by the rule engine in 'deficiency.drl' and 'deficiency_base.dsl'.
 * </p>
 *
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 *@author brmob
 *@since v20160314-MNT-UATEnhancements
 *@see DeficiencyHelper
 */
public class DeficiencyHelperBB extends DeficiencyHelper {
	
	/**
     * Defines whether its editable or not for each of the existing four groups of (magnitude (amount), magnitude (currency),
     * and magnitude (NA)) in the same way: If NA is chosen, the fields 'amount' and 'currency' are read-only.
     * 
     * @since v20160314-MNT-UATEnhancements
     */
    public static void handleMagnitudesAndCurrenciesEditable(String amountAttr, String currencyAttr, String naAttr) {

        REEnvironment env = REEnvironment.getInstance();
        RuleAppObj deficiency = env.getRuleAppObj();
        StateInformation s = env.getStateInformation();


        REKey magnNaKey = deficiency.createAtomicKey(naAttr);
        REKey amountAttrKey = deficiency.createAtomicKey(currencyAttr);
        REKey currencyAttrKey = deficiency.createAtomicKey(amountAttr);

        Boolean magnNa = deficiency.getRawValue(magnNaKey.getString());
        boolean amountAndCurrencyEditable = magnNa == null || !magnNa;

        s.setEditable(amountAttrKey.getString(), amountAndCurrencyEditable);
        s.setEditable(currencyAttrKey.getString(), amountAndCurrencyEditable);
        //if editable then set also as 'not mandatory'
        if (amountAndCurrencyEditable) {
            s.setMandatory(amountAttrKey.getString(), !amountAndCurrencyEditable);
            s.setMandatory(currencyAttrKey.getString(), !amountAndCurrencyEditable);
        }
    }

	/**
     * Created for support especific situation when we have the attribute "magn_after_comp_l1_nap" setted up as false,
     * and the attribute magn_after_comp_l1 must be mandatory in this case.
     * 
     * @since v20160314-MNT-UATEnhancements
     */
    public static void handleMagnitudesKnowned(String amountAttr, String currencyAttr, String naAttr) {

        REEnvironment env = REEnvironment.getInstance();
        RuleAppObj deficiency = env.getRuleAppObj();
        StateInformation s = env.getStateInformation();


        REKey magnNaKey = deficiency.createAtomicKey(naAttr);
        REKey amountAttrKey = deficiency.createAtomicKey(currencyAttr);
        REKey currencyAttrKey = deficiency.createAtomicKey(amountAttr);

        Boolean magnNa = deficiency.getRawValue(magnNaKey.getString());
        boolean amountAndCurrencyEditable = magnNa == null || !magnNa;

        s.setEditable(amountAttrKey.getString(), amountAndCurrencyEditable);
        s.setEditable(currencyAttrKey.getString(), amountAndCurrencyEditable);
        //if editable then set also as 'not mandatory'
  /*      if (amountAndCurrencyEditable) {
            s.setMandatory(amountAttrKey.getString(), !amountAndCurrencyEditable);
            s.setMandatory(currencyAttrKey.getString(), !amountAndCurrencyEditable);
        }*/
    }
    
    
    
    /**
     * Decides for of the given four attributes if their <code>mandatory</code> flag is set to <code>true</code> or <code>false</code>.
     *
     * @param amountAttr is set to <code>true</code> if <code>naAttr</code> has not value <code>true</code>, otherwise <code>false</code>
     * @param currencyAttr is set to <code>true</code> if <code>naAttr</code> has not value <code>true</code>, otherwise <code>false</code>
     * @param naAttr is set to <code>false</code> if <code>naAttr</code> has not value <code>true</code>, otherwise <code>true</code>
     * @param commentAttr is set to <code>false</code> if <code>naAttr</code> has not value <code>true</code>, otherwise <code>true</code>
     */
    public static void decideMandatoryProperty(String amountAttr, String currencyAttr, String naAttr, String commentAttr) {
        REEnvironment env = REEnvironment.getInstance();
        RuleAppObj deficiency = env.getRuleAppObj();
        StateInformation s = env.getStateInformation();
        String amountKey = deficiency.createAtomicKey(amountAttr).getString();
        String currencyKey = deficiency.createAtomicKey(currencyAttr).getString();
        String naKey = deficiency.createAtomicKey(naAttr).getString();
        String commentKey = deficiency.createAtomicKey(commentAttr).getString();
        Boolean naValue = deficiency.getRawValue(naKey);
        // "magn_na" is_not filled     OR
		// "magn_na" has value "false"
        if (naValue==null || !naValue) {
            // ... is going to specify amount and currency
            s.setMandatory(amountKey, true);
            s.setMandatory(currencyKey, true);
            s.setMandatory(naKey, false);
            s.setMandatory(commentKey, false);
        } else {
            // ... is going to specify nothing
            s.setMandatory(amountKey, false);
            s.setMandatory(currencyKey, false);
            s.setMandatory(naKey, true);
            s.setMandatory(commentKey, true);
        }
    }

    public static Boolean checkRiskMaxSize(){
    	REEnvironment env = REEnvironment.getInstance();
        RuleAppObj deficiency = env.getRuleAppObj();
        
        IListAttribute riskAttr = (IListAttribute)deficiency.getAttribute("risk");
        List<IAppObj> lstRisks = riskAttr.getElements(env.getUserContext());
        return lstRisks.size() > 1;
        
    }
}
