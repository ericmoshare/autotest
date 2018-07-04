package org.ericmoshare.test.testng.component;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.Assert;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * @author eric.mo
 * @since 2018/6/25
 */
public class DataSourceConfigurer {

    /**
     * init datasource
     *
     * @param is
     * @return JdbcTemplate
     */
    public JdbcTemplate init(InputStream is) {
        Assert.notNull(is, "InputStream must not be null");

        Properties pro = new Properties();
        try {
            pro.load(is);
        } catch (Exception e) {
            System.out.println("load properties cause error, " + e.getMessage());
        }

        DriverManagerDataSource dataSource = null;
        try {
            dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName(pro.getProperty("database.driverClassName"));
            dataSource.setUrl(pro.getProperty("database.url"));
            dataSource.setUsername(pro.getProperty("database.username"));
            dataSource.setPassword(pro.getProperty("database.password"));
        } catch (Exception e) {
            System.out.println("init dataSource cause error");
            e.printStackTrace();
        }

        return new JdbcTemplate(dataSource);
    }

    /**
     * init datasource
     *
     * @param data
     * @return JdbcTemplate
     */
    public JdbcTemplate init(Map<String, String> data) {

        DriverManagerDataSource dataSource = null;
        try {
            dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName(data.get("driverClassName"));
            dataSource.setUrl(data.get("url"));
            dataSource.setUsername(data.get("username"));
            dataSource.setPassword(data.get("password"));
        } catch (Exception e) {
            System.out.println("init dataSource cause error");
            e.printStackTrace();
        }

        return new JdbcTemplate(dataSource);
    }
}
