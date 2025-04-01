package top.dreamcenter.bkpg.demo;

import top.dreamcenter.bkpg.protocal.template.ProcessAppTemplate;

public class DemoApp2 extends ProcessAppTemplate {
    private static final String DEMO_APP = "案例程序2";

    @Override
    public String getName() {
        return DEMO_APP;
    }

    @Override
    public String getCmdStr() {
        return "ping 81.70.80.152 -t";
    }
}
