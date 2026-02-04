package top.dreamcenter.bkpg.group.bean;

import java.util.List;

public class ProcessConfigBean {

    private List<ProcessBean> processBeanList;

    public List<ProcessBean> getProcessBeanList() {
        return processBeanList;
    }

    public void setProcessBeanList(List<ProcessBean> processBeanList) {
        this.processBeanList = processBeanList;
    }

    public static class ProcessBean{

        /**
         * 名称
         */
        private String name;
        /**
         * 指令
         */
        private String cmd;

        /**
         * 路径
         */
        private String path;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCmd() {
            return cmd;
        }

        public void setCmd(String cmd) {
            this.cmd = cmd;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

}
