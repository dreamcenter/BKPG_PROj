package top.dreamcenter.bkpg.util;

import java.io.File;
import java.io.FilenameFilter;

public class JarFileFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String name) {
        return name.endsWith(".jar");
    }
}
