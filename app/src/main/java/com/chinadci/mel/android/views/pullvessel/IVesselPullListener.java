package com.chinadci.mel.android.views.pullvessel;
/**
 * 
* @ClassName IVesselPullListener 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年6月12日 上午10:30:05 
*
 */
public interface IVesselPullListener {
	public static final int ACTION_PULL_DOWN = 0x000000;
	public static final int ACTION_PULL_UP = 0x000001;

	public Object doTask(int action);
}
