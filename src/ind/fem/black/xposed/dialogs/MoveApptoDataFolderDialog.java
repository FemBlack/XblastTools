package ind.fem.black.xposed.dialogs;

import ind.fem.black.xposed.mods.R;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.ramdroid.roottools.ex.AsyncShell;
import com.ramdroid.roottools.ex.ErrorCode;

public class MoveApptoDataFolderDialog extends DialogFragment
{
	public static MoveApptoDataFolderDialog newInstance()
	  {
		MoveApptoDataFolderDialog localPromptMoveAppDialog = new MoveApptoDataFolderDialog();
	    localPromptMoveAppDialog.setArguments(new Bundle());
	    return localPromptMoveAppDialog;
	  }
	
  public Dialog onCreateDialog(Bundle paramBundle)
  {
    setCancelable(false);
    return new AlertDialog.Builder(getActivity()).setTitle(R.string.move_internal_storage).setMessage(R.string.move_message).setCancelable(false).setPositiveButton(R.string.move, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface dialogInterface, int value)
      {
        try
        {
          ProgressDialog.show(MoveApptoDataFolderDialog.this.getActivity(), "", getResources().getString(R.string.move_internal_storage), true);
          String str1 = "cat " + MoveApptoDataFolderDialog.this.getActivity().getApplicationInfo().sourceDir + " > " + MoveApptoDataFolderDialog.this.getActivity().getExternalCacheDir().getAbsolutePath() + "/tmp.apk";
          String str2 = "pm install -r -f " + MoveApptoDataFolderDialog.this.getActivity().getExternalCacheDir().getAbsolutePath() + "/tmp.apk";
         
          AsyncShell.send(true, str1 + " && " + str2, new ErrorCode.OutputListener()
          {
            public void onResult(int paramAnonymous2Int, List<String> paramAnonymous2List) {
            	
            }
          });
          return;
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
          Toast.makeText(MoveApptoDataFolderDialog.this.getActivity(), R.string.move_failed, Toast.LENGTH_SHORT).show();
          MoveApptoDataFolderDialog.this.getActivity().finish();
        }
      }
    }).setNegativeButton(R.string.quit, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {    	 
        MoveApptoDataFolderDialog.this.getActivity().finish();
        MoveApptoDataFolderDialog.this.dismiss();
      }
    }).create();
  }
  
  /*@Override
  public void onDetach() {
	  super.onDetach();
	  System.out.println("onDetach");
	   Intent intent = new Intent(Intent.ACTION_MAIN);
      intent.setComponent(new ComponentName(XblastSettings.PACKAGE_NAME,"ind.fem.black.xposed.mods.XblastSettings"));
      System.out.println("onDetach reactivity");
      intent.addCategory("android.intent.category.LAUNCHER");
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      //startActivity(act.getIntent());
      //intent.setClassName(XblastSettings.PACKAGE_NAME, "ind.fem.black.xposed.mods.XblastSettings");
      startActivity(intent);
      System.out.println("onDetach reactivity");
  }
  @Override
  public void onDestroyView() {
	  super.onDestroyView();
	  System.out.println("onDestroyView");
	  Intent intent = new Intent(Intent.ACTION_MAIN);
      intent.setComponent(new ComponentName(XblastSettings.PACKAGE_NAME,"ind.fem.black.xposed.mods.XblastSettings"));
      System.out.println("onDestroyView reactivity");
      intent.addCategory("android.intent.category.LAUNCHER");
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      //startActivity(act.getIntent());
      //intent.setClassName(XblastSettings.PACKAGE_NAME, "ind.fem.black.xposed.mods.XblastSettings");
      startActivity(intent);
      System.out.println("onDestroyView reactivity");
  }*/
}