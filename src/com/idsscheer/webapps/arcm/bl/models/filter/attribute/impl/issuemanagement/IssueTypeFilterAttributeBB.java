/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.issuemanagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.bl.common.support.AppObjUtility;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.FilterAttributeInitParameter;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.IFilterAttribute;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.IPredefinedFilterValue;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.IPredefinedFilterValueFilterAttribute;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.ClientSignFilterAttribute;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.EnumFilterAttribute;
import com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.PredefinedFilterValue;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.common.constants.metadata.EnumerationsBB;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumeration;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.config.metadata.filter.IFilterAttributeType;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;

/**
 * @author Administrator
 *
 */
public class IssueTypeFilterAttributeBB extends EnumFilterAttribute implements IPredefinedFilterValueFilterAttribute {

	protected static final Log log = LogFactory.getLog(IssueTypeFilterAttributeBB.class);
	protected String clientSign;
	protected List<IPredefinedFilterValue> predefinedFilterValues;
	protected List<String> allAvailableIssueTypes;
	
	public IssueTypeFilterAttributeBB(IUserContext userContext,
			IFilterAttributeType attributeConfig) {
		super(userContext, attributeConfig);
		this.predefinedFilterValues = null;
	}
	
	@Override
	public IEnumeration getEnumeration() {
		return EnumerationsBB.ISSUETYPE.ENUM;
	}
	
	@Override
	public void update(String leadingAttrName, IFilterAttribute leadingFilterAttr) {
		if ((leadingFilterAttr instanceof ClientSignFilterAttribute)) {
			
			String newClientSign = (String) leadingFilterAttr.getRawValue(leadingAttrName);
			if (!StringUtils.equals(this.clientSign, newClientSign)) {
				this.clientSign = newClientSign;
				this.predefinedFilterValues = null;
			}
		}
	}
	
	@Override
	public void init(Map<FilterAttributeInitParameter, Object> parameterMap) {
		super.init(parameterMap);
		Map<String, String> paramMap = (Map<String, String>) parameterMap.get(FilterAttributeInitParameter.FilterSetParameterMap);
		if (paramMap != null) {
			if (paramMap.containsKey("client_sign")) {
				this.clientSign = ((String) paramMap.get("client_sign"));
			} else if (parameterMap.containsKey(FilterAttributeInitParameter.ListParentAppObj)) {
				IAppObj appObj = (IAppObj) parameterMap.get(FilterAttributeInitParameter.ListParentAppObj);
				
				if (!appObj.getObjectType().isClientDependent()) {
					return;
				}
				this.clientSign = AppObjUtility.getRelatedClientSign(appObj);
			}
		}
	}
	
	@Override
	public List<IPredefinedFilterValue> getPredefinedValues() {
		if (this.predefinedFilterValues != null) {
			return this.predefinedFilterValues;
		}

		this.predefinedFilterValues = new ArrayList<IPredefinedFilterValue>();
		this.allAvailableIssueTypes = new ArrayList<String>();

		for (IEnumerationItem issueTypeEnumItem : EnumerationsBB.ISSUETYPE.ENUM.getItems()) {


			PredefinedFilterValue predefinedFilterValue = new PredefinedFilterValue(issueTypeEnumItem.getValue(), issueTypeEnumItem.getPropertyKey(), false);

			if (issueTypeEnumItem.isFilterRelevant()) {

				this.predefinedFilterValues.add(predefinedFilterValue);
				if ((!issueTypeEnumItem.isAll()) || (!issueTypeEnumItem.isVirtual())) {
					this.allAvailableIssueTypes.add(issueTypeEnumItem.getValue());
				}
			}
		}
		return this.predefinedFilterValues;
	}
	
	@Override
	public List<IFilterCriteria> getFilterCriteria() {
		getPredefinedValues();

		List<IFilterCriteria> filterCriterias = new ArrayList<IFilterCriteria>();

		List<IEnumerationItem> values = getValues();

		if ((values == null) || (values.isEmpty()) || (((IEnumerationItem) values.get(0)).isAll())) {
			
			filterCriterias.add(filterFactory.getSimpleFilterCriteria(getViewColumnAttributeAlias(), this.allAvailableIssueTypes));
		} else if (((IEnumerationItem) values.get(0)).isVirtual()) {
			List<String> resolvedValues = new ArrayList<String>();
			
			for (String value : ((IEnumerationItem) values.get(0)).getRelatedEnumerationItemValues()) {
				if (this.allAvailableIssueTypes.contains(value)) {
					resolvedValues.add(value);
				}
			}
			filterCriterias.add(filterFactory.getSimpleFilterCriteria(getViewColumnAttributeAlias(), resolvedValues));
		} else {
			filterCriterias.add(filterFactory.getSimpleFilterCriteria(getViewColumnAttributeAlias(), ((IEnumerationItem) values.get(0)).getValue()));
		}

		filterCriterias.add(filterFactory.getSimpleFilterCriteria("client_sign", this.clientSign));

		return filterCriterias;
	}
	
	@Override
	public boolean isEmpty() {
		return this.items.isEmpty();
	}
	
	@Override
	public Object getRawValue(String viewColumnName) {
		return this.items;
	}

}
