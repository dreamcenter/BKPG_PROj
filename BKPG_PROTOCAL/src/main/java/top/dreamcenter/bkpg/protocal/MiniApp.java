package top.dreamcenter.bkpg.protocal;

import javax.swing.*;

/**
 * 程序基类
 */
public interface MiniApp {

    /**
     * 获取应用名称
     * @return 应用名称
     */
    String getName();

    /**
     * 获取应用面板
     * @return 面板
     */
    JPanel getPanel();

    /**
     * 保护应用，重启的时候是否重新装载
     * @return
     */
    boolean protect();
}
