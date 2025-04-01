package top.dreamcenter.bkpg.group;

import top.dreamcenter.bkpg.protocal.MiniApp;
import top.dreamcenter.bkpg.protocal.MiniAppGroup;
import top.dreamcenter.bkpg.protocal.template.ProcessAppTemplate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class ProcessGroupApp implements MiniAppGroup {
    @Override
    public List<MiniApp> getMiniApps() {

        List<MiniApp> list = new LinkedList<>();


        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("ext/ProgressGroup.txt")))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(": ");

                String appName = split[0];
                String appCmd = split[1];

                MiniApp miniApp = new ProcessAppTemplate() {
                    @Override
                    public String getCmdStr() {
                        return appCmd;
                    }

                    @Override
                    public String getName() {
                        return appName;
                    }
                };

                list.add(miniApp);
            }

        } catch (Exception e){
            System.err.println(e.getMessage());
            return list;
        }

        return list;
    }

}
