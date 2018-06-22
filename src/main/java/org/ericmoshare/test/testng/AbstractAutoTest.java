package org.ericmoshare.test.testng;

import org.ericmoshare.test.testng.entity.MyConstants;
import org.ericmoshare.test.testng.entity.Scenario;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * @author eric.mo
 * @since 2018/6/21
 */
public abstract class AbstractAutoTest extends AbstractTestNGSpringContextTests {

    protected static final Logger log = LoggerFactory.getLogger(AbstractAutoTest.class);

    private JdbcTemplate jdbcTemplate;

    protected void initDB() {
        InputStream is = null;
        try {
            is = getResourceAsStream();
        } catch (Exception e) {
            return;
        }

        Properties pro = new Properties();
        try {
            pro.load(is);
        } catch (IOException e) {
            return;
        }

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(pro.getProperty("database.driverClassName"));
        dataSource.setUrl(pro.getProperty("database.url"));
        dataSource.setUsername(pro.getProperty("database.username"));
        dataSource.setPassword(pro.getProperty("database.password"));

        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * clean table
     *
     * @param tableName
     */
    protected void clean(String tableName) {
        String sql = "delete from " + tableName;
        System.out.println(sql);
        jdbcTemplate.execute(sql);
    }

    @BeforeClass
    public void beforeClass() throws IllegalAccessException {
        initDB();

    }

    protected String load(Class clazz, String filetype) throws IOException {
        URL url = clazz.getClassLoader().getResource(clazz.getSimpleName() + "." + filetype);
        System.out.println("load file from url:" + url.getPath());
        return FileUtils.readFileToString(new File(url.getPath()));
    }

    protected Object loadYaml(String content) {
        Yaml yaml = new Yaml();
        return yaml.load(content);
    }

    protected Object[][] parseYamlToArray(String content) {
        Yaml yaml = new Yaml();
        List<Map> list = yaml.loadAs(content, List.class);

        Object[][] result = new Object[list.size()][];

        for (int i = 0; i < list.size(); i++) {
            result[i] = new Object[]{list.get(i)};
        }
        return result;
    }

    @DataProvider(name = "defaultData")
    public Iterator<Object[]> defaultData() throws IOException {
        String fileContent = load(getSubClass(), "yml");
        Yaml yaml = new Yaml();
        List<Map> list = yaml.loadAs(fileContent, List.class);

        List<Object[]> scenarios = new LinkedList<>();

        for (int i = 0; i < list.size(); i++) {

            Map map = list.get(i);

            Scenario scenario = new Scenario();
            scenario.setName((String) map.get(MyConstants.NAME));
            scenario.setData(map);

            scenarios.add(new Object[]{scenario});
        }

        return scenarios.iterator();
    }

    protected Object[][] provideData(Class type, String filetype) throws IOException {
        String fileContent = load(type, filetype);
        return parseYamlToArray(fileContent);
    }

    @Test(dataProvider = "defaultData", expectedExceptions = RuntimeException.class)
    public void test(Scenario scenario) throws Throwable {
        Map param = scenario.getData();

        given(param);

        Throwable throwable = null;
        try {
            when(scenario.getData());
        } catch (IllegalArgumentException e) {
            throwable = e;
            e.printStackTrace();
        }

        if (scenario.getError() != null) {
            String errorMsg = String.valueOf(scenario.getError());

            Assert.assertNotNull(throwable);
            Assert.assertTrue(throwable.getMessage().contains(errorMsg));

            throw throwable;
        }

        if (scenario.getExpected() != null) {
            Map<String, Object> expected = (Map) scenario.getExpected();

            Map resultAsMap = expect(scenario.getData());

            for (Map.Entry<String, Object> entry : expected.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                log.info("expected {}:{} vs {}:{}", key, value, key, resultAsMap.get(key));
                Assert.assertEquals(String.valueOf(resultAsMap.get(key)), value);

            }

        }

        throw new IllegalArgumentException("Normal Execption, skiped");
    }

    protected abstract InputStream getResourceAsStream() throws Exception;

    protected abstract void given(Map param) throws Exception;

    protected abstract void when(Map param) throws Exception;

    protected Map expect(Map param) throws Exception {
        return null;
    }

    protected abstract Class getSubClass();
}
