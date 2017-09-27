package com.chinadci.mel.mleo.ui.popups;

import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chinadci.mel.mleo.core.FJWMTSImageryLayer;
import com.chinadci.mel.mleo.core.FJWMTSImageryLayer_;
import com.chinadci.mel.mleo.core.FJWMTSMapLayer;
import com.chinadci.mel.mleo.core.FJWMTSMapLayer_;
import com.chinadci.mel.mleo.core.LayerDepictInfo;
import com.esri.android.map.MapView;
import com.esri.android.map.TiledServiceLayer;

/**
 * 
 * @ClassName BaseLayerChooser
 * @Description TODO
 * @author leix@geo-k.cn
 * @date 2014年6月12日 上午10:37:21
 * 
 */
@SuppressLint("ViewConstructor") 
public class BaseLayerChooser extends PopupWindow {

	ArrayList<LayerDepictInfo> layerEleInfos;
	Context context;
	MapView map;
	LinearLayout layoutView;

	Drawable bgDrawable;
	int defWidth, defHeight, defSpacing, maxIconH, allIconW;

	File vCacheFile;
	File vCacheFile_;
	File rCacheFile;
	File rCacheFile_;
	
	String externalStorageDir;
	int i_spacing;
	int i_dir_root;

	/**
	 * 基础地图
	 */
	protected TiledServiceLayer vectorTiledLayer;
	/**
	 * 基础地图
	 */
	protected TiledServiceLayer vectorTiledLayer_;

	/**
	 * 影像地图
	 */
	protected TiledServiceLayer imageryTiledLayer;
	/**
	 * 影像地图
	 */
	protected TiledServiceLayer imageryTiledLayer_;

	public void setVectorTiledLayer(TiledServiceLayer layer,TiledServiceLayer layer_) {
		this.vectorTiledLayer = layer;
		this.vectorTiledLayer_ = layer_;
		if (map != null) {
			if (map.getLayers().length > 0
					&& map.getLayer(0) instanceof TiledServiceLayer
					&& map.getLayer(1) instanceof TiledServiceLayer){
				map.removeLayer(0);
				map.removeLayer(1);
			}
			map.addLayer(vectorTiledLayer, 0);
			map.addLayer(vectorTiledLayer_, 1);
		}
	}
	public void setImageryLayer(TiledServiceLayer layer,TiledServiceLayer layer_) {
		this.imageryTiledLayer = layer;
		this.imageryTiledLayer_ = layer_;
	}

	@SuppressWarnings("deprecation")
	public void setBackgroundDrawable(Drawable drawable) {
		super.setBackgroundDrawable(new BitmapDrawable());
		bgDrawable = drawable;
		layoutView.setBackgroundDrawable(bgDrawable);
	}

	public void showAsDropDown(View v) {
		int w = v.getMeasuredWidth();
		int h = v.getMeasuredHeight();
		super.showAsDropDown(v, -defWidth, -h);
	}

	void initRes(Context context) {
		i_spacing = context.getResources().getIdentifier(
				context.getPackageName() + ":dimen/" + "spacing", null, null);
		i_dir_root = context.getResources().getIdentifier(
				context.getPackageName() + ":string/" + "dir_root", null, null);
	}

