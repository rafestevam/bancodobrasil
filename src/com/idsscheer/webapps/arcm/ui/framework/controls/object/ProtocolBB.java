package com.idsscheer.webapps.arcm.ui.framework.controls.object;

import java.util.Map;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.ui.framework.controls.context.IControlContext;

public class ProtocolBB extends Protocol {

	private IAppObj m_leftObject;
	private IAppObj m_rightObject;

	public ProtocolBB(String p_instanceName, Map<String, String> p_parameters) {
		super(p_instanceName, p_parameters);
	}

	@Override
	protected void renderVersionSelect() {
		Boolean renderToolbar = (Boolean) getRequestContext().getAttribute("__toolbar");
		if ((renderToolbar == null) || (renderToolbar.booleanValue())) {
			this.buffer.append("<tr><td class=\"SEPARATOR\" colspan=\"6\">&nbsp;</td></tr>");
		}

		Long choosenVerisonLeft = Long.valueOf(this.m_leftObject.getVersionData().getOVID().getVersion());
		Long choosenVerisonRight = Long.valueOf(this.m_rightObject.getVersionData().getOVID().getVersion());

		if ((choosenVerisonLeft.equals(choosenVerisonRight))
				&& (!this.m_rightObject.isDirty())) {
			this.buffer.append("<tr><td class=\"NOTIFICATION_COLORS_INFO NOTIFICATION_SIZES_OBJECT\" colspan=\"6\">")
					.append(this.environment.getMessage("protocol.version_hint",new String[] { choosenVerisonRight.toString() }))
					.append("</td></tr>");
		}

		this.buffer.append("<tr>")
				.append("<th class=\"PROTOCOL_LABEL_WIDTH PROTOCOL_HEADER_LABEL PROTOCOL_HEADER contentfont\">")
				.append(this.environment.getMessage("form.version.DBI",new String[0]))
				.append("</th>")
				.append("<td class=\"PROTOCOL_VERTICAL_SPLIT PROTOCOL_HEADER\"></td>")
				.append("<td class=\"PROTOCOL_HEADER_DATA TOP_SPACE LEFT_RIGHT_WIDTH PROTOCOL_HEADER_BG contentfont\">")
				.append(getChooseVersion(this.m_leftObject, "protocol_ver_left"))
				.append("</td>")
				.append("<td class=\"PROTOCOL_VERTICAL_SPLIT PROTOCOL_HEADER\"></td>")
				.append("<td class=\"PROTOCOL_HEADER_DATA TOP_SPACE LEFT_RIGHT_WIDTH PROTOCOL_HEADER_BG contentfont\">")
				.append(getChooseVersion(this.m_rightObject,
						"protocol_ver_right")).append("</td>").append("</tr>");

		this.buffer.append("<tr>")
				.append("<th class=\"PROTOCOL_LABEL_WIDTH PROTOCOL_HEADER_LABEL PROTOCOL_HEADER contentfont\">")
				.append(this.environment.getMessage("form.performed.by.DBI",new String[0]))
				.append("</th>")
				.append("<td class=\"PROTOCOL_VERTICAL_SPLIT PROTOCOL_HEADER\"></td>")
				.append("<td class=\"PROTOCOL_HEADER_DATA TOP_SPACE LEFT_RIGHT_WIDTH PROTOCOL_HEADER contentfont\">")
				.append(getUserName(this.m_leftObject))
				.append("</td>")
				.append("<td class=\"PROTOCOL_VERTICAL_SPLIT PROTOCOL_HEADER\"></td>")
				.append("<td class=\"PROTOCOL_HEADER_DATA TOP_SPACE LEFT_RIGHT_WIDTH PROTOCOL_HEADER contentfont\">")
				.append(getUserName(this.m_rightObject)).append("</td>")
				.append("</tr>");
		
		/** BEGIN BDB : Enhancement #114550 : TC.GC.298 - Explorer/Gestão de Teste / caso de teste (analista) - Transferir Caso de teste **/
		if((ObjectType.TESTCASE.equals(m_leftObject.getObjectType()))
				|| (ObjectType.RISKASSESSMENT.equals(m_leftObject.getObjectType()))
				|| (ObjectType.QUESTIONNAIRE.equals(m_leftObject.getObjectType()))
				|| (ObjectType.POLICYREVIEW.equals(m_leftObject.getObjectType()))) {
			this.buffer.append("<tr>")
			.append("<th class=\"PROTOCOL_LABEL_WIDTH PROTOCOL_HEADER_LABEL PROTOCOL_HEADER contentfont\">")
			.append(this.environment.getMessage("form.uploaded.by.DBI",new String[0]))
			.append("</th>")
			.append("<td class=\"PROTOCOL_VERTICAL_SPLIT PROTOCOL_HEADER\"></td>")
			.append("<td class=\"PROTOCOL_HEADER_DATA TOP_SPACE LEFT_RIGHT_WIDTH PROTOCOL_HEADER contentfont\">")
			.append(getUserName(this.m_leftObject))
			.append("</td>")
			.append("<td class=\"PROTOCOL_VERTICAL_SPLIT PROTOCOL_HEADER\"></td>")
			.append("<td class=\"PROTOCOL_HEADER_DATA TOP_SPACE LEFT_RIGHT_WIDTH PROTOCOL_HEADER contentfont\">")
			.append(getUserName(this.m_leftObject)).append("</td>")
			.append("</tr>");
		}
		/** END BDB **/

		this.buffer.append("<tr>")
				.append("<th class=\"PROTOCOL_LABEL_WIDTH PROTOCOL_HEADER_LABEL PROTOCOL_HEADER contentfont\">")
				.append(this.environment.getMessage("form.changedate.DBI",new String[0]))
				.append("</th>")
				.append("<td class=\"PROTOCOL_VERTICAL_SPLIT PROTOCOL_HEADER\"></td>")
				.append("<td class=\"PROTOCOL_HEADER_DATA TOP_SPACE LEFT_RIGHT_WIDTH PROTOCOL_HEADER contentfont\">")
				.append(getDisplayedDate(this.m_leftObject))
				.append("</td>")
				.append("<td class=\"PROTOCOL_VERTICAL_SPLIT PROTOCOL_HEADER\"></td>")
				.append("<td class=\"PROTOCOL_HEADER_DATA TOP_SPACE LEFT_RIGHT_WIDTH PROTOCOL_HEADER contentfont\">")
				.append(getDisplayedDate(this.m_rightObject)).append("</td>")
				.append("</tr>");

		this.buffer.append("<tr>")
				.append("<th class=\"PROTOCOL_LABEL_WIDTH PROTOCOL_HEADER_LABEL PROTOCOL_HEADER contentfont\">")
				.append(this.environment.getMessage("form.arischangedate.DBI",new String[0]))
				.append("</th>")
				.append("<td class=\"PROTOCOL_VERTICAL_SPLIT PROTOCOL_HEADER\"></td>")
				.append("<td class=\"PROTOCOL_HEADER_DATA TOP_SPACE LEFT_RIGHT_WIDTH PROTOCOL_HEADER contentfont\">")
				.append(getDisplayedARISDate(this.m_leftObject))
				.append("</td>")
				.append("<td class=\"PROTOCOL_VERTICAL_SPLIT PROTOCOL_HEADER\"></td>")
				.append("<td class=\"PROTOCOL_HEADER_DATA TOP_SPACE LEFT_RIGHT_WIDTH PROTOCOL_HEADER contentfont\">")
				.append(getDisplayedARISDate(this.m_rightObject))
				.append("</td>").append("</tr>");

		String changeTypeProtocolSame = "";
		if (!getChangeType(this.m_leftObject).equals(getChangeType(this.m_rightObject)))
			changeTypeProtocolSame = "protocol-modified";

		this.buffer.append("<tr><td class=\"HEADER_TO_CONTENT_SEPARATOR\" colspan=\"5\">&nbsp;</td></tr>");

		this.buffer.append("<tr>")
				.append("<th class=\"PROTOCOL_LABEL_WIDTH PROTOCOL_HEADER_LABEL PROTOCOL_HEADER contentfont\">")
				.append(this.environment.getMessage("form.changetype.DBI",new String[0]))
				.append("</th>")
				.append("<td class=\"PROTOCOL_VERTICAL_SPLIT PROTOCOL_HEADER\"></td>")
				.append("<td class=\"PROTOCOL_HEADER_DATA TOP_SPACE LEFT_RIGHT_WIDTH PROTOCOL_HEADER contentfont "+ changeTypeProtocolSame + "  \">")
				.append(getChangeType(this.m_leftObject))
				.append("</td>")
				.append("<td class=\"PROTOCOL_VERTICAL_SPLIT PROTOCOL_HEADER "+ changeTypeProtocolSame + " \"></td>")
				.append("<td class=\"PROTOCOL_HEADER_DATA TOP_SPACE LEFT_RIGHT_WIDTH PROTOCOL_HEADER contentfont "+ changeTypeProtocolSame + " \">")
				.append(getChangeType(this.m_rightObject)).append("</td>")
				.append("</tr>");

		this.buffer.append("<tr><td class=\"HEADER_TO_CONTENT_SEPARATOR\" colspan=\"5\">&nbsp;</td></tr>");
	}
	
	@Override
	public void init(IControlContext p_context) {
		super.init(p_context);
		
		this.m_leftObject = ((IAppObj)getRequestContext().getAttribute("protocol_obj_left"));
		this.m_rightObject = ((IAppObj)getRequestContext().getAttribute("protocol_obj_right"));
	}
}
