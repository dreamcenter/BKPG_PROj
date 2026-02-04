package top.dreamcenter.bkpg.protocal.template;

import top.dreamcenter.bkpg.protocal.MiniApp;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * 进程启动模板
 */
public abstract class ProcessAppTemplate implements MiniApp, ActionListener {

    private Process process;

    private JTextArea textArea;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private int currentLines = 0;

    /**
     * 控制台显示最大行数
     * @return 行数
     */
    public int maxLinesOnConsole () {
        return 100;
    }

    /**
     * 需要执行的命令
     * @return Cmd 命令
     */
    public abstract String getCmdStr();

    /**
     * 路径位置
     * <br/>
     * 默认 null
     * @return 执行命令所在路径
     */
    public String getRunPath() {
        return null;
    }

    /**
     * 是否允许写入到某处, 默认写入文件
     * @return true：可以写入本地文件，false ：不允许写入
     */
    public boolean enableLogging2There() {
        return true;
    }

    /**
     * 写入内容到某处，此处留空，可选实现，默认到文件中
     * @param row 待写入的行
     */
    public void write2There (String row) {
        if (enableLogging2There()) {
            File logDir = new File("logs");
            if (!logDir.exists()) {
                boolean mkdirRes = logDir.mkdirs();
                if (!mkdirRes){
                    textArea.append(formatOutput(false, "日志文件夹创建失败") + "\n");
                }
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter(Paths.get("logs", getName() + ".log").toFile(), true), true)) {
                writer.println(row);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

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
                File file = null;

                if (getRunPath() != null && !getRunPath().equals("")) {
                    file = new File(getRunPath());
                    if (!file.exists()) {
                        failWrite("未找到启动路径:" + getRunPath());
                        return;
                    }
                }

                process = Runtime.getRuntime().exec(getCmdStr(), null, file);
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
                failWrite("启动失败。" + e.getMessage());
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
        String row = formatOutput(true, raw);
        write0(row);
    }

    /**
     * 写入错误信息
     * @param raw 原文
     */
    private void failWrite(String raw) {
        String row = formatOutput(false, raw);
        write0(row);
    }

    private void write0(String row) {
        textArea.append(row + "\n");
        currentLines++;

        // 检查行行数是否达到限制，达到则将textArea删除多余的行
        int exceedLines = currentLines - maxLinesOnConsole();
        if (exceedLines > 0) {
            try {
                textArea.replaceRange("", 0, textArea.getLineEndOffset(exceedLines + (maxLinesOnConsole()>>1)));
                currentLines = textArea.getLineCount();
            } catch (BadLocationException ignored) {
            }
        }

        write2There(row);
    }

    /**
     * 输出格式化
     * @param code 是否成功结果
     * @param raw 原文
     * @return 输出结果
     */
    private String formatOutput(boolean code,String raw) {
        return (code ? "O" : "E") + " " + dateFormat.format(Calendar.getInstance().getTime()) + "  " + raw;
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
