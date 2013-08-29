package ind.fem.black.xposed.dialogs;

import ind.fem.black.xposed.adapters.BackupAdapter;
import ind.fem.black.xposed.mods.R;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

public class RestoreDialog extends DialogFragment {

	private String path;
	private File dir;
	private File[] backups;
	private AlertDialog dialog;
	private RestoreDialogListener listener;
	private BackupAdapter adapter;
	private ListView listView;

	public RestoreDialog() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.listener = (RestoreDialogListener) activity;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		path = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator + SaveDialog.BACKUP_DIR;
		LinearLayout linearLayout = new LinearLayout(getActivity());
		listView = new ListView(getActivity());
		linearLayout.addView(listView);
		TextView emptyView = new TextView(getActivity(), null,
				android.R.layout.simple_list_item_1);
		emptyView
				.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT,
						(int) (Resources.getSystem().getDisplayMetrics().density * 48)));
		emptyView.setGravity(Gravity.CENTER);
		linearLayout.addView(emptyView);
		emptyView.setText(R.string.no_backups);
		listView.setEmptyView(emptyView);
		dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		setupData();

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				listener.onRestoreBackup(backups[arg2]);
				dialog.dismiss();
			}

		});
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				final PopupMenu menu = new PopupMenu(getActivity(), arg1);
				menu.inflate(R.menu.backup_item);
				//menu.dismiss();
				menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						if (item.getItemId() == R.id.action_delete) {

							backups[arg2].delete();
							adapter.notifyDataSetChanged();

							setupData();
							if (backups == null || backups.length == 0) {
								dialog.dismiss();
							}

						}
						return true;
					}
				});
				menu.show();
				return true;
			}
		});
		dialog = builder
				.setCancelable(true)
				.setTitle(R.string.restore)
				.setView(linearLayout)
				.setPositiveButton(R.string.reset, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						listener.onRestoreDefaults();
					}
				})
				.setNegativeButton(android.R.string.cancel,
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create();
		return dialog;

	}

	private boolean setupData() {
		backups = dir.listFiles();
		if (backups == null || backups.length == 0) {
			return false;
		}
		adapter = new BackupAdapter(getActivity(), backups);
		listView.setAdapter(adapter);
		return true;
	}

	public interface RestoreDialogListener {
		public void onRestoreDefaults();

		public void onRestoreBackup(File file);
	}

}
