package com.chinadci.mel.mleo.comands;

import java.io.File;

import android.app.Activity;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.chinadci.mel.mleo.core.Command;

public class MapUpCommand extends Command {
	@Override
	public Object run(Activity activity, View view, Object object) {
		if (fileIsExists(Environment.getExternalStorageDirectory()
				+ "/FJGouTuYiDongZhiFa/cache/")) {
			deleteAllFile(activity,Environment.getExternalStorageDirectory()
					+ "/FJGouTuYiDongZhiFa/cache/");
		}else{
			Toast.makeText(activity, "您的地图已是最新", Toast.LENGTH_SHORT).show();
		}
		return null;
	}
	private boolean fileIsExists(String Path){
        try{
        	File f=new File(Path);
            if(!f.exists()){
            	return false;
            }
        }catch (Exception e) {
               return false;
        }
        return true;
	}
	private void deleteAllFile(Activity activity,String path) {
		deleteFile(new File(path));
		Toast.makeText(activity, "更新成功", Toast.LENGTH_SHORT).show();
	}
	
	private void deleteFile(File oldPath) {
		if (oldPath.isDirectory()) {
			File[] files = oldPath.listFiles();
			for (File file : files) {
				deleteFile(file);
				file.delete();
			}
		} else {
			oldPath.delete();
		}
	}
}
