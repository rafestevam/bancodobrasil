/**
 * 
 */
package com.idsscheer.webapps.arcm.ui.components.riskmanagement.actioncommands;

import com.idsscheer.webapps.arcm.bl.models.form.IRiskassessmentFormModel;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.ICxnObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IRa_impacttypeAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IRiskassessmentAppObj;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IEnumAttribute;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IListAttribute;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ILossAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.ILossAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRa_impacttypeAttributeType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IRa_impacttypeAttributeTypeBB;
import com.idsscheer.webapps.arcm.common.util.ARCMCollections;
import com.idsscheer.webapps.arcm.common.util.ovid.IOVID;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;

/**
 * @author Administrator
 *
 */
public class RiskassessmentCopyLossValuesActionCommandBB extends RiskassessmentCopyLossValuesActionCommand {
	
	double max = Double.MIN_VALUE;
	double min = Double.MAX_VALUE;
	double avg = 0.0D;
	double sum = 0.0D;
	
	double reducedMax = Double.MIN_VALUE;
	double reducedMin = Double.MAX_VALUE;
	double reducedAvg = 0.0D;
	double reducedSum = 0.0D;
	
	long qtdeMax = Long.MIN_VALUE;
	long qtdeMin = Long.MAX_VALUE;
	long qtdeSum = 0L;
	long qtdeRelevantSum = 0L;
	
	double indirectLossSum = 0.0D;
	double nearLossSum = 0.0D;
	double gainSum = 0.0D;
	double lostIncomeSum = 0.0D;
	
	@Override
	protected void execute() {
		
		
		IListAttribute riskAttribute = getAppObject().getAttribute(IRiskassessmentAppObj.LIST_RISK);
		IOVID riskID = (IOVID)riskAttribute.getElementIds().get(0);
		
		for (IAppObj loss : this.losses) {
			
			IEnumAttribute lossTypeAttr = loss.getAttribute(ILossAttributeTypeBB.ATTR_TYPE);
			IEnumerationItem lossType = ARCMCollections.extractSingleEntry(lossTypeAttr.getRawValue(), true);
			
			if (lossType.equals(EnumerationsBB.LOSSDB_LOSS_TYPE_BB.DIRECT_LOSS)) {
				calculateDirectLossValues(loss, riskID);
			} else {
				calculateAnotherLossTypeValues(loss, lossType);
			}
		}
		
		this.jsBuffer = new StringBuilder();
		
		copyDirectLossValues();
		copyLossValue(IRa_impacttypeAttributeTypeBB.ATTR_INDIRECTLOSS_SUM, indirectLossSum);
		copyLossValue(IRa_impacttypeAttributeTypeBB.ATTR_NEARLOSS_SUM, nearLossSum);
		copyLossValue(IRa_impacttypeAttributeTypeBB.ATTR_GAIN_SUM, gainSum);
		copyLossValue(IRa_impacttypeAttributeTypeBB.ATTR_LOSTINCOME_SUM, lostIncomeSum);
		
		getAppObject().getAttribute(IRiskassessmentAppObj.ATTR_CURRENCY).addItem(this.currentCurrency);
		this.jsBuffer.append("currency").append(":").append("'" + this.currentCurrency.getValue() + "'");
		IRiskassessmentFormModel riskassessmentModel = (IRiskassessmentFormModel)lookupFormModel();
		IRa_impacttypeAppObj impactType = (IRa_impacttypeAppObj)riskassessmentModel.getModel(riskassessmentModel.getCurrentImpactTypeID()).getAppObj();
		impactType.setUsedLosses(com.idsscheer.webapps.arcm.bl.common.support.AppObjUtility.extractOVIDs(this.losses, true));

		this.notificationDialog.addInfoKey("info.copy.loss.values.successful.INF", new String[0]);
		this.notificationDialog.addJavascript("if(opener) {opener.__setValues({" + this.jsBuffer.toString() + "});opener.aam_objectcommand('cache')}");
	}
	
