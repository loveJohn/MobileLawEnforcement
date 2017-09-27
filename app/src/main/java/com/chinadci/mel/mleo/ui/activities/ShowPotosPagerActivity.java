package com.chinadci.mel.mleo.ui.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import com.chinadci.mel.R;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ShowPotosPagerActivity extends Activity {
	private ViewPager pager;
	private String[] imageUrls;
	private ArrayList<String> imageUrlsList;
	private DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.mipmap.ic_img_nomal)
				.showImageOnFail(R.mipmap.ic_img_nomal)
				.resetViewBeforeLoading(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();
		this.setContentView(R.layout.potos_viewpager);
		initView();
		initData();
	}

	private void initView() {
		pager = (ViewPager) this.findViewById(R.id.pager);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				ShowPotosPagerActivity.this)
				.memoryCacheExtraOptions(768, 1024)
				// maxwidth, max height，即保存的每个缓存文件的最大长宽
				.threadPoolSize(3)
				// 线程池内加载的数量
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
				// 你可以通过自己的内存缓存实现
				.memoryCacheSize(2 * 1024 * 1024)
				.discCacheSize(50 * 1024 * 1024)
				.discCacheFileNameGenerator(new HashCodeFileNameGenerator())
				// 将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCacheFileCount(100) // 缓存的文件数量
				// .discCache(new UnlimitedDiskCache(new
				// File(MyApplication.IMG_CACHE_REAL_DIR)))//自定义缓存路径
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.imageDownloader(
						new BaseImageDownloader(ShowPotosPagerActivity.this,
								5 * 1000, 30 * 1000)) // connectTimeout (5 s),
														// readTimeout (30
														// s)超时时间
				.writeDebugLogs() // Remove for releaseapp
				.build();// 开始构建
		ImageLoader.getInstance().init(config);
	}

	private void initData() {
		try {
			imageUrlsList = getIntent().getStringArrayListExtra("imageUrlsList");
			imageUrls = imageUrlsList.toArray(new String[imageUrlsList.size()]);
			pager.setAdapter(new ImageAdapter());
			// pager.setCurrentItem(getArguments().getInt("IMAGE_POSITION", 0));
		} catch (Exception ignored) {
		}
	}
@Override
public void onBackPressed() {
	super.onBackPressed();
	finish();
}
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private class ImageAdapter extends PagerAdapter {
		private LayoutInflater inflater;

		ImageAdapter() {
			inflater = LayoutInflater.from(ShowPotosPagerActivity.this);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return imageUrls.length;
		}

		@Override
		public Object instantiateItem(ViewGroup view, final int position) {
			View imageLayout = inflater.inflate(R.layout.item_pager_image,
					view, false);
			assert imageLayout != null;
			ImageView imageView = (ImageView) imageLayout
					.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) imageLayout
					.findViewById(R.id.loading);
			imageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
//					String path = imageUrls[position];
//					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//					Uri mUri = Uri.parse("file://" + path);
//					intent.setDataAndType(mUri, "image/*");
//					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					intent.setAction(android.content.Intent.ACTION_VIEW);
//					ShowPotosPagerActivity.this.startActivity(intent);
				}
			});
			ImageLoader.getInstance().displayImage("file://" +imageUrls[position],
					imageView, options, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							spinner.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							String message = null;
							switch (failReason.getType()) {
							case IO_ERROR:
								message = "Input/Output error";
								break;
							case DECODING_ERROR:
								message = "Image can't be decoded";
								break;
							case NETWORK_DENIED:
								message = "Downloads are denied";
								break;
							case OUT_OF_MEMORY:
								message = "Out Of Memory error";
								break;
							case UNKNOWN:
								message = "Unknown error";
								break;
							}
							Toast.makeText(ShowPotosPagerActivity.this,
									message, Toast.LENGTH_SHORT).show();
							spinner.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							spinner.setVisibility(View.GONE);
						}
					});

			view.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		imageUrlsList.clear();
	}

}
