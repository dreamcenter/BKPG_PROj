package top.dreamcenter.bkpg.entity;

import top.dreamcenter.bkpg.protocal.MiniApp;

/**
 * 应用包装
 */
public class MiniAppJListWrap {
    private MiniApp miniApp;

    public MiniAppJListWrap(MiniApp miniApp) {
        this.miniApp = miniApp;
    }

    public MiniApp getMiniApp() {
        return miniApp;
    }

    @Override
    public String toString() {
        return miniApp.getName();
    }
}
