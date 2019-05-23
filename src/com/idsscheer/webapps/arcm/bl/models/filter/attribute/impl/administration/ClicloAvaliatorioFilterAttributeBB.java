package com.idsscheer.webapps.arcm.bl.models.filter.attribute.impl.administration;

import java.util.ArrayList;
import java.util.List;

import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.config.metadata.filter.IFilterAttributeType;
import com.idsscheer.webapps.arcm.dl.framework.DataLayerComparator;
import com.idsscheer.webapps.arcm.dl.framework.IFilterCriteria;

public class ClicloAvaliatorioFilterAttributeBB extends ProcessHierarchyDateRangeFilterAttributeBB {
	//hardcoded but it can be modified to more general solution
	protected static String testcaseVersionActiveViewAttr = "ta_va";
	protected static String testcaseDeactivatedViewAttr = "ta_deactivated";
	protected static String deficiencyVersionActiveViewAttr = "def_va";
	protected static String deficiencyDeactivatedViewAttr = "def_deactivated";
	protected static String deficiencyBbInicioClicloAvaliatorio = "def_bb_inicioClicloAvaliatorio";
	protected static String deficiencyBbFimClicloAvaliatorio = "def_bb_fimClicloAvaliatorio";
	
	public ClicloAvaliatorioFilterAttributeBB(IUserContext userContext, IFilterAttributeType attributeConfig) {
		super(userContext, attributeConfig);
	}

	public List<IFilterCriteria> getFilterCriteria() {
		List<IFilterCriteria> list = super.getFilterCriteria();
		if (fromFilterDate.getDate() != null || toFilterDate.getDate() != null) {
			List<IFilterCriteria> testcaseFilter = new ArrayList<IFilterCriteria>();
			testcaseFilter.add(filterFactory.getSimpleFilterCriteria(testcaseVersionActiveViewAttr, DataLayerComparator.EQUAL, Boolean.TRUE));
			testcaseFilter.add(filterFactory.getSimpleFilterCriteria(testcaseDeactivatedViewAttr, DataLayerComparator.EQUAL, Boolean.FALSE));
			testcaseFilter.addAll(list);

			List<IFilterCriteria> deficiencyFilter = new ArrayList<IFilterCriteria>();
			deficiencyFilter.add(filterFactory.getSimpleFilterCriteria(deficiencyVersionActiveViewAttr, DataLayerComparator.EQUAL, Boolean.TRUE));
			deficiencyFilter.add(filterFactory.getSimpleFilterCriteria(deficiencyDeactivatedViewAttr, DataLayerComparator.EQUAL, Boolean.FALSE));
			if (fromFilterDate.getDate() != null) {
				deficiencyFilter.add(filterFactory.getSimpleFilterCriteria(deficiencyBbFimClicloAvaliatorio, DataLayerComparator.GREATEROREQUAL, this.fromFilterDate.getDate()));
			}	
			if (toFilterDate.getDate() != null) {
				deficiencyFilter.add(filterFactory.getSimpleFilterCriteria(deficiencyBbInicioClicloAvaliatorio, DataLayerComparator.LESSOREQUAL, this.toFilterDate.getDate()));
			}	

			IFilterCriteria testcaseAndCriteria = filterFactory.and(testcaseFilter);
			IFilterCriteria deficiencyAndCriteria = filterFactory.and(deficiencyFilter);

			List<IFilterCriteria> orFilter = new ArrayList<IFilterCriteria>();
			orFilter.add(testcaseAndCriteria);
			orFilter.add(deficiencyAndCriteria);
			
			List<IFilterCriteria> resultFilter = new ArrayList<IFilterCriteria>();

			/*IFilterCriteria orCriteria = filterFactory.or(orFilter);
			resultFilter.add(orCriteria);
			resultFilter.add(filterFactory.or(orFilter));
			*/			
			resultFilter.add(filterFactory.getSimpleFilterCriteria(orFilter, IFilterCriteria.FilterConnect.OR));
			return resultFilter;
		}
		else {
			return list;
		}
    }
}
