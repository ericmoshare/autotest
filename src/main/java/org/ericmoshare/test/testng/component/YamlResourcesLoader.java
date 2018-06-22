package org.ericmoshare.test.testng.component;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author eric.mo
 * @since 2018/6/25
 */
public class YamlResourcesLoader extends ResourceLoader {

    @Override
    Object parse(String content) throws IOException {
        Yaml yaml = new Yaml();
        return yaml.loadAs(content, List.class);
    }

    @Override
    String getFileSuffix() throws IOException {
        return "yml";
    }

    public Object[][] parseYamlToArray(String content) {
        Yaml yaml = new Yaml();
        List<Map> list = yaml.loadAs(content, List.class);

        Object[][] result = new Object[list.size()][];

        for (int i = 0; i < list.size(); i++) {
            result[i] = new Object[]{list.get(i)};
        }
        return result;
    }
}
