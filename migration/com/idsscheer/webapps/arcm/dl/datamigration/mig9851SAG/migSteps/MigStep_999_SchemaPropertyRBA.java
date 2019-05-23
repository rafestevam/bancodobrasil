package com.idsscheer.webapps.arcm.dl.datamigration.mig9851SAG.migSteps;

import java.sql.SQLException;

import com.idsscheer.webapps.arcm.dl.datamigration.exception.DDLMigrationException;
import com.idsscheer.webapps.arcm.dl.datamigration.mapping.IMapping;
import com.idsscheer.webapps.arcm.dl.datamigration.migrationstep.IMigrationStep;
import com.idsscheer.webapps.arcm.dl.datamigration.migrationstep.stepImpl.BaseMigrationStep;

public class MigStep_999_SchemaPropertyRBA extends BaseMigrationStep implements IMigrationStep{

	protected MigStep_999_SchemaPropertyRBA(String resourcePath) {
		super(MigStep_999_SchemaPropertyRBA.class.getSimpleName(), resourcePath);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(IMapping mapping) {
		// TODO Auto-generated method stub
		try {
            //empty A_SCHEMAPROPERTY_TBL
            String sql = "delete from A_SCHEMAPROPERTY_TBL where PROPERTYKEY = 'currentSchemaId'";
            mapping.executeSQL(sql);

            //insert new value into A_SCHEMAPROPERTY_TBL
            sql = "insert into A_SCHEMAPROPERTY_TBL (PROPERTYKEY, PROPERTYVALUE) values ('currentSchemaId', 'arcm_9.8.5.1_rba_SoftwareAG')";
            mapping.executeSQL(sql);

        } catch (SQLException e) {
            throw new DDLMigrationException("cannot migrate schema version entry inside A_SCHEMAPROPERTY_TBL", e);
        }
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
