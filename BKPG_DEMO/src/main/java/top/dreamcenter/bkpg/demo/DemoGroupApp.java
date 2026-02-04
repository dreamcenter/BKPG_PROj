package top.dreamcenter.bkpg.demo;

import top.dreamcenter.bkpg.protocal.MiniApp;
import top.dreamcenter.bkpg.protocal.MiniAppGroup;
import top.dreamcenter.bkpg.protocal.template.ProcessAppTemplate;

import java.util.LinkedList;
import java.util.List;

public class DemoGroupApp implements MiniAppGroup {
    @Override
    public List<MiniApp> getMiniApps() {

        List<MiniApp> list = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            int finalI = i;
            MiniApp miniApp = new ProcessAppTemplate() {
                @Override
                public String getCmdStr() {
                    return "ping www.baidu.com -n " + (finalI + 1);
                }

                @Override
                public String getName() {
                    return "GroupDemo" + finalI;
                }
            };
            list.add(miniApp);
        }

        return list;
    }

}
