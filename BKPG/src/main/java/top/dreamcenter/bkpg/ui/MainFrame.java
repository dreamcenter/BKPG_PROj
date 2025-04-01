package top.dreamcenter.bkpg.ui;

import top.dreamcenter.bkpg.entity.MiniAppJListWrap;
import top.dreamcenter.bkpg.protocal.MiniApp;
import top.dreamcenter.bkpg.util.MiniAppRegister;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class MainFrame extends JFrame {


    private final HashMap<String, MiniApp> container;

    public MainFrame() {
        
        container = MiniAppRegister.registerApps();

        // 基础设置
        setSize(600,400);
        setLocationRelativeTo(null);
        setLayout(null);
        setTitle("JM管理器");
        setResizable(false);
        URL resource = MainFrame.class.getClassLoader().getResource("img/icon.png");
        Image icon = Toolkit.getDefaultToolkit().createImage(resource);
        setIconImage(icon);

        initializeContent();

    }

    private void initializeContent() {

        // 面板
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBounds(156, 2,424,358);
        panel.setBackground(new Color(250,250,250));
        panel.add(new JLabel("欢迎使用JM管理器"), BorderLayout.NORTH);

        // 任务列表
        DefaultListModel<MiniAppJListWrap> model = new DefaultListModel<>();
        for (Map.Entry<String, MiniApp> next : container.entrySet()) {
            model.addElement(new MiniAppJListWrap(next.getValue()));
        }
        JList<MiniAppJListWrap> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()){
                panel.removeAll();

                MiniAppJListWrap entity = list.getSelectedValue();

                panel.add(entity.getMiniApp().getPanel());
                panel.updateUI();
            }
        });
        JScrollPane tasksPanelS = new JScrollPane(list);
        tasksPanelS.setBounds(2,2,150,358);


        add(tasksPanelS);
        add(panel);
    }

    public void reloadFrame() {

        // 1. 保护中的程序列表
        Map<String, MiniApp> protectedApps = container.entrySet().stream()
                .filter(item -> item.getValue().protect())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // 2. 新的程序列表
        HashMap<String, MiniApp> list = MiniAppRegister.registerApps();
        list.forEach((key, value) -> {
            if (!protectedApps.containsKey(key)) {
                protectedApps.put(key, value);
            }
        });

        // 放入容器
        container.clear();
        container.putAll(protectedApps);



        getContentPane().removeAll();
        initializeContent();

        revalidate();
        repaint();

    }
}
