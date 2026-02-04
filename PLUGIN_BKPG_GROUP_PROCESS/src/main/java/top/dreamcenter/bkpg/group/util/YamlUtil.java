package top.dreamcenter.bkpg.group.util;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import top.dreamcenter.bkpg.group.bean.ProcessConfigBean;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class YamlUtil {

    public static ProcessConfigBean getProcessConfig(String path) throws IOException {
        Yaml yaml = new Yaml(new Constructor(ProcessConfigBean.class));

        try (InputStream in = new FileInputStream(path)) {
            return yaml.load(in);
        }

    }

}
