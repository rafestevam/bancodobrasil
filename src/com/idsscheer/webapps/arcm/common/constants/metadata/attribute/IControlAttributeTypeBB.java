/**
 * 
 */
package com.idsscheer.webapps.arcm.common.constants.metadata.attribute;

import static com.idsscheer.webapps.arcm.common.constants.metadata.attribute.MetadataConstantsUtil.create;

import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IBooleanAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IEnumAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IListAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.ITextAttributeType;

/**
 * <p>  this interface defines all attribute-types and attribute-names of CONTROL IAppObj implementation.
 *      names of common attributes e.g. OBJ_ID and GUID are defined in {@link IObjectAttributeType} 
 *      additional attribute-names of versionized objects are defined in {@link IVersionAttributeType}
 * </p>
 *
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 *@author brmob
 *@since v20150126-CUST-Compliance
 *@see IControlAttributeType
 */
public interface IControlAttributeTypeBB extends IControlAttributeType {
	
	final static String STR_AFIRMACOES_CONTROLE = "bb_afirmacoesControle";
	final static String STR_ASSERTIONS = "bb_controlAssertions";
	final static String STR_APPSYSTEMS = "bb_appSystems";
	final static String STR_EVIDENCIAS_CONTROLE = "bb_evidenciasControle";
	final static String STR_NIVEL_CONTROLE = "bb_nivelControle";
	final static String STR_SCOPE = "bb_scope";
	final static String STR_SALVA_ATIVOS = "bb_salvaAtivos";
	final static String STR_CONTROLE_ANTIFRAUDE = "bb_controleAntifraude";
	final static String STR_RISCO_FALHA = "bb_riscoFalha";
	final static String STR_PLANILHA_DEPTARTAMENTAL = "bb_planilhaDept";
	final static String STR_CONFORMIDADE = "bb_conformidade";
	final static String STR_PROCESS = "BB_control_process";
	
	final IEnumAttributeType ATTR_AFIRMACOES_CONTROLE = create(OBJECT_TYPE, STR_AFIRMACOES_CONTROLE);
	final IEnumAttributeType ATTR_ASSERTIONS = create(OBJECT_TYPE, STR_ASSERTIONS);
	final IListAttributeType LIST_APPSYSTEMS = create(OBJECT_TYPE, STR_APPSYSTEMS);
	final ITextAttributeType ATTR_EVIDENCIAS_CONTROLE = create(OBJECT_TYPE, STR_EVIDENCIAS_CONTROLE);
	final IEnumAttributeType ATTR_NIVEL_CONTROLE = create(OBJECT_TYPE, STR_NIVEL_CONTROLE);
	final IEnumAttributeType ATTR_SCOPE = create(OBJECT_TYPE, STR_SCOPE);
	final IBooleanAttributeType ATTR_SALVA_ATIVOS = create(OBJECT_TYPE, STR_SALVA_ATIVOS);
	final IBooleanAttributeType ATTR_CONTROLE_ANTIFRAUDE = create(OBJECT_TYPE, STR_CONTROLE_ANTIFRAUDE);
	final IEnumAttributeType ATTR_RISCO_FALHA = create(OBJECT_TYPE, STR_RISCO_FALHA);
	final ITextAttributeType ATTR_PLANILHA_DEPARTAMENTAL = create(OBJECT_TYPE, STR_PLANILHA_DEPTARTAMENTAL);
	final IBooleanAttributeType ATTR_CONFORMIDADE = create(OBJECT_TYPE, STR_CONFORMIDADE);
	final IListAttributeType LIST_PROCESS = create(OBJECT_TYPE, STR_PROCESS);
}
