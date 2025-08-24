package com.kenyajug.core;
/*
 * MIT License
 *
 * Copyright (c) 2025 Kenya JUG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
public class DatabaseManager {
    private static final String sqlSchemaFile = "db_init.sql";
    public static String fetchDatabaseUrl() throws IOException {
        var propertiesFileName = "public_env.properties";
        var dbUrlKey = "db.url";
        var properties = new Properties();
        try(InputStream inputStream = DatabaseManager
                .class
                .getClassLoader()
                .getResourceAsStream(propertiesFileName)) {
            if (inputStream == null)
                throw new IOException("Failed to open properties file;File not found");
            properties.load(inputStream);
            return (String) properties.get(dbUrlKey);
        }
    }
    public static boolean initSchema() throws IOException, SQLException {
        String createTablesSql;
        try(InputStream inputStream = DatabaseManager.class
                .getClassLoader()
                .getResourceAsStream(sqlSchemaFile)){
            if (inputStream == null)
                throw new IOException("Failed to open schema sql file; File not found");
            createTablesSql = new String(inputStream.readAllBytes());
        }
        String[] queries = createTablesSql.split(";");
        var url = fetchDatabaseUrl();
        for (String query : queries) {
            Connection dbConnection = DriverManager.getConnection(url,"","");
            Statement statement = dbConnection.createStatement();
            statement.execute(query);
            dbConnection.close();
        }
        return true;
    }
    public static boolean clearDatabase() throws IOException,SQLException {
        try(InputStream inputStream = DatabaseManager.class
                .getClassLoader()
                .getResourceAsStream(sqlSchemaFile)){
            if (inputStream == null)
                throw new IOException("Failed to open schema sql file; File not found");
            var schemaSql = new String(inputStream.readAllBytes());
            var tables = extractSqlTableName(schemaSql);
            var dbUrl = fetchDatabaseUrl();
            try(Connection connection = DriverManager.getConnection(dbUrl)) {
                for (String table : tables) {
                    var statement = connection.createStatement();
                    statement.execute("DELETE FROM " + table);
                }
            }
        }
        return true;
    }
    public static List<String> extractSqlTableName(String rawSqlSchema){
        var tablesRegex = "(?i)CREATE\\s+TABLE\\s+(?:IF\\s+NOT\\s+EXISTS\\s+)?([A-Za-z_][A-Za-z0-9_]*)";
        var pattern = Pattern.compile(tablesRegex);
        var matcher = pattern.matcher(rawSqlSchema);
        List<String> tables = new ArrayList<>();
        while (matcher.find()){
            tables.add(matcher.group(1));
        }
        return tables;
    }
}
