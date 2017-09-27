package com.chinadci.mel.android.views.pullvessel;

//import com.chinadci.mel.mleo.fj.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @ClassName ListViewRefreshVessel
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:30:08
 * 
 */
public class ListViewRefreshVessel extends RelativeLayout implements
		OnTouchListener {
	static final int STATUS_PULL_TO_REFRESH = 0x000000;
	static final int STATUS_RELEASE_TO_REFRESH = 0x000001;
	static final int STATUS_REFRESHING = 0x000002;
	static final int STATUS_REFRESH_FINISHED = 0x000003;
	static final int SCROLL_SPEED = -1;

	int i_head_vessel_refresh = -1;
	int i_foot_vessel_refresh = -1;

	int i_head_vessel_refresh_arrow = -1;
	int i_head_vessel_refresh_text = -1;
	int i_head_vessel_refresh_progress = -1;

	int i_foot_vessel_refresh_arrow = -1;
	int i_foot_vessel_refresh_text = -1;
	int i_foot_vessel_refresh_progress = -1;
	int i_pull_height = -1;

	int pullHeight;
	int touchSlop;
	int currentStatus = STATUS_REFRESH_FINISHED;// 当前状态
	int lastStatus = currentStatus;// 记录上一次的状态是什么，避免进行重复操作
	float yDown;

	boolean pullDownEnable;
	boolean pullUpEnable;
	boolean layoutFirst = true;

	boolean downEnable = true;
	boolean upEnable = true;

	LayoutParams headParams;// 下拉头的布局参数
	LayoutParams footParams;// 下拉头的布局参数
	LayoutParams listParams;// 列表的布局参数
	ListView listView;

	View headView;
	View footView;

	TextView headTextView;
	ProgressBar headProgressBar;
	ImageView headArrowView;

	TextView footTextView;
	ProgressBar footProgressBar;
	ImageView footArrowView;
	IVesselPullListener pullListener;

	public void finishRefresh() {
		currentStatus = STATUS_REFRESH_FINISHED;
		upadateStatusDisplay();
		new HideTask().execute();
		listView.setClickable(true);
		listView.setPressed(true);
		listView.setFocusable(true);
		listView.setFocusableInTouchMode(true);
	}

	/**
	 * 
	 * @Title: addVesselPullListener
	 * @Description: TODO
	 * @param listener
	 * @throws
	 */
	public void addVesselPullListener(IVesselPullListener listener) {
		this.pullListener = listener;
	}

	/**
	 * 
	 * @Title: setHeadBackgroundDrawable
	 * @Description: TODO
	 * @param drawable
	 * @throws
	 */
	public void setHeadBackgroundDrawable(Drawable drawable) {
		headView.setBackgroundDrawable(drawable);
	}

	/**
	 * 
	 * @Title: setFootBackgroundDrawable
	 * @Description: TODO
	 * @param drawable
	 * @throws
	 */
	public void setFootBackgroundDrawable(Drawable drawable) {
		footView.setBackgroundDrawable(drawable);
	}

	/**
	 * 
	 * @Title: setHeadTextStyle
	 * @Description: TODO
	 * @param color
	 * @param size
	 * @throws
	 */
	public void setHeadTextStyle(int color, float size) {
		if (color > -1)
			headTextView.setTextColor(color);
		if (size > -1)
			headTextView.setTextSize(size);
	}

	/**
	 * 
	 * @Title: setFootTextStyle
	 * @Description: TODO
	 * @param color
	 * @param size
	 * @throws
	 */
	public void setFootTextStyle(int color, float size) {
		if (color > -1)
			footTextView.setTextColor(color);
		if (size > -1)
			footTextView.setTextSize(size);
	}

	public void setPullupEnable(boolean enable) {
		upEnable = enable;
	}

	public void setPulldownEnable(boolean enable) {
		downEnable = enable;
	}

	public ListViewRefreshVessel(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public ListViewRefreshVessel(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public ListViewRefreshVessel(Context context) {
		super(context);
		initView(context);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
		if (changed) {
			if (layoutFirst) {
				listView = (ListView) getChildAt(0);
				listParams = (LayoutParams) listView.getLayoutParams();
				listParams.height = getMeasuredHeight();
				listView.requestLayout();
				listView.setOnTouchListener(this);
				addView(headView, headParams);
				addView(footView, footParams);
				headView.requestLayout();
				footView.requestLayout();
				layoutFirst = false;
			} else {
				listParams = (LayoutParams) listView.getLayoutParams();
				listParams.height = getMeasuredHeight();
				listView.setLayoutParams(listParams);
				listView.requestLayout();

				headParams.topMargin = -pullHeight;
				headView.setLayoutParams(headParams);
				headView.requestLayout();

				footParams.bottomMargin = -pullHeight;
				footView.setLayoutParams(footParams);
				footView.requestLayout();
			}
		}
	}

	public boolean onTouch(View v, MotionEvent event) {
		int distance;
		float yMove;
		setPullEnable(event);// 判定并设置拉动操作是否可用
		if (currentStatus != STATUS_REFRESHING) {
			if (pullDownEnable || pullUpEnable) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					yDown = event.getRawY();
					break;

				case MotionEvent.ACTION_MOVE:
					yMove = event.getRawY();
					distance = (int) (yMove - yDown);
					if (Math.abs(distance) < touchSlop)
						return false;

					if (distance < 0)// 向上拉
					{
						if (!pullUpEnable)
							return false;
						if (currentStatus != STATUS_REFRESHING) {
							if (footParams.bottomMargin > 0) {
								currentStatus = STATUS_RELEASE_TO_REFRESH;
							} else {
								currentStatus = STATUS_PULL_TO_REFRESH;
							}
						}
					} else {// 向下拉
						if (!pullDownEnable)
							return false;
						if (currentStatus != STATUS_REFRESHING) {
							if (headParams.topMargin > 0) {
								currentStatus = STATUS_RELEASE_TO_REFRESH;
							} else {
								currentStatus = STATUS_PULL_TO_REFRESH;
							}
						}
					}

					headParams.topMargin = -pullHeight + distance / 2;
					headView.setLayoutParams(headParams);
					headView.requestLayout();

					footParams.bottomMargin = -pullHeight - distance / 2;
					footView.setLayoutParams(footParams);
					footView.requestLayout();

					listParams.topMargin = distance / 2;
					listView.setLayoutParams(listParams);
					listView.requestLayout();
					break;

				case MotionEvent.ACTION_UP:
				default:
					if (currentStatus == STATUS_PULL_TO_REFRESH) {
						new HideTask().execute();
					} else if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
						currentStatus = STATUS_REFRESHING;
						new RefreshingTask().execute();
					}
					break;
				}
				upadateStatusDisplay();
				lastStatus = currentStatus;

				if (currentStatus == STATUS_PULL_TO_REFRESH
						|| currentStatus == STATUS_RELEASE_TO_REFRESH
						|| currentStatus == STATUS_REFRESHING) {
					listView.setClickable(false);
					listView.setPressed(false);
					listView.setFocusable(false);
					listView.setFocusableInTouchMode(false);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @Title: initView
	 * @Description: TODO
	 * @param context
	 * @throws
	 */
	void initView(Context context) {
		i_head_vessel_refresh = context.getResources().getIdentifier(
				context.getPackageName() + ":layout/" + "head_vessel_refresh",
				null, null);
		i_foot_vessel_refresh = context.getResources().getIdentifier(
				context.getPackageName() + ":layout/" + "foot_vessel_refresh",
				null, null);

		i_head_vessel_refresh_arrow = context.getResources()
				.getIdentifier(
						context.getPackageName() + ":id/"
								+ "head_vessel_refresh_arrow", null, null);

		i_head_vessel_refresh_text = context.getResources().getIdentifier(
				context.getPackageName() + ":id/" + "head_vessel_refresh_text",
				null, null);
		i_head_vessel_refresh_progress = context.getResources().getIdentifier(
				context.getPackageName() + ":id/"
						+ "head_vessel_refresh_progress", null, null);

		i_foot_vessel_refresh_arrow = context.getResources()
				.getIdentifier(
						context.getPackageName() + ":id/"
								+ "foot_vessel_refresh_arrow", null, null);
		i_foot_vessel_refresh_text = context.getResources().getIdentifier(
				context.getPackageName() + ":id/" + "foot_vessel_refresh_text",
				null, null);
		i_foot_vessel_refresh_progress = context.getResources().getIdentifier(
				context.getPackageName() + ":id/"
						+ "foot_vessel_refresh_progress", null, null);
		i_pull_height = context.getResources().getIdentifier(
				context.getPackageName() + ":dimen/" + "pull_height", null,
				null);
		pullHeight = getResources().getDimensionPixelSize(i_pull_height);
		touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

		headParams = new LayoutParams(LayoutParams.MATCH_PARENT, pullHeight);
		headParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		headParams.topMargin = -pullHeight;

		footParams = new LayoutParams(LayoutParams.MATCH_PARENT, pullHeight);
		footParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		footParams.bottomMargin = -pullHeight;

		headView = LayoutInflater.from(context).inflate(i_head_vessel_refresh,
				null);
		footView = LayoutInflater.from(context).inflate(i_foot_vessel_refresh,
				null);

		headArrowView = (ImageView) headView
				.findViewById(i_head_vessel_refresh_arrow);
		headTextView = (TextView) headView
				.findViewById(i_head_vessel_refresh_text);
		headProgressBar = (ProgressBar) headView
				.findViewById(i_head_vessel_refresh_progress);

		footArrowView = (ImageView) footView
				.findViewById(i_foot_vessel_refresh_arrow);
		footTextView = (TextView) footView
				.findViewById(i_foot_vessel_refresh_text);
		footProgressBar = (ProgressBar) footView
				.findViewById(i_foot_vessel_refresh_progress);
	}

	/**
	 * 
	 * @Title: setPullEnable
	 * @Description: TODO
	 * @param event
	 * @throws
	 */
	void setPullEnable(MotionEvent event) {

		int childCount = listView.getChildCount();
		if (childCount > 0) {// listView中有子元素
			int size = listView.getCount();
			int listHeight = listView.getMeasuredHeight();
			int lastVisiblePostion = listView.getLastVisiblePosition();
			int firstVisiblePositon = listView.getFirstVisiblePosition();
			View firstChildView = listView.getChildAt(0);
			View lastChildView = listView.getChildAt(childCount - 1);

			if (firstVisiblePositon == 0
					&& firstChildView.getTop() == listView.getPaddingTop()
					&& downEnable) {// listView已滚动到顶部
				if (!pullDownEnable)
					yDown = event.getRawY();
				pullDownEnable = true;
			} else {
				pullDownEnable = false;
			}

			if (lastVisiblePostion == size - 1
					&& lastChildView.getBottom() <= listHeight && upEnable) {// listView已滚动到底部
				if (!pullUpEnable)
					yDown = event.getRawY();
				pullUpEnable = true;
			} else {
				pullUpEnable = false;
			}
		} else {// listView中没有子元素时可上下拉动

			pullDownEnable = downEnable;
			pullUpEnable = upEnable;
		}
	}

	/**
	 * 
	 * @Title: upadateStatusDisplay
	 * @Description: TODO
	 * @throws
	 */
	void upadateStatusDisplay() {
		if (currentStatus != lastStatus) {
			if (currentStatus == STATUS_PULL_TO_REFRESH) {
				if (pullDownEnable) {
					int i_pull_to_refresh = getContext().getResources()
							.getIdentifier(
									getContext().getPackageName() + ":string/"
											+ "pull_to_refresh", null, null);
					headTextView.setText(i_pull_to_refresh);
					headProgressBar.setVisibility(View.GONE);
					headArrowView.setVisibility(View.VISIBLE);
					rotateArrow(headArrowView);
				}

				if (pullUpEnable) {
					int i_pull_to_load = getContext().getResources()
							.getIdentifier(
									getContext().getPackageName() + ":string/"
											+ "pull_to_load", null, null);
					footTextView.setText(i_pull_to_load);
					footProgressBar.setVisibility(View.GONE);
					footArrowView.setVisibility(View.VISIBLE);
					rotateArrow(footArrowView);
				}
			} else if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
				if (pullDownEnable) {
					int i_release_to_refresh = getContext().getResources()
							.getIdentifier(
									getContext().getPackageName() + ":string/"
											+ "release_to_refresh", null, null);
					headTextView.setText(i_release_to_refresh);
					headProgressBar.setVisibility(View.GONE);
					headArrowView.setVisibility(View.VISIBLE);
					rotateArrow(headArrowView);
				}

				if (pullUpEnable) {
					int i_release_to_load = getContext().getResources()
							.getIdentifier(
									getContext().getPackageName() + ":string/"
											+ "release_to_load", null, null);
					footTextView.setText(i_release_to_load);
					footProgressBar.setVisibility(View.GONE);
					footArrowView.setVisibility(View.VISIBLE);
					rotateArrow(footArrowView);
				}
			} else if (currentStatus == STATUS_REFRESHING) {
				if (pullDownEnable) {
					int i_refreshing = getContext().getResources()
							.getIdentifier(
									getContext().getPackageName() + ":string/"
											+ "refreshing", null, null);
					headTextView.setText(i_refreshing);
					headArrowView.clearAnimation();
					headArrowView.setVisibility(View.GONE);
					headProgressBar.setVisibility(View.VISIBLE);
				}

				if (pullUpEnable) {
					int i_loading = getContext().getResources().getIdentifier(
							getContext().getPackageName() + ":string/"
									+ "loading", null, null);
					footTextView.setText(i_loading);
					footArrowView.clearAnimation();
					footArrowView.setVisibility(View.GONE);
					footProgressBar.setVisibility(View.VISIBLE);
				}
			}
			headView.requestLayout();
			footView.requestLayout();
		}
	}

	/**
	 * 
	 * @Title: rotateArrow
	 * @Description: 根据当前的状态来旋转箭头。
	 * @param view
	 */
	void rotateArrow(View view) {
		float pivotX = view.getWidth() / 2f;
		float pivotY = view.getHeight() / 2f;
		float fromDegrees = 0f;
		float toDegrees = 0f;
		if (currentStatus == STATUS_PULL_TO_REFRESH) {
			fromDegrees = 180f;
			toDegrees = 360f;
		} else if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
			fromDegrees = 0f;
			toDegrees = 180f;
		}
		RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees,
				pivotX, pivotY);
		animation.setDuration(100);
		animation.setFillAfter(true);
		view.startAnimation(animation);
	}

	/**
	 * 
	 * @ClassName: HideTask
	 * @Description: TODO
	 * @author leix@geo-k.cn
	 * @date 2014年5月6日
	 * 
	 */
	class HideTask extends AsyncTask<Void, Integer, Integer> {
		boolean isPullDown = false;

		@Override
		protected Integer doInBackground(Void... params) {
			// TODO Auto-generated method stub
			int margin = 0;
			int headTop = headParams.topMargin;
			int footBottom = footParams.bottomMargin;
			int listTop = listParams.topMargin;

			if (listTop > 0) {
				margin = headTop;
				isPullDown = true;
			} else if (listTop < 0) {
				margin = footBottom;
				isPullDown = false;
			} else {
				return null;
			}

			while (margin > -pullHeight) {
				try {
					margin = margin + SCROLL_SPEED;
					publishProgress(margin);
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return margin;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			int margin = values[0];
			if (isPullDown) {
				headParams.topMargin = margin;
				headView.setLayoutParams(headParams);
				headView.requestLayout();

				listParams.topMargin = margin + pullHeight;
				listView.setLayoutParams(listParams);
				listView.requestLayout();

				footParams.bottomMargin = -pullHeight;
				footView.setLayoutParams(footParams);
				footView.requestLayout();

			} else {
				footParams.bottomMargin = margin;
				footView.setLayoutParams(footParams);
				footView.requestLayout();

				listParams.topMargin = -margin - pullHeight;
				listView.setLayoutParams(listParams);
				listView.requestLayout();

				headParams.topMargin = -pullHeight;
				headView.setLayoutParams(headParams);
				headView.requestLayout();
			}
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			footParams.bottomMargin = -pullHeight;
			footView.setLayoutParams(footParams);
			footView.requestLayout();

			listParams.topMargin = 0;
			listView.setLayoutParams(listParams);
			listView.requestLayout();

			headParams.topMargin = -pullHeight;
			headView.setLayoutParams(headParams);
			headView.requestLayout();

		}
	}

	/**
	 * 
	 * @ClassName: RefreshingTask
	 * @Description: TODO
	 * @author leix@geo-k.cn
	 * @date 2014年5月6日
	 * 
	 */
	class RefreshingTask extends AsyncTask<Void, Integer, Integer> {
		boolean isPullDown = false;

		@Override
		protected Integer doInBackground(Void... params) {
			// TODO Auto-generated method stub
			int margin = 0;
			int headTop = headParams.topMargin;
			int footBottom = footParams.bottomMargin;
			if (headTop > 0) {
				margin = headTop;
				isPullDown = true;
			} else if (footBottom > 0) {
				margin = footBottom;
				isPullDown = false;
			}
			while (margin > 0) {
				try {
					margin = margin + SCROLL_SPEED;
					publishProgress(margin);
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			publishProgress(0);
			return 0;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			upadateStatusDisplay();
			int margin = values[0];
			if (isPullDown) {
				headParams.topMargin = margin;
				headView.setLayoutParams(headParams);
				headView.requestLayout();

				listParams.topMargin = margin + pullHeight;
				listView.setLayoutParams(listParams);
				listView.requestLayout();

				footParams.bottomMargin = -pullHeight;
				footView.setLayoutParams(footParams);
				footView.requestLayout();

			} else {
				footParams.bottomMargin = margin;
				footView.setLayoutParams(footParams);
				footView.requestLayout();

				listParams.topMargin = -margin - pullHeight;
				listView.setLayoutParams(listParams);
				listView.requestLayout();

				headParams.topMargin = -pullHeight;
				headView.setLayoutParams(headParams);
				headView.requestLayout();
			}
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);

			if (pullListener != null) {
				if (isPullDown)
					pullListener.doTask(IVesselPullListener.ACTION_PULL_DOWN);
				else
					pullListener.doTask(IVesselPullListener.ACTION_PULL_UP);
				return;
			}
			finishRefresh();
		}
	}
}
