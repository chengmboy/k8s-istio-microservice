package io.github.chengmboy.kim.common.mysql;

import org.hibernate.dialect.MySQL57Dialect;

/**
 * @author cheng_mboy
 */
public class MySQL57InnodbNoForeignKeyDialect extends MySQL57Dialect {

    @Override
    public String getAddForeignKeyConstraintString(String constraintName, String foreignKeyDefinition) {
        return "";
    }

    @Override
    public String getAddForeignKeyConstraintString(String constraintName, String[] foreignKey, String referencedTable, String[] primaryKey, boolean referencesPrimaryKey) {
        return "";
    }
}
