package org.ericmoshare.test;

import org.ericmoshare.test.testng.AbstractAutoTest;
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
public class SimpleExtensionAutoTest extends AbstractAutoTest {

    public SimpleExtensionAutoTest() {
        super();
    }

    @Override
    protected InputStream getResourceAsStream() throws RuntimeException {
        return this.getClass().getClassLoader().getResourceAsStream("application.properties");
    }

    @Override
    protected Map expect(Map datas) throws RuntimeException {
        List list = (List) datas.get("data.2");

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
    protected void given(Map datas) throws RuntimeException {
        clean("person");
        clean("auth_role");
    }

    @Override
    protected void when(Map datas) throws RuntimeException {

    }
}
