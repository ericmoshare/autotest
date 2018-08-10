package org.ericmoshare.test;

import org.ericmoshare.test.testng.AbstractAutoTest2;
import org.ericmoshare.test.testng.annotation.Expect;
import org.ericmoshare.test.testng.annotation.Given;
import org.ericmoshare.test.testng.annotation.Param;
import org.ericmoshare.test.testng.annotation.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author eric.mo
 * @since 2018/6/22
 */
@Configuration
@ContextConfiguration(locations = {"classpath*:spring-demo.xml"})
@ActiveProfiles(value = "development")
public class SimpleAutoTest2 extends AbstractAutoTest2 {

    protected static final Logger log = LoggerFactory.getLogger(SimpleAutoTest2.class);

    public SimpleAutoTest2() {
        super();
    }

    @Override
    protected Class getSubClass() {
        return this.getClass();
    }

    @Given
    public void given(@Param("a") String aaa, @Param("b") String bbb, @Param("#request.b1") String b1, @Param("#request") Map request) {
        log.info("get params a:{}, b:{}", aaa, bbb);
        log.info("get params b1:{}", b1);
        log.info("get params request:{}", request);
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

    @When
    public void when(@Param("#data") Map data, @Param("#request") Map request) throws Exception {
        log.info("get params datas:{}", data);
        log.info("get params request:{}", request);
    }

    @Expect
    public Map expect(@Param("a") String aaa, @Param("b") String bbb) throws RuntimeException {
        log.info("get params a:{}, b:{}", aaa, bbb);

        int a = Integer.parseInt(aaa);
        int b = Integer.parseInt(bbb);

        Map map = new HashMap();
        map.put("sum", a + b);
        return map;
    }
}
