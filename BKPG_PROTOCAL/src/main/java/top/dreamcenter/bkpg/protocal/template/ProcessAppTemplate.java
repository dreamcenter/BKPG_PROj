package top.dreamcenter.bkpg.protocal.template;

import top.dreamcenter.bkpg.protocal.MiniApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * 进程启动模板
 */
public abstract class ProcessAppTemplate implements MiniApp, ActionListener {

    private Process process;

    private JTextArea textArea;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 需要执行的命令
     * @return CMD命令
     */
    public abstract String getCmdStr();

    @Override
    public JPanel getPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout());
        centerPanel.setBackground(Color.WHITE);

        // 标题
        JLabel title = new JLabel(getName());

        // 按钮组
        JButton startBtn = new JButton("启动");
        JButton stopBtn = new JButton("停止");

        startBtn.setBackground(Color.WHITE);
        stopBtn.setBackground(Color.WHITE);

        startBtn.addActionListener(this);
        stopBtn.addActionListener(this);

        startBtn.setActionCommand("start");
        stopBtn.setActionCommand("stop");

        centerPanel.add(startBtn);
        centerPanel.add(stopBtn);

        // 输出
        if (textArea == null) {
            textArea = new JTextArea();
        }
        textArea.setBackground(Color.DARK_GRAY);
        textArea.setForeground(Color.WHITE);
        textArea.setRows(15);
        JScrollPane scrollPane = new JScrollPane(textArea);

        panel.add(title, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);

        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()){
            case "start": btnStartMethod(); break;
            case "stop": btnStopMethod(); break;
            default:
                System.out.println("未找到的命令:" + e.getActionCommand());
        }
    }

    /**
     * 按钮 - 启动
     */
    private void btnStartMethod() {
        new Thread(() -> {
            successWrite("尝试启动程序...");
            if (process != null) {
                failWrite("已启动过。");
                return;
            }
            try {
                process = Runtime.getRuntime().exec(getCmdStr());
                successWrite("启动成功!");
                heartbeatCheck();
                String tmp;

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
                BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), "GBK"));

                while ((tmp = reader.readLine()) != null) {
                    successWrite(tmp);
                }

                while ((tmp = errReader.readLine()) != null) {
                    failWrite(tmp);
                }

                reader.close();
                errReader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 按钮 - 停止
     */
    private void btnStopMethod() {
        if (process ==null) {
            failWrite("未启动。");
            return;
        }
        if (process.isAlive()) process.destroy();
        process = null;
        successWrite("已终止。");
    }


    /**
     * 写入正确信息
     * @param raw 原文
     */
    private void successWrite(String raw) {
        textArea.append(formatOutput(true, raw));
    }

    /**
     * 写入错误信息
     * @param raw 原文
     */
    private void failWrite(String raw) {
        textArea.append(formatOutput(false, raw));
    }

    /**
     * 输出格式化
     * @param code 是否成功结果
     * @param raw 原文
     * @return 输出结果
     */
    private String formatOutput(boolean code,String raw) {
        return (code ? "O" : "E") + " " + dateFormat.format(Calendar.getInstance().getTime()) + "  " + raw + "\n";
    }

    /**
     * 5 分钟心跳检测，如果心跳停止，则自动释放进程
     */
    private void heartbeatCheck() {
        new Thread(() -> {
            while (true) {
                if (process == null) break;
                else if (!process.isAlive()) {
                    failWrite("心跳停止");
                    process.destroy();
                    process = null;
                    successWrite("已释放资源");
                    break;
                }
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public boolean protect() {
        return process != null &&  process.isAlive();
    }
}
