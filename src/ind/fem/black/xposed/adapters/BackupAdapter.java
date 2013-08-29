package ind.fem.black.xposed.adapters;

import java.io.File;
import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BackupAdapter extends BaseAdapter {

	private Context context;
	private File[] backups;

	public BackupAdapter(Context context, File[] backups) {
		this.context = context;
		this.backups = backups;
	}

	@Override
	public int getCount() {
		return backups.length;
	}

	@Override
	public Object getItem(int position) {
		return backups[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = LayoutInflater.from(context).inflate(
				android.R.layout.simple_list_item_2, null);
		((TextView) v.findViewById(android.R.id.text1))
				.setText(backups[position].getName().replace(".fem", ""));
		((TextView) v.findViewById(android.R.id.text2)).setText(new Date(
				backups[position].lastModified()).toLocaleString());

		return v;
	}

}
