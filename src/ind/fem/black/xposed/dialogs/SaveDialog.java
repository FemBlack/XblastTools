package ind.fem.black.xposed.dialogs;

import ind.fem.black.xposed.mods.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

public class SaveDialog extends DialogFragment {

	public static final String BACKUP_DIR = "XBlastTools";
	private AlertDialog dialog;

	public SaveDialog() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final EditText editText = new EditText(getActivity());
		editText.setHint(R.string.backup_name);
		editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (count == 0) {
					dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
				} else {
					dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		dialog = builder
				.setCancelable(true)
				.setTitle(R.string.save)
				.setView(editText)
				.setPositiveButton(R.string.save, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (savePreferencesToSdCard(editText.getText()
								.toString())) {
							Toast.makeText(getActivity(),
									R.string.save_successful,
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getActivity(),
									R.string.save_unsuccessful,
									Toast.LENGTH_SHORT).show();
						}
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

	@Override
	public void onStart() {
		super.onStart();
		dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
	}

	protected boolean savePreferencesToSdCard(String string) {
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator + BACKUP_DIR;
		File dir = new File(path);
		dir.mkdirs();

		File file = new File(dir, string + ".fem");

		boolean res = false;
		ObjectOutputStream output = null;
		try {
			output = new ObjectOutputStream(new FileOutputStream(file));
			SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			output.writeObject(pref.getAll());

			res = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (output != null) {
					output.flush();
					output.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return res;
	}

}
