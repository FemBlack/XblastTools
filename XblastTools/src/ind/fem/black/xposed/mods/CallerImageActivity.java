package ind.fem.black.xposed.mods;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

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

public class CallerImageActivity extends Activity {
	
	 private File wallpaperImage;
     private File wallpaperTemporary;
     private static final int DEFAULT_CALLER_IMAGE = 1024;
     static Context context;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        /*wallpaperImage = new File(context.getFilesDir() + "/defaultCallerImage");
        wallpaperTemporary = new File(context.getCacheDir() + "/defaultCallerImage.tmp");*/
        setDefaultCallerImage();
    }
	
	@SuppressWarnings("deprecation")
    private void setDefaultCallerImage() {
        /*Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
        }*/
        try {
        	Intent photoIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        	photoIntent.setType("image/*");
        	//photoIntent.setAction(Intent.ACTION_GET_CONTENT);
        	/*photoIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        	photoIntent.putExtra("crop", "true");
        	photoIntent.putExtra("scale", true);
        	photoIntent.putExtra("scaleUpIfNeeded", false);*/
        	photoIntent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        	startActivityForResult(photoIntent,	DEFAULT_CALLER_IMAGE);
            //startActivityForResult(intent,DEFAULT_CALLER_IMAGE);
            //((Activity) context).startActivityFromFragment(this, intent, LOCKSCREEN_BACKGROUND);
        } catch (Throwable e) {
            Toast.makeText(context, getString(
                    R.string.default_caller_image_result_not_successful),
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DEFAULT_CALLER_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
            	Uri selectedImage = data.getData();
            	XblastSettings.mPrefs.edit().putString("callerBgImageUri",
            	selectedImage.toString()).commit();
            	/*getPreferenceManager().getSharedPreferences().edit()
            	.putBoolean("notificationPanelBackgroundEnabled", true)
            	.commit();
                wallpaperImage.setReadable(true, false);*/
                Toast.makeText(context, getString(
                        R.string.default_caller_image_result_successful),
                        Toast.LENGTH_SHORT).show();
            } else {
               /* if (wallpaperTemporary.exists()) {
                    wallpaperTemporary.delete();
                }*/
                Toast.makeText(context, getString(
                        R.string.default_caller_image_result_not_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
        /*Toast.makeText(context, "finished",
                Toast.LENGTH_SHORT).show();*/
    }
    
	public static String executeScript(String name) {
		File scriptFile = writeAssetToCacheFile(name);
		if (scriptFile == null)
			return "Could not find asset \"" + name + "\"";

		File busybox = writeAssetToCacheFile("busybox-xposed");
		if (busybox == null) {
			scriptFile.delete();
			return "Could not find asset \"busybox-xposed\"";
		}

		scriptFile.setReadable(true, false);
		scriptFile.setExecutable(true, false);

		busybox.setReadable(true, false);
		busybox.setExecutable(true, false);

		try {
			Process p = Runtime.getRuntime().exec(
					new String[] {
							"su",
							"-c",
							scriptFile.getAbsolutePath() + " "
									+ android.os.Process.myUid() + " 2>&1" },
					null, context.getCacheDir());
			BufferedReader stdout = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = stdout.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
			}
			stdout.close();
			return sb.toString();

		} catch (IOException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			return sw.toString();
		} finally {
			scriptFile.delete();
			busybox.delete();
		}
	}

	public static File writeAssetToCacheFile(String name) {
		return writeAssetToCacheFile(name, name);
	}

	public static File writeAssetToCacheFile(String assetName, String fileName) {
		File file = null;
		try {
			InputStream in = context.getAssets().open(assetName);
			file = new File(context.getCacheDir(), fileName);
			FileOutputStream out = new FileOutputStream(file);

			byte[] buffer = new byte[1024];
			int len;
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
			in.close();
			out.close();

			return file;
		} catch (IOException e) {
			e.printStackTrace();
			if (file != null)
				file.delete();

			return null;
		}
	}
}

