/**
 * 
 */
package com.idsscheer.webapps.arcm.common.constants.metadata.attribute;

import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IDateAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IListAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IStringAttributeType;
import static com.idsscheer.webapps.arcm.common.constants.metadata.attribute.MetadataConstantsUtil.*;

/**
 * <p>  this interface defines all attribute-types and attribute-names of TESTCASE IAppObj implementation.
 *      names of common attributes e.g. OBJ_ID and GUID are defined in {@link IObjectAttributeType} 
 *      additional attribute-names of versionized objects are defined in {@link IVersionAttributeType}
 * </p>
 *
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 *@author brmob
 *@since v20150314-MNT-UATEnhancements
 *@see ITestcaseAttributeType
 */
public interface ITestcaseAttributeTypeBB extends ITestcaseAttributeType {
	
	public static final String STR_VALIDACAO_REFERENCIA = "bb_validacaoReferencia";
	public static final String STR_INICIO_CICLO_AVALIATORIO = "bb_inicioClicloAvaliatorio";
	public static final String STR_FIM_CICLO_AVALIATORIO = "bb_fimClicloAvaliatorio";
	public static final String STR_DEFMANAGERL1_GROUP = "bb_defManagerL1_group";
	public static final String STR_CONTROLPROCESS = "BB_control_process";
	
	public static final IStringAttributeType ATTR_VALIDACAO_REFERENCIA = create(OBJECT_TYPE, STR_VALIDACAO_REFERENCIA);
	public static final IDateAttributeType ATTR_INICIO_CICLO_AVALIATORIO = create(OBJECT_TYPE, STR_INICIO_CICLO_AVALIATORIO);
	public static final IDateAttributeType ATTR_FIM_CICLO_AVALIATORIO = create(OBJECT_TYPE, STR_FIM_CICLO_AVALIATORIO);
	public static final IListAttributeType LIST_DEFMANAGERL1_GROUP = create(OBJECT_TYPE, STR_DEFMANAGERL1_GROUP);
	public static final IListAttributeType LIST_CONTROLPROCESS = create(OBJECT_TYPE, STR_CONTROLPROCESS);
	
}
