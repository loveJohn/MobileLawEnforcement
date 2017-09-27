package com.chinadci.mel.mleo.ui.activities;

import android.os.Bundle;

import com.chinadci.android.map.dci.WGS84TiandituImageryLayer;
import com.chinadci.android.map.dci.WGS84TiandituMapLayer;

public class KKPolyGatherActivity extends PolyGatherActivity {
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setVectorTiledLayer(new WGS84TiandituMapLayer(),null);
		setImageryLayer(new WGS84TiandituImageryLayer(),null);
	}
}
