package com.chinadci.mel.mleo.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;

import android.annotation.SuppressLint;
import android.util.Log;

import com.esri.android.map.TiledServiceLayer;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;

public class FJWMTSMapLayer extends TiledServiceLayer {
	File cacheFolder;
//	static String url = "http://service.fjmap.net/TianDiVector/wmts";
	static String url = "http://service.fjmap.net/vec_fj/wmts";
	static String url2 = "http://t1.tianditu.com/vec_c/wmts";

	public FJWMTSMapLayer() {
		super(url);
		initLayer();
	}

	public FJWMTSMapLayer(File cacheFolder) {
		super(url);
		this.cacheFolder = cacheFolder;
		if (this.cacheFolder.isDirectory()) {
			if (!cacheFolder.exists()) {
				cacheFolder.mkdirs();
			}
		}
		initLayer();

	}

	@Override
	protected void initLayer() {
		try {
			if (getID() == 0) {
				a();
			} else {
				b();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void a() {
		this.nativeHandle = create();
		changeStatus(com.esri.android.map.event.OnStatusChangedListener.STATUS
				.fromInt(-1000));
	}

	private void b() {
		SpatialReference localSpatialReference = SpatialReference
				.create(SpatialReference.WKID_WGS84);

		Envelope FullEnvelope = new Envelope(112.5D, 22.5D, 126.5625D, 30.9375D);
		Envelope initEnvelope = new Envelope(112.5D, 22.5D, 126.5625D, 30.9375D);

		setDefaultSpatialReference(localSpatialReference);
		setFullExtent(FullEnvelope);
		setInitialExtent(initEnvelope);

		Point localPoint = new Point(-180, 90);
		double[] arrayOfDoublescale = new double[] { 4622333.6789775901D,
				2311166.8394887899D, 1155583.4197444001D, 577791.70987219794D,
				288895.85493609897D, 144447.92746805001D, 72223.963734024801D,
				36111.9818670124D, 18055.990933506204D, 9027.9954667531019D,
				4513.997733376551D,2256.9988666882755D };
		double[] arrayOfDoubleres = new double[] { 0.010986328125D,
				0.0054931640625D, 0.00274658203125D, 0.001373291015625D,
				0.000686645507813D, 0.0003433227539063D, 0.00017166137695313D,
				8.58306884765625E-005D, 4.29153442382813E-005D,
				2.14576721191406E-005D, 1.07288360595703E-005D ,5.36441802978516E-06D};

		int cols = 256;
		int dpi = 96;
		int rows = 256;
		int k = 12;

		TiledServiceLayer.TileInfo localTileInfo = new TiledServiceLayer.TileInfo(
				localPoint, arrayOfDoublescale, arrayOfDoubleres, k, dpi, rows,
				cols);
		setTileInfo(localTileInfo);
		super.initLayer();

	}

	@SuppressLint("NewApi")
	@Override
	protected byte[] getTile(int level, int col, int row) throws Exception {
		byte[] bytes = null;
		String tileUrl = "";
		if (level > 17)
			return null;
		try {
//			String url = String
//					.format("%1$s?SERVICE=WMTS&REQUEST=GetTile&VERSION=%2$s&LAYER=%3$s&STYLE=%4$s&TILEMATRIXSET=%5$s&TILEMATRIX=%6$s&TILEROW=%7$s&TILECOL=%8$s&FORMAT=%9$s",
//							new Object[] { getUrl(), "1.0.0", "TianDiVector",
//									"TianDiVector", "Matrix_0",
//									String.valueOf(level + 7),
//									String.valueOf(row), String.valueOf(col),
//									"image/tile" });
			if (level + 7 > 7&&level + 7<18) {
				tileUrl = String
						.format("%1$s?SERVICE=WMTS&REQUEST=GetTile&VERSION=%2$s&LAYER=%3$s&STYLE=%4$s&TILEMATRIXSET=%5$s&TILEMATRIX=%6$s&tilerow=%7$s&tilecol=%8$s&FORMAT=%9$s",
								new Object[] { getUrl(), "1.0.0", "vec_fj",
										"vec_fj", "Matrix_0",
										String.valueOf(level + 7),
										String.valueOf(row), String.valueOf(col),
										"image%2Fpng" });
			}else{
				tileUrl = String
						.format("%1$s?service=WMTS&request=GetTile&version=%2$s&layer=%3$s&STYLE=%4$s&TILEMATRIXSET=%5$s&TILEMATRIX=%6$s&tilerow=%7$s&tilecol=%8$s&FORMAT=%9$s",
								new Object[] { url2, "1.0.0", "vec",
										"default", "c",
										String.valueOf(level + 7),
										String.valueOf(row), String.valueOf(col),
										"tiles" });
			}
			Log.i("uri", tileUrl);
			if (cacheFolder == null) {
				bytes = getServiceTileBytes(tileUrl);
				return bytes;
			} else {
				String levelString = String.format("L%02d", level + 7);
				String colString = String.format("%1$#010x", col).replace("0x",
						"C");
				String rowString = String.format("%1$#010x", row).replace("0x",
						"R");
				String cacheName = new StringBuffer(colString).append(".tile")
						.toString();
				String cacheDir = new StringBuffer(
						cacheFolder.getAbsolutePath()).append("/")
						.append(levelString).append("/").append(rowString)
						.toString();
				File cacheDirFile = new File(cacheDir);
				if (!cacheDirFile.exists())
					cacheDirFile.mkdirs();
				File cacheFile = new File(cacheDirFile, cacheName);
				if (cacheFile.exists() && cacheFile.getTotalSpace() > 0) {
					FileInputStream inputStream = new FileInputStream(cacheFile);
					int len = 0;
					byte buf[] = new byte[1024];
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					while ((len = inputStream.read(buf)) != -1) {
						out.write(buf, 0, len);
					}
					out.close();
					return out.toByteArray();
				} else {
					bytes = getServiceTileBytes(tileUrl);
					if (bytes!=null&&bytes.length > 0) {
						OutputStream outStream = new FileOutputStream(cacheFile);
						outStream.write(bytes);
						outStream.flush();
						outStream.close();
					}
					return bytes;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bytes;
	}

	private byte[] getServiceTileBytes(String url) {
		Map<String, String> map = null;
		try {
			return com.esri.core.internal.io.handler.a.a(url, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
