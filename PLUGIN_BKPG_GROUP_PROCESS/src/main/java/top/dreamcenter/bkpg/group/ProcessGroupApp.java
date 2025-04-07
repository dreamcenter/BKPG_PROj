package top.dreamcenter.bkpg.group;

import top.dreamcenter.bkpg.group.bean.ProcessConfigBean;
import top.dreamcenter.bkpg.group.util.YamlUtil;
import top.dreamcenter.bkpg.protocal.MiniApp;
import top.dreamcenter.bkpg.protocal.MiniAppGroup;
import top.dreamcenter.bkpg.protocal.template.ProcessAppTemplate;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class ProcessGroupApp implements MiniAppGroup {
    @Override
    public List<MiniApp> getMiniApps() {

        ProcessConfigBean processConfig = null;
        List<MiniApp> list = new LinkedList<>();
        try {
            processConfig = YamlUtil.getProcessConfig("ext/ProgressGroup.yml");

            // 从配置装载命令
            for (ProcessConfigBean.ProcessBean bean : processConfig.getProcessBeanList()){
                MiniApp miniApp = new ProcessAppTemplate() {
                    @Override
                    public String getCmdStr() {
                        return bean.getCmd();
                    }

                    @Override
                    public String getName() {
                        return bean.getName();
                    }

                    @Override
                    public String getRunPath() {
                        return bean.getPath();
                    }
                };

                list.add(miniApp);
            }

        } catch (Exception e) {
            // 异常则显示异常面板
            MiniApp miniApp = new MiniApp() {
                @Override
                public String getName() {
                    return "ProcessGroupApp";
                }

                @Override
                public JPanel getPanel() {
                    JPanel panel = new JPanel();
                    panel.setLayout(new BorderLayout());

                    JTextArea textArea = new JTextArea();
                    textArea.setText(e.getMessage());
                    textArea.setForeground(Color.RED);
                    System.out.println(e.getMessage());

                    JScrollPane scrollPane = new JScrollPane(textArea);
                    textArea.setRows(15);

                    panel.add(scrollPane);
                    return panel;
                }

                @Override
                public boolean protect() {
                    return false;
                }
            };

            list.add(miniApp);
        }

        return list;
    }

}
