package org.ericmoshare.test.testng;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.ArrayUtils;
import org.ericmoshare.test.testng.annotation.Expect;
import org.ericmoshare.test.testng.annotation.Given;
import org.ericmoshare.test.testng.annotation.Param;
import org.ericmoshare.test.testng.annotation.When;
import org.ericmoshare.test.testng.entity.Scenario;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.MethodParameterNamesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.testng.annotations.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author eric.mo
 * @since 2018/6/21
 */
public abstract class AbstractAutoTest2 extends AbstractConfigurableContext {

    private static final String SKIP_MESSAGE = "Normal Execption, skiped";
    private static final String JOINER = ".";
    private static final String HEAD = "#";

    protected static final Logger log = LoggerFactory.getLogger(AbstractAutoTest2.class);

    private Reflections reflections;

    @Test(dataProvider = "defaultData", expectedExceptions = Exception.class, expectedExceptionsMessageRegExp = SKIP_MESSAGE)
    public void test(Scenario scenario) throws Throwable {
        super.scenario = scenario;
        log.info("run scenario: {}", scenario);

        Map param = scenario.getData();

        log.info("get parameters: {}", JSON.toJSONString(param));

        Class subClass = getSubClass();
        log.debug("clazz: " + subClass.getName());

        String packageName = subClass.getPackage().getName();
        log.debug("scan package: " + packageName);

        if (reflections == null) {
            reflections = new Reflections(new ConfigurationBuilder()
                    .setUrls(ClasspathHelper.forPackage(packageName))
                    .setScanners(new SubTypesScanner(),
                            new MethodAnnotationsScanner(),
                            new MethodParameterNamesScanner()));

        }

        Set<Method> method4Given = reflections.getMethodsAnnotatedWith(Given.class);
        invokeMethod(method4Given, subClass, param);

        Exception throwable = null;
        try {

            Set<Method> method4When = reflections.getMethodsAnnotatedWith(When.class);
            invokeMethod(method4When, subClass, param);
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

            Set<Method> method4Expect = reflections.getMethodsAnnotatedWith(Expect.class);

            Map resultAsMap = (Map) invokeMethod(method4Expect, subClass, param);

            validateExpected(expected, resultAsMap);

        }

        throw new IllegalArgumentException(SKIP_MESSAGE);
    }

    private Object invokeMethod(Set<Method> methods, Class subClass, Map param) throws Throwable {

        for (Method method : methods) {
            if (method.getDeclaringClass().getName().equalsIgnoreCase(subClass.getName())) {
                log.debug("[init] method class: {}", method.getDeclaringClass().getName());
                return invokeMethod(method, subClass, param);
            }
        }

        return null;
    }

    private Object invokeMethod(Method method, Class subClass, Map param) throws Throwable {
        log.debug("[init] class:{}, method: {}", subClass.getName(), method.getName());

        Type[] paramsTypes = method.getParameterTypes();

        Map data = getDefaultMap(param, "data");

        Object result = null;

        if (paramsTypes.length > 0) {

            List<String> paramNameList = new ArrayList<String>();
            List<Object> paramValues = new ArrayList<Object>();

            Annotation[][] annotationDyadicArray = method.getParameterAnnotations();
            if (ArrayUtils.isNotEmpty(annotationDyadicArray)) {
                for (Annotation[] annotations : annotationDyadicArray) {
                    if (ArrayUtils.isNotEmpty(annotations)) {
                        for (Annotation anno : annotations) {
                            if (anno instanceof Param) {
                                paramNameList.add(((Param) anno).value());
                                break;
                            }
                        }
                    }
                }

                int i = 0;
                for (String name : paramNameList) {
                    log.debug("paramName:{}, paramsTypes:{}", name, paramsTypes[i]);

                    if (name.startsWith(HEAD) && !name.contains(JOINER)) {
                        String tmpName = name.substring(1, name.length());

                        log.debug("bucket name:{}", tmpName);

                        Map map = getDefaultMap(param, tmpName);

                        paramValues.add(map);

                    } else if (name.startsWith(HEAD) && name.contains(JOINER)) {
                        String tmpName = name.substring(1, name.indexOf(JOINER));

                        log.debug("bucket name:{}", tmpName);

                        Map map = getDefaultMap(param, tmpName);

                        String key = name.substring(name.indexOf(JOINER) + 1, name.length());

                        log.debug("key:{}", key);

                        paramValues.add(map.get(key));

                    } else if (name.contains(JOINER)) {
                        String[] pairs = StringUtils.tokenizeToStringArray(name, JOINER);

                        log.debug("pairs:{}", pairs);

                        Map map = getDefaultMap(param, pairs[0]);

                        paramValues.add(map.get(pairs[1]));

                    } else {
                        paramValues.add(data.get(name));
                    }

                    i++;
                }
            } else {
                log.debug("parameter annotations is empty {}");

                List<String> paramNames = reflections.getMethodParamNames(method);

                log.debug("paramNames:" + paramNames);

                int i = 0;
                for (String p : paramNames) {
                    log.debug("paramName:{}, paramsTypes:{}", p, paramsTypes[i]);

                    paramValues.add(data.get(p));

                    i++;
                }

            }

            log.debug("paramValues:{}", paramValues);

            if (CollectionUtils.isEmpty(paramValues)) {
                paramValues.add(data);
            }

            ReflectionUtils.makeAccessible(method);
            result = handleExecption(method, paramValues.toArray());
        } else {
            ReflectionUtils.makeAccessible(method);
            // result = ReflectionUtils.invokeMethod(method, this);
            result = handleExecption(method, null);
        }

        log.debug("result:" + result);

        return result;
    }

    private Map getDefaultMap(Map map, String name) {
        return map.get(name) == null ? new HashMap() : (Map) map.get(name);
    }

    private Object handleExecption(Method method, Object[] params) throws Throwable {
        try {
            if (params != null) {
                return ReflectionUtils.invokeMethod(method, this, params);
            }

            return ReflectionUtils.invokeMethod(method, this);
        } catch (Exception e) {
            log.error("invoke method catch error, {}", e.getMessage(), e);
            throw e.getCause();
        }
    }
}
