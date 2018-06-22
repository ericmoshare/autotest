package org.ericmoshare.test.testng.entity;

import lombok.Data;

import java.util.Map;

/**
 * @author eric.mo
 * @since 2018/6/21
 */

@Data
public class Scenario {
    String name;
    Map data;
    Map expected;

    public Object getExpected() {
        return data.get(MyConstants.EXPECTED);
    }

    public Object getError() {
        return data.get(MyConstants.ERROR);
    }

    @Override
    public String toString() {
        return name;
    }
}
