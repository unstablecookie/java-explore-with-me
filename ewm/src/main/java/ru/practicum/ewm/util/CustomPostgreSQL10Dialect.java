package ru.practicum.ewm.util;

import org.hibernate.dialect.PostgreSQL10Dialect;
import ru.practicum.ewm.locations.sql.CustomLocationShortArrayType;

public class CustomPostgreSQL10Dialect extends PostgreSQL10Dialect {
    public CustomPostgreSQL10Dialect() {
        super();
        this.registerHibernateType(2003, CustomLocationShortArrayType.class.getName());
    }
}
