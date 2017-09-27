package com.chinadci.mel.mleo.ui.fragments.data.task;

/**
 * @author  guanghuil@geo-k.cn on 2015/10/12.
 */
public class Constants {
    // 操作结果类型
    public class RESULT_CODE {
        public static final int ERROR = 1; // 错误
        public static final int NO_DATA = 2;// 无数据
        public static final int REFRESH = 3;// 刷新
        public static final int LOAD = 4; // 加载
        public static final int FIRST = 5;// 第一次加载
    }
    // 任务类型
    public class TASK_TYPE {
        public static final String FIRST = "first";
        public static final String NOR_FIRST = "not_first";
        public static final String REFRESH = "REFRESH";
        public static final String LOAD = "LOAD";
    }
    //activity交互intent 传值所需key
    public class INTENT_KEY {
        public static final String WEB_URL = "web_url";
        public static final String IMG_URL = "img_url";
        public static final String IMG_NAME = "img_name";
        public static final String IC_NAME = "ic_name";
        public static final String IC_TEXT = "ic_text";
        public static final String SELECT_THEME = "select_theme";
    }
    //一些提醒常量
    public class TOASTS{
        public static final String CONTENT_NO_NET = "世界上最遥远的距离就是没网。";
        public static final String BTN_CHECK_SETTING = "检查设置";
        public static final String TEXT_NOW = "刚刚";
        public static final String TEXT_NO_DATA = "没有更多数据了";
    }
    //一些服务器错误信息提醒
    public class ERRORS{
        public static final String NET_CONNECTIONS_ERROR = "网络连接失败或服务器返回错误";
        public static final String LOAD_ERROR = "加载失败";
        public static final String REQUEST_ERROR = "请求出错";
        public static final String NETWORK_CONNECTION_TIMEOUT = "连接服务器超时，请检查网络！";

        public static final String OPEN_IMG_ERROR = "打开图片发生错误";
        public static final String CHART_TYPE_ERROR = "未获得统计图类型";
        public static final String REQUEST_XZQH_ERROR = "获取行政区划发生错误";
        public static final String REQUEST_THEMES_ERROR = "获取图层列表发生错误";
    }

}
