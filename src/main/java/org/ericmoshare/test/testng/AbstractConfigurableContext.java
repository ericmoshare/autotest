package org.ericmoshare.test.testng;

import org.ericmoshare.test.testng.component.DataSourceConfigurer;
import org.ericmoshare.test.testng.component.ResourceLoader;
import org.ericmoshare.test.testng.component.YamlResourcesLoader;
import org.ericmoshare.test.testng.entity.MyConstants;
import org.ericmoshare.test.testng.entity.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author eric.mo
 * @since 2018/6/25
 */
public abstract class AbstractConfigurableContext extends AbstractTestNGSpringContextTests {

    protected static final Logger log = LoggerFactory.getLogger(AbstractConfigurableContext.class);

    protected JdbcTemplate jdbcTemplate;

    protected Scenario scenario;

    @BeforeClass
    public void beforeClass() throws IllegalAccessException {
        DataSourceConfigurer configurer = new DataSourceConfigurer();
        try {
            InputStream is = getResourceAsStream();
            if (is != null) {
                jdbcTemplate = configurer.init(is);
            } else {
                jdbcTemplate = configurer.init(getDataSourceConfiguration());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DataProvider(name = "defaultData")
    public Iterator<Object[]> defaultData() throws IOException {
        ResourceLoader resourceLoader = new YamlResourcesLoader();
        List<Map> list = (List<Map>) resourceLoader.load(getSubClass());

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

    protected abstract Class getSubClass();

    protected Scenario getScenario() {
        return scenario;
    }

    protected JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    protected InputStream getResourceAsStream() {
        return null;
    }

    protected abstract Map<String, String> getDataSourceConfiguration() throws Exception;

    void validateError(Exception throwable) throws Exception {

        String errorMsg = String.valueOf(scenario.getError());

        Assert.assertNotNull(throwable);
        Assert.assertEquals(String.valueOf(throwable.getMessage()), String.valueOf(errorMsg));

    }

    void validateExpected(Map<String, Object> expected, Map resultAsMap) throws Exception {

        for (Map.Entry<String, Object> entry : expected.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            log.info("expected {}:{} vs {}:{}", key, value, key, resultAsMap.get(key));
            Assert.assertEquals(String.valueOf(resultAsMap.get(key)), String.valueOf(value));

        }
    }
}