	private void calculateDirectLossValues(IAppObj loss, IOVID riskID){
		long qtdeEventos = loss.getAttribute(ILossAttributeTypeBB.ATTR_QTDE_EVENTOS).getPersistentRawValue();
		long qtdePerdasRelevantes = loss.getAttribute(ILossAttributeTypeBB.ATTR_QTDE_PERDAS_RELEVANTES).getPersistentRawValue();
		
		ICxnObj connection = getConnectionObject(riskID, loss);
		double gross = connection.getAttribute(ILossAttributeType.LIST_RELRISKS_CXN_ATTRIBUTE_TYPE.ATTR_RELRISKS_GROSSACTUAL).getRawValue().doubleValue() / qtdeEventos;
		double net = connection.getAttribute(ILossAttributeType.LIST_RELRISKS_CXN_ATTRIBUTE_TYPE.ATTR_RELRISKS_NETACTUAL).getRawValue().doubleValue() / qtdeEventos;
		
		if (gross > max) {
		    max = gross;
		}
		if (gross < min) {
		    min = gross;
		}
		if (net > reducedMax) {
		    reducedMax = net;
		}
		if (net < reducedMin) {
		    reducedMin = net;
		}
		if (qtdeEventos > qtdeMax) {
			qtdeMax = qtdeEventos;
		}
		if (qtdeEventos < qtdeMin) {
			qtdeMin = qtdeEventos;
		}
		
		sum += connection.getAttribute(ILossAttributeType.LIST_RELRISKS_CXN_ATTRIBUTE_TYPE.ATTR_RELRISKS_GROSSACTUAL).getRawValue().doubleValue();
		reducedSum += connection.getAttribute(ILossAttributeType.LIST_RELRISKS_CXN_ATTRIBUTE_TYPE.ATTR_RELRISKS_NETACTUAL).getRawValue().doubleValue();
		qtdeSum += qtdeEventos;
		qtdeRelevantSum += qtdePerdasRelevantes;
		
		avg += gross;
		reducedAvg += net;
	}
	
	private void copyDirectLossValues(){
		copyLossValue(IRa_impacttypeAttributeType.ATTR_MAX_LOSS, max);
		copyLossValue(IRa_impacttypeAttributeType.ATTR_MIN_LOSS, min);
		copyLossValue(IRa_impacttypeAttributeTypeBB.ATTR_SUM_LOSS, sum);
		
		copyLossValue(IRa_impacttypeAttributeType.ATTR_RED_MAX_LOSS, reducedMax);
		copyLossValue(IRa_impacttypeAttributeType.ATTR_RED_MIN_LOSS, reducedMin);
		copyLossValue(IRa_impacttypeAttributeTypeBB.ATTR_RED_SUM_LOSS, reducedSum);
		
		copyLossValue(IRa_impacttypeAttributeType.ATTR_FREQU_MAX_LOSS, qtdeMax);
		copyLossValue(IRa_impacttypeAttributeType.ATTR_FREQU_MIN_LOSS, qtdeMin);
		copyLossValue(IRa_impacttypeAttributeTypeBB.ATTR_FREQU_SUM_LOSS, qtdeSum);
		
		copyLossValue(IRa_impacttypeAttributeType.ATTR_RED_FREQU_MAX_LOSS, qtdeMax);
		copyLossValue(IRa_impacttypeAttributeType.ATTR_RED_FREQU_MIN_LOSS, qtdeMin);
		copyLossValue(IRa_impacttypeAttributeTypeBB.ATTR_RED_FREQU_SUM_LOSS, qtdeSum);
		
		int selectedDirectLosses = getSelectedDirectLosses();
		
		copyLossValue(IRa_impacttypeAttributeType.ATTR_AVG_LOSS, avg / selectedDirectLosses);
		copyLossValue(IRa_impacttypeAttributeType.ATTR_RED_AVG_LOSS, reducedAvg / selectedDirectLosses);
		copyLossValue(IRa_impacttypeAttributeType.ATTR_FREQU_AVG_LOSS, qtdeSum / selectedDirectLosses);
		copyLossValue(IRa_impacttypeAttributeType.ATTR_RED_FREQU_AVG_LOSS, qtdeSum / selectedDirectLosses);
		
		copyLossValue(IRa_impacttypeAttributeTypeBB.ATTR_SUM_RELEVANT_EVENTS, qtdeRelevantSum);
	}
	
	private void calculateAnotherLossTypeValues(IAppObj loss, IEnumerationItem lossType){
		double pendingLoss = loss.getAttribute(ILossAttributeTypeBB.ATTR_PENDING_LOSS).getPersistentRawValue();
		if (lossType.equals(EnumerationsBB.LOSSDB_LOSS_TYPE_BB.LOST_INCOME)) {
			lostIncomeSum += pendingLoss;
		} else if (lossType.equals(EnumerationsBB.LOSSDB_LOSS_TYPE_BB.GAIN)) {
			gainSum += pendingLoss;
		} else if (lossType.equals(EnumerationsBB.LOSSDB_LOSS_TYPE_BB.INDIRECT_LOSS)) {
			indirectLossSum += pendingLoss;
		} else if (lossType.equals(EnumerationsBB.LOSSDB_LOSS_TYPE_BB.NEAR_LOSS)) {
			nearLossSum += pendingLoss;
		}
	}
	
	private int getSelectedDirectLosses(){
		int directLossCount = 0;
		for (IAppObj loss : losses) {
			IEnumAttribute lossTypeAttr = loss.getAttribute(ILossAttributeTypeBB.ATTR_TYPE);
			IEnumerationItem lossType = ARCMCollections.extractSingleEntry(lossTypeAttr.getRawValue(), true);
			
			directLossCount = lossType.equals(EnumerationsBB.LOSSDB_LOSS_TYPE_BB.DIRECT_LOSS) ? directLossCount+1 : directLossCount;
		}
		return directLossCount;
	}
	
	
	
}
