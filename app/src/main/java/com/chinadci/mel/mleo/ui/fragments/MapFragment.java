package com.chinadci.mel.mleo.ui.fragments;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import com.chinadci.mel.mleo.core.OutsideManifestHandler;
import com.chinadci.mel.mleo.ui.views.MleoMapView;
import com.esri.android.map.MapView;
import com.esri.android.map.TiledServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;

public class MapFragment extends ContentFragment {
	MleoMapView mapViewer;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mapViewer = new MleoMapView(getActivity());
		Log.i("ydzf","MapFragment onCreateView");
		if (vectorTiledLayer != null&&vectorTiledLayer_ != null)
			mapViewer.setVectorTiledLayer(vectorTiledLayer,vectorTiledLayer_);
		if (imageryTiledLayer != null&&imageryTiledLayer_ != null)
			mapViewer.setImageryLayer(imageryTiledLayer,imageryTiledLayer_);

		mapViewer.setMapStatusChangedListener(new OnStatusChangedListener() {
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("deprecation")
			public void onStatusChanged(Object source, STATUS status) {
				try {
					MapView mapView = (MapView) source;
					Log.i("ydzf","MapFragment_onCreateView");
					Location location = mapView.getLocationDisplayManager()
							.getLocation();
					if (location != null
							&& pointIsInside(new Point(location.getLongitude(),
									location.getLatitude()), mapView
									.getMaxExtent())) {
						mapView.zoomToScale(new Point(location.getLongitude(),
								location.getLatitude()), context.getResources()
								.getDisplayMetrics().density * 20000d);
						mapViewer.viewMapScale(context.getResources()
								.getDisplayMetrics().density * 20000d);
					} else {
						OutsideManifestHandler handler = OutsideManifestHandler
								.getHandler(context);
						if (handler != null
								&& handler.getUserMapCentre() != null) {
							Point centrePoint = handler.getUserMapCentre()
									.getPoint();
							double scale = handler.getUserMapCentre()
									.getScale();
							if (centrePoint != null && scale > 0) {
								mapView.zoomToScale(centrePoint, scale);
								mapViewer.viewMapScale(scale);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return mapViewer;
	}

	private boolean pointIsInside(Point point, Envelope extent) {
		return (point.getX() > extent.getXMin()
				&& point.getX() < extent.getXMax()
				&& point.getY() > extent.getYMin() && point.getY() < extent
				.getYMax());
	}
}
