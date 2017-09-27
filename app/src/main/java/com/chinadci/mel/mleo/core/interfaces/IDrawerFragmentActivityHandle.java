package com.chinadci.mel.mleo.core.interfaces;

import android.os.Bundle;

import com.chinadci.mel.mleo.ui.fragments.ContentFragment;
import com.chinadci.mel.mleo.ui.fragments.ToolFragment;
/**
 * 
* @ClassName IDrawerFragmentActivityHandle 
* @Description TODO
* @author leix@geo-k.cn
* @date 2014年6月12日 上午10:30:45 
*
 */
public interface IDrawerFragmentActivityHandle {
	public void replaceContentFragment(ContentFragment fragment, Bundle bundle, int inAnimRes,
			int outAnimRes);

	public void replaceToolFragment(ToolFragment fragment, Bundle bundle, int inAnimRes,
			int outAnimRes);

	public void replaceTitle(String t);

	public void setModule(int i);
	
	public void setToolButtonType(int i);

	public void contentFragmentHandle(Object o);

	public void toolFragmentHandle(Object o);

	public void activityHandle(Object o);
}
