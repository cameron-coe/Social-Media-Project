package com.creditcardcomparison.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

public class PgAdminConnection {

    private String host = "localhost";
    private int portNumber = 5432;
    private String databaseName = "social_media";
    private String user = "postgres";
    private String password = "postgres1";

    private DataSource dataSource;

    public PgAdminConnection() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerName(host);
        dataSource.setPortNumber(portNumber);
        dataSource.setDatabaseName(databaseName);
        dataSource.setUser(user);
        dataSource.setPassword(password);
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
