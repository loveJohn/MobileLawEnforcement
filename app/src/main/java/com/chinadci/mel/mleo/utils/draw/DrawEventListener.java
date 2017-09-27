package com.chinadci.mel.mleo.utils.draw;

import java.util.EventListener;

/**
 *	定义画图事件监听接口
 */
public interface DrawEventListener extends EventListener {
	void handleDrawEvent(DrawEvent event);
}
