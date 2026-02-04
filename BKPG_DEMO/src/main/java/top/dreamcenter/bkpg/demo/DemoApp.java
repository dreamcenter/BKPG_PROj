package top.dreamcenter.bkpg.demo;

import top.dreamcenter.bkpg.demo.ui.MainPanel;
import top.dreamcenter.bkpg.protocal.MiniApp;

import javax.swing.*;

public class DemoApp implements MiniApp {

    private static final String DEMO_APP = "案例程序";

    @Override
    public String getName() {
        return DEMO_APP;
    }

    @Override
    public JPanel getPanel() {
        MainPanel mainPanel = new MainPanel();
        mainPanel.add(new JLabel(getName()));
        return mainPanel;
    }

    @Override
    public boolean protect() {
        return false;
    }
}
