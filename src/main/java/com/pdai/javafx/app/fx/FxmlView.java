package com.pdai.javafx.app.fx;

import java.util.ResourceBundle;

public enum FxmlView {
    MAIN {
        @Override
		public String title() {
            return "离心机管理系统";
                    //getStringFromResourceBundle("app.title");
        }

        @Override
		public String fxml() {
            return "/template/main/main.fxml";
        }

    },
    MAIN_VIEW {
        @Override
        public String title() {
            return "主界面";
        }

        @Override
        public String fxml() {
            return "/template/module/serialPort.fxml";
        }
    },

    MODULE_DASHBOARD {
        @Override
		public String title() {
            return "数据查询";
                    //getStringFromResourceBundle("module.dashboard.title");
        }

        @Override
		public String fxml() {
            return "/template/module/dashboard.fxml";
        }

    },
    MODULE_PROFILE {
        @Override
		public String title() {
            return "关于我们";
                    //getStringFromResourceBundle("module.profile.title");
        }

        @Override
		public String fxml() {
            return "/template/module/profile.fxml";
        }

    },
    MODULE_WEBVIEW {
        @Override
		public String title() {
            return "内质网页";
                    //getStringFromResourceBundle("module.webview.title");
        }

        @Override
		public String fxml() {
            return "/template/module/webview.fxml";
        }

    };
	
    
    public abstract String title();
    public abstract String fxml();
    
    String getStringFromResourceBundle(String key){
        return ResourceBundle.getBundle("Bundle").getString(key);
    }

}
