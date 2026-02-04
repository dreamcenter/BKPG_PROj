package top.dreamcenter.bkpg.demo;

import top.dreamcenter.bkpg.protocal.template.ProcessAppTemplate;

public class DemoApp3 extends ProcessAppTemplate {
    @Override
    public String getName() {
        return "案例程序3";
    }

    @Override
    public String getCmdStr() {
        return "ipconfig";
    }
}