	public BaseLayerChooser(Context c, int w, int h, MapView m,
			ArrayList<LayerDepictInfo> infos) {
		super(w, h);
		if (m == null)
			return;

		map = m;
		context = c;
		layerEleInfos = infos;
		initRes(c);
		defSpacing = context.getResources().getDimensionPixelSize(i_spacing);
		layoutView = new LinearLayout(context);
		layoutView.setOrientation(LinearLayout.HORIZONTAL);
		layoutView.setGravity(Gravity.CENTER);
		layoutView.setPadding(2 * defSpacing, 2 * defSpacing, 2 * defSpacing,
				2 * defSpacing);
		externalStorageDir = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		String s1 = new StringBuffer(externalStorageDir).append("/")
				.append(context.getString(i_dir_root)).append("/cache/V")
				.toString();
		String s1_ = new StringBuffer(externalStorageDir).append("/")
				.append(context.getString(i_dir_root)).append("/cache/V_")
				.toString();
		String s2 = new StringBuffer(externalStorageDir).append("/")
				.append(context.getString(i_dir_root)).append("/cache/R")
				.toString();
		String s2_ = new StringBuffer(externalStorageDir).append("/")
				.append(context.getString(i_dir_root)).append("/cache/R_")
				.toString();

		vCacheFile = new File(s1);
		vCacheFile_ = new File(s1_);
		rCacheFile = new File(s2);
		rCacheFile_ = new File(s2_);

		for (int i = 0; i < layerEleInfos.size(); i++) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			LayerDepictInfo layerEle = layerEleInfos.get(i);

			TextView layButton = new TextView(c);
			layButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);

			layButton.setTextColor(Color.BLACK);
			layButton.setText(layerEle.getName());
			layButton.setCompoundDrawablesWithIntrinsicBounds(null,
					layerEle.getIcon(), null, null);
			layButton.setTag(i);
			layButton.setGravity(Gravity.CENTER);
			layButton.setOnClickListener(layerChooserClickListener);

			if (i == 0) {
				maxIconH = layerEle.getIcon().getIntrinsicHeight();
				allIconW = layerEle.getIcon().getIntrinsicWidth();
			} else {
				params.setMargins(defSpacing, 0, 0, 0);
				if (maxIconH < layerEle.getIcon().getIntrinsicHeight())
					maxIconH = layerEle.getIcon().getIntrinsicHeight();

				allIconW += layerEle.getIcon().getIntrinsicWidth();
			}
			layoutView.addView(layButton, params);
		}

		defHeight = maxIconH + 4 * defSpacing;
		defWidth = allIconW + (layerEleInfos.size()) * 2 * defSpacing;
		setWidth(defWidth);
		setHeight(defHeight);
		TiledServiceLayer tiledLayer = vectorTiledLayer;
		TiledServiceLayer tiledLayer_ = vectorTiledLayer_;
		if (tiledLayer == null)
			tiledLayer = new FJWMTSMapLayer(vCacheFile);// WGS84TiandituMapLayer();
			tiledLayer_ = new FJWMTSMapLayer_(vCacheFile_);
		map.removeLayer(0);
		map.addLayer(tiledLayer, 0);
		map.removeLayer(1);
		map.addLayer(tiledLayer_, 1);
		layoutView.setPadding(0, 0, 0, defSpacing);
		setContentView(layoutView);
	}

	OnClickListener layerChooserClickListener = new OnClickListener() {
		public void onClick(View v) {
			int index = Integer.valueOf(v.getTag().toString());
			LayerDepictInfo layerEle = layerEleInfos.get(index);
			TiledServiceLayer tiledLayer = null;
			TiledServiceLayer tiledLayer_ = null;
			if (index == 0) {
				tiledLayer = vectorTiledLayer != null ? vectorTiledLayer
						: new FJWMTSMapLayer(vCacheFile);// WGS84TiandituMapLayer()
				tiledLayer_ = vectorTiledLayer_ != null ? vectorTiledLayer_
						: new FJWMTSMapLayer_(vCacheFile_);// WGS84TiandituMapLayer()

			} else {
				tiledLayer = imageryTiledLayer != null ? imageryTiledLayer
						: new FJWMTSImageryLayer(rCacheFile);// WGS84TiandituImageryLayer()
				tiledLayer_ = imageryTiledLayer_ != null ? imageryTiledLayer_
						: new FJWMTSImageryLayer_(rCacheFile_);// WGS84TiandituMapLayer()
			}
			map.removeLayer(0);
			map.addLayer(tiledLayer, 0);
			map.removeLayer(1);
			map.addLayer(tiledLayer_, 1);
			BaseLayerChooser.this.dismiss();
		}
	};
}
