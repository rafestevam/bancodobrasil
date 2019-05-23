package com.idsscheer.webapps.arcm.ui.framework.renderer;

import com.idsscheer.webapps.arcm.common.support.ConfigParameter;
import com.idsscheer.webapps.arcm.ui.framework.support.StringUtils;
import com.idsscheer.webapps.arcm.ui.web.html.HTMLInput;

public class TextFieldRendererBB extends TextFieldRenderer {
	public static final String PARAM_CHAR_LIMIT = "charLimit";

	@ConfigParameter(PARAM_CHAR_LIMIT)
	
    /**
     * the height in rows of the HTML control
     */	
	protected String charLimit = "-1";
	
	protected void init() throws RenderingException {
		super.init();
		charLimit = getParameter(PARAM_CHAR_LIMIT, "-1");
	}
	
	protected void renderReadonly() throws RenderingException {
		if (null != data) {
            if (0 < size) {
                result.append("<div style=\"text-align:" + alignment + ";width:").append(size * 5 + 50).append("px;\">");
            }
            final HTMLInput input = getHTMLHelper().createInput(HTMLInput.TYPE_HIDDEN,
					null,
					context.getAttributeName(),
					StringUtils.escape(data.toString()));
			result.append(input);
			if (null != maxlength) {
				result.append("<nobr>");
			}
			if (null != propertyKey) {
				result.append(getMessage(propertyKey)).append("&nbsp;");
			}
			String text = data.toString();
			int count = Integer.parseInt(charLimit);
			if (!charLimit.equals("-1") && text.length() >= count) {
				if (text.length() == count) {
					text = text.substring(0, count);
				}
				else {
					text = text.substring(0, count - 1) + "&#x85;";
				}	
			}
			result.append(StringUtils.escapeSpaces(text));
			if (null != maxlength) {
				for (int i = data.toString().length(); i < Integer.parseInt(maxlength); i++) {
					result.append("&nbsp;&nbsp;");
				}
			}
			if (null != maxlength) {
				result.append("</nobr>");
			}
            if (0 < size) {
                result.append("</div>");
            }
		}
	}	
}