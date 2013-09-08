package ind.fem.black.xposed.mods;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Window;
import android.widget.Toast;

public class XDreamImageActivity extends Activity {
	
	 private File wallpaperImage;
     private File wallpaperTemporary;
     static final int XDREAM_IMAGE = 1077;
     static Context context;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        wallpaperImage = new File(context.getFilesDir() + "/xDreamImage");
        wallpaperTemporary = new File(context.getCacheDir() + "/xDreamImage.tmp");
        setDefaultCallerImage();
    }
	
	@SuppressWarnings("deprecation")
    private void setDefaultCallerImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
            wallpaperTemporary.createNewFile();
            wallpaperTemporary.setWritable(true, false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(wallpaperTemporary));
            intent.putExtra("return-data", false);
            startActivityForResult(intent,XDREAM_IMAGE);
            //((Activity) context).startActivityFromFragment(this, intent, LOCKSCREEN_BACKGROUND);
        } catch (Throwable e) {
            Toast.makeText(context, getString(
                    R.string.xdream_image_result_not_successful),
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == XDREAM_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (wallpaperTemporary.exists()) {
                    wallpaperTemporary.renameTo(wallpaperImage);
                }
                wallpaperImage.setReadable(true, false);
                
                Toast.makeText(context, getString(
                        R.string.xdream_image_result_successful),
                        Toast.LENGTH_SHORT).show();
            } else {
                if (wallpaperTemporary.exists()) {
                    wallpaperTemporary.delete();
                }
                Toast.makeText(context, getString(
                        R.string.xdream_image_result_not_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
        /*Toast.makeText(context, "finished",
                Toast.LENGTH_SHORT).show();*/
    }
    
}

