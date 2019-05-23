package com.idsscheer.webapps.arcm.ui.framework.filterrenderer;

import java.util.List;

import com.idsscheer.webapps.arcm.bl.models.filter.attribute.IPredefinedFilterValueFilterAttribute;
import com.idsscheer.webapps.arcm.common.support.AllowedAttributeType;
import com.idsscheer.webapps.arcm.common.support.AllowedFilterAttributeType;
import com.idsscheer.webapps.arcm.common.util.predefinedvalue.IPredefinedValue;
import com.idsscheer.webapps.arcm.config.metadata.enumerations.IEnumerationItem;
import com.idsscheer.webapps.arcm.ui.framework.renderer.context.IFilterRenderContext;
import com.idsscheer.webapps.arcm.ui.web.html.HTMLSelect;


@AllowedFilterAttributeType({IPredefinedFilterValueFilterAttribute.class})
@AllowedAttributeType
public class PredefinedValueExtendedFilterRendererBB
extends AbstractFilterRenderer
{
	protected String selection;
	protected List<IPredefinedValue> predefinedValues;

	public PredefinedValueExtendedFilterRendererBB() {}

	protected void renderWritable()
	{
		if (1 < this.predefinedValues.size()) {
			renderEditableFieldParam();
		}
		this.context.getJsEventMap().put("onkeypress", "return aam_applyOnReturn(event, this.form);");
		HTMLSelect select = getHTMLHelper().createSelect(this.context.getAttributeName()).setEvents(getJSEvents()).setClassName("w80 extended-combobox");


		select.setDisabled(2 > this.predefinedValues.size());
		select.addOptions(this.predefinedValues, this.context.getLocale(), this.selection);
		this.result.append(select);
	}

	protected void init() {
		Object rawValue = this.context.getAttributeRawValue(this.context.getAttributeName());
		if ((rawValue instanceof String)) {
			this.selection = ((String)rawValue);
		}
		if ((rawValue instanceof List)) {
			List rawValues = (List)rawValue;
			if (!rawValues.isEmpty()) {
				this.selection = ((IEnumerationItem)rawValues.get(0)).getValue();
			}
		} else if (null != rawValue) {
			this.selection = rawValue.toString();
		}
		this.predefinedValues = ((IFilterRenderContext)this.context).getPredefinedValues();
	}
}
