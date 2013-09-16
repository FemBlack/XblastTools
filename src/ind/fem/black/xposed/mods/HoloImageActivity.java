package ind.fem.black.xposed.mods;

import ind.fem.black.xposed.dialogs.SaveDialog;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.Window;
import android.widget.Toast;

public class HoloImageActivity extends Activity {
	
	 private static final int HOLO_IMAGE = 1088;
     private static Context context;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
      /* wallpaperImage = new File(context.getFilesDir() + "/holoImage");
        wallpaperTemporary = new File(context.getCacheDir() + "/holoImage.tmp");*/
        setDefaultCallerImage();
    }
	
	@SuppressWarnings("deprecation")
    private void setDefaultCallerImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		//Intent intent = new Intent();
		
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        Rect rect = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;
        int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentViewTop - statusBarHeight;
        // Lock screen for tablets visible section are different in landscape/portrait,
        // image need to be cropped correctly, like wallpaper setup for scrolling in background in home screen
        // other wise it does not scale correctly
        if (Black.isTablet(context)) {
            width = context.getWallpaperDesiredMinimumWidth();
            height = context.getWallpaperDesiredMinimumHeight();
            float spotlightX = (float) display.getWidth() / width;
            float spotlightY = (float) display.getHeight() / height;
            intent.putExtra("aspectX", width);
            intent.putExtra("aspectY", height);
            intent.putExtra("outputX", width);
            intent.putExtra("outputY", height);
            intent.putExtra("spotlightX", spotlightX);
            intent.putExtra("spotlightY", spotlightY);
        } else {
            boolean isPortrait = getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT;
            intent.putExtra("aspectX", isPortrait ? width : height - titleBarHeight);
            intent.putExtra("aspectY", isPortrait ? height - titleBarHeight : width);
        }
        try {
        	String path = Environment.getExternalStorageDirectory()
    				.getAbsolutePath() + File.separator + SaveDialog.BACKUP_DIR + File.separator + HoloBg.HOLO_DIR;
        	File dir = new File(path);
    		dir.mkdirs();
        	intent.putExtra("output", Uri.fromFile(new File(path, "holo.png")));
        	/*intent.setAction("android.intent.action.GET_CONTENT");
        	intent.addCategory("android.intent.category.OPENABLE");*/
            startActivityForResult(intent,HOLO_IMAGE);
            //((Activity) context).startActivityFromFragment(this, intent, LOCKSCREEN_BACKGROUND);
        } catch (Throwable e) {
            Toast.makeText(context, getString(
                    R.string.holo_image_result_not_successful),
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == HOLO_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
               /* if (wallpaperTemporary.exists()) {
                    wallpaperTemporary.renameTo(wallpaperImage);
                }
                wallpaperImage.setReadable(true, false);*/
                
                Toast.makeText(context, getString(
                        R.string.holo_image_result_successful),
                        Toast.LENGTH_SHORT).show();
            } else {
                /*if (wallpaperTemporary.exists()) {
                    wallpaperTemporary.delete();
                }*/
                Toast.makeText(context, getString(
                        R.string.holo_image_result_not_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
        /*Toast.makeText(context, "finished",
                Toast.LENGTH_SHORT).show();*/
    }
    
}

