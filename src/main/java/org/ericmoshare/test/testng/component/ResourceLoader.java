package org.ericmoshare.test.testng.component;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author eric.mo
 * @since 2018/6/25
 */
public abstract class ResourceLoader {

    /**
     * load properties
     *
     * @param clazz
     * @return
     * @throws IOException
     */
    public Object load(Class clazz) throws IOException {

        String fileContent = getContent(clazz, getFileSuffix());
        return parse(fileContent);
    }

    /**
     * get file content from url
     *
     * @param clazz
     * @param suffix
     * @return file content
     * @throws IOException if file is not existed.
     */
    String getContent(Class clazz, String suffix) throws IOException {
        URL url = clazz.getClassLoader().getResource(clazz.getSimpleName() + "." + suffix);
        System.out.println("load file from url:" + url.getPath());
        return FileUtils.readFileToString(new File(url.getPath()));
    }

    /**
     * parse content
     *
     * @param content
     * @return result
     * @throws IOException
     */
    abstract Object parse(String content) throws IOException;

    /**
     * get fileSuffix, such as yaml or yml
     *
     * @return result
     * @throws IOException
     */
    abstract String getFileSuffix() throws IOException;
}
