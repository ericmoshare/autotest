package org.ericmoshare.test;

import org.ericmoshare.test.testng.AbstractAutoTest;
import org.ericmoshare.test.testng.annotation.Given;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author eric.mo
 * @since 2018/6/22
 */
@Configuration
@ContextConfiguration(locations = {"classpath*:spring-demo.xml"})
@ActiveProfiles(value = "development")
public class SimpleAutoTest extends AbstractAutoTest {

    public SimpleAutoTest() {
        super();
    }


    @Override
    protected void given(Map param) throws RuntimeException {
        clean("person");
        clean("auth_role");
    }

    @Given
    public void run(String aaa) {
        System.out.println("\n\nrun like never before");
    }


    @Override
    protected void when(Map param) throws RuntimeException {
        // log.info("scenario: {}", JSON.toJSONString(getScenario()));
    }

    @Override
    protected Map expect(Map param) throws RuntimeException {
        List list = (List) param.get("data");

        Map map = new HashMap();

        int sum = 0;
        for (Object index : list) {
            sum += Integer.valueOf(String.valueOf(index));
        }

        map.put("sum", sum);
        return map;
    }

    @Override
    protected Class getSubClass() {
        return this.getClass();
    }

    @Override
    protected Map<String, String> getDataSourceConfiguration() throws RuntimeException {
        Map<String, String> data = new HashMap<>();
        data.put("url", "jdbc:mysql://127.0.0.1:3306/pangu?characterEncoding=UTF8&useSSL=false");
        data.put("username", "root");
        data.put("password", "12345678");
        data.put("driverClassName", "com.mysql.jdbc.Driver");
        return data;
    }
}
