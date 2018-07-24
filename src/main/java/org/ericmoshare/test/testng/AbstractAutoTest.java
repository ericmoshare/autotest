package org.ericmoshare.test.testng;

import org.ericmoshare.test.testng.entity.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author eric.mo
 * @since 2018/6/21
 */
public abstract class AbstractAutoTest extends AbstractConfigurableContext {

    protected static final Logger log = LoggerFactory.getLogger(AbstractAutoTest.class);

    @Test(dataProvider = "defaultData", expectedExceptions = Exception.class)
    public void test(Scenario scenario) throws Exception {
        super.scenario = scenario;
        log.info("run scenario={}", scenario);

        Map param = scenario.getData();

        given(param);

        Exception throwable = null;
        try {
            when(scenario.getData());
        } catch (Exception e) {
            throwable = e;
            e.printStackTrace();
        }

        if (scenario.getError() != null) {
            validateError(throwable);
        }

        if (scenario.getExpected() != null) {
            Map expected = (Map) scenario.getExpected();

            Map resultAsMap = expect(scenario.getData());

            validateExpected(expected,resultAsMap);

        }

        throw new IllegalArgumentException("Normal Execption, skiped");
    }

    protected abstract void given(Map datas) throws Exception;

    protected abstract void when(Map datas) throws Exception;

    protected Map expect(Map datas) throws Exception {
        return new HashMap();
    }

}
