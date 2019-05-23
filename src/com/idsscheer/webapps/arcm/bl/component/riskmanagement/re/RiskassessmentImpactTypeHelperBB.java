package com.idsscheer.webapps.arcm.bl.component.riskmanagement.re;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.idsscheer.webapps.arcm.bl.re.ext.MessageInformation;
import com.idsscheer.webapps.arcm.bl.re.ext.RuleAppObj;
import com.idsscheer.webapps.arcm.bl.re.ext.StateInformation;
import com.idsscheer.webapps.arcm.bl.re.impl.REEnvironment;
import com.idsscheer.webapps.arcm.common.support.DateUtils;

public class RiskassessmentImpactTypeHelperBB extends RiskassessmentImpactTypeHelper{

	public static void checkDurationRiskManagement(String dataInicial, String dataFinal, String duration){
	
		REEnvironment env = REEnvironment.getInstance();
		RuleAppObj ruleAppObj = env.getRuleAppObj();
		MessageInformation messageInformation = env.getMessageInformation();
		StateInformation stateInformation = env.getStateInformation();
		
		if(!ruleAppObj.isEmpty(dataFinal) && !ruleAppObj.isEmpty(duration)){
			Date dtIni 	= ruleAppObj.getRawValue(dataInicial);
			Date dtfin 	= ruleAppObj.getRawValue(dataFinal);
			long qtd 	= ruleAppObj.getRawValue(duration);
			
			int i = (int)qtd;
			
			Date dtPlusQtd = new Date();
			
			Calendar c = Calendar.getInstance();
			c.setTime(dtIni);
			c.add(Calendar.DATE, i);
			
			dtPlusQtd = c.getTime();
			
			if(dtPlusQtd.after(dtfin)){
				stateInformation.setValid(duration, false);
				messageInformation.addErrorMessage(duration, "erro.date.durationriskassessment.DBI", new Object[0]);
			}
			
			Date today = DateUtils.today();
			if (today.after(dtIni)) {
				long diffDays = today.getTime() - dtIni.getTime();
				long differenceInDays = TimeUnit.DAYS.convert(diffDays, TimeUnit.MILLISECONDS);
				
				if (differenceInDays > qtd) {
					stateInformation.setValid(duration, false);
					messageInformation.addErrorMessage(duration, "error.risk.durationtooshort.ERR", differenceInDays+1L);
				}
			}
			
		}
	}
}
