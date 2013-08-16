package ind.fem.black.xposed.quicksettings;

import ind.fem.black.xposed.mods.R;
import ind.fem.black.xposed.mods.XblastSettings;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class GravityBoxTile extends AQuickSettingsTile {

    public GravityBoxTile(Context context, Context gbContext, Object statusBar, Object panelBar) {
        super(context, gbContext, statusBar, panelBar);

        mOnClick = new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClassName(XblastSettings.PACKAGE_NAME, XblastSettings.class.getName());
                startActivity(i);
            }
        };
    }

    @Override
    protected void onTileCreate() {
        mDrawableId = R.drawable.ic_launcher;
        mLabel = "GravityBox";

        LayoutInflater inflater = LayoutInflater.from(mGbContext);
        inflater.inflate(R.layout.quick_settings_tile_gravity, mTile);
    }

    @Override
    protected synchronized void updateTile() {
        TextView tv = (TextView) mTile.findViewById(R.id.gravitybox_tileview);
        tv.setText(mLabel);
        tv.setCompoundDrawablesWithIntrinsicBounds(0, mDrawableId, 0, 0);
    }
}