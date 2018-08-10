package org.ericmoshare.test.testng;

import org.ericmoshare.test.testng.entity.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * @author eric.mo
 * @since 2018/6/21
 */
public abstract class AbstractAutoTest extends AbstractConfigurableContext {

    private static final String MESSAGE = "Normal Execption, skiped";
    protected static final Logger log = LoggerFactory.getLogger(AbstractAutoTest.class);

    @Test(dataProvider = "defaultData", expectedExceptions = Exception.class, expectedExceptionsMessageRegExp = MESSAGE)
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

        Object error = scenario.getError();

        if (error == null && throwable != null) {
            throw throwable;
        }

        if (error != null) {
            validateError(throwable);
        }

        if (scenario.getExpected() != null) {
            Map expected = (Map) scenario.getExpected();

            Map resultAsMap = expect(scenario.getData());

            validateExpected(expected, resultAsMap);

        }

        throw new IllegalArgumentException(MESSAGE);
    }

    protected abstract void given(Map datas) throws Exception;

    protected abstract void when(Map datas) throws Exception;

    protected abstract Map expect(Map datas) throws Exception;

}
