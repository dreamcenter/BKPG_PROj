package top.dreamcenter.bkpg;

import top.dreamcenter.bkpg.ui.MainFrame;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

/**
 * 应用管理器
 *
 */
public class App 
{
    public static void main(String[] args){
        final MainFrame[] mainFrame = {new MainFrame()};
        mainFrame[0].setVisible(true);


        if (SystemTray.isSupported()){
            URL url = App.class.getClassLoader().getResource("img/icon.png");


            Image icon = Toolkit.getDefaultToolkit().createImage(url);
            SystemTray tray = SystemTray.getSystemTray();


            PopupMenu popupMenu = new PopupMenu();
            MenuItem reloadItem = new MenuItem("Reload");
            reloadItem.addActionListener(e -> {
                mainFrame[0].setVisible(false);
                mainFrame[0].reloadFrame();
                mainFrame[0].setVisible(true);
            });
            MenuItem exitItem = new MenuItem("Exit");
            exitItem.addActionListener(e -> System.exit(0));
            popupMenu.add(reloadItem);
            popupMenu.add(exitItem);

            TrayIcon trayIcon = new TrayIcon(icon, "任务管理器", popupMenu);
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == 1) {
                        mainFrame[0].setVisible(!mainFrame[0].isVisible());
                    }
                }
            });

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }
}
