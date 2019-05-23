package com.idsscheer.webapps.arcm.ui.framework.filterrenderer;

import com.idsscheer.webapps.arcm.bl.models.filter.attribute.IBooleanFilterAttribute;
import com.idsscheer.webapps.arcm.common.support.AllowedAttributeType;
import com.idsscheer.webapps.arcm.common.support.AllowedFilterAttributeType;
import com.idsscheer.webapps.arcm.common.support.ConfigParameter;
import com.idsscheer.webapps.arcm.common.support.ConfigParameterType;
import com.idsscheer.webapps.arcm.common.support.DefaultRenderer;
import com.idsscheer.webapps.arcm.ui.framework.renderer.context.IRenderContext;
import com.idsscheer.webapps.arcm.ui.framework.renderer.result.IRenderResult;
import com.idsscheer.webapps.arcm.ui.web.html.HTMLSelect;
import com.idsscheer.webapps.arcm.ui.web.html.IHTMLUtils;
import java.util.Map;


@AllowedFilterAttributeType({IBooleanFilterAttribute.class})
@AllowedAttributeType({com.idsscheer.webapps.arcm.config.metadata.objecttypes.AttributeDataType.BOOLEAN, com.idsscheer.webapps.arcm.config.metadata.objecttypes.AttributeDataType.LONG})
@DefaultRenderer({com.idsscheer.webapps.arcm.config.metadata.objecttypes.AttributeDataType.BOOLEAN})
public class CountFilterRenderer
  extends AbstractFilterRenderer
{
  protected static final String PARAM_SHOW_ALL = "showAll";
  protected static final String PARAM_ALL_KEY = "allKey";
  protected static final String PARAM_NO_KEY = "noKey";
  protected static final String PARAM_YES_KEY = "yesKey";
  @ConfigParameter(value="yesKey", type=ConfigParameterType.PROPERTY_KEY)
  protected String yesKey;
  @ConfigParameter(value="noKey", type=ConfigParameterType.PROPERTY_KEY)
  protected String noKey;
  @ConfigParameter(value="allKey", type=ConfigParameterType.PROPERTY_KEY)
  protected String allKey;
  @ConfigParameter("showAll")
  protected boolean showAll;
  protected Boolean data;
  
  public CountFilterRenderer() {}
  
  protected void renderWritable()
  {
    renderEditableFieldParam();
    this.context.getJsEventMap().put("onkeypress", "return aam_applyOnReturn(event, this.form);");
    HTMLSelect select = getHTMLHelper().createSelect(this.context.getAttributeName()).setEvents(getJSEvents()).setClassName("w80");
    

    if (this.showAll) {
      select.addOption(getStringValue(null), getMessage(this.allKey, new String[0]), getStringValue(this.data).equals(getStringValue(null)));
    }
    select.addOption(getStringValue(Boolean.FALSE), getMessage(this.noKey, new String[0]), getStringValue(this.data).equals(getStringValue(Boolean.FALSE)));
    select.addOption(getStringValue(Boolean.TRUE), getMessage(this.yesKey, new String[0]), getStringValue(this.data).equals(getStringValue(Boolean.TRUE)));
    
    this.result.append(select);
  }
  



  protected String getStringValue(Boolean p_data)
  {
    if (null == p_data) {
      return "-1";
    }
    if (p_data.booleanValue()) {
      return "1";
    }
    return "0";
  }
  
  protected void init() {
    this.yesKey = getParameter("yesKey", "html.yes");
    this.noKey = getParameter("noKey", "html.no");
    this.allKey = getParameter("allKey", "html.all");
    this.showAll = getBooleanParameter("showAll", Boolean.TRUE).booleanValue();
    
    if(this.context.getAttributeRawValue(this.context.getAttributeName()) == null)
    	this.data = null;
    else if(this.context.getAttributeRawValue(this.context.getAttributeName()) instanceof Boolean)
    	this.data = (Boolean)this.context.getAttributeRawValue(this.context.getAttributeName());
    else if((Long)this.context.getAttributeRawValue(this.context.getAttributeName()) == -1)
    	this.data = null;
    else
    	this.data = (Long)this.context.getAttributeRawValue(this.context.getAttributeName()) != 0L;
    
  }
}
