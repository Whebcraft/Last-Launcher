/*
 * Last Launcher
 * Copyright (C) 2019 Shubham Tyagi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.subhamtyagi.lastlauncher.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import io.github.subhamtyagi.lastlauncher.BuildConfig;
import io.github.subhamtyagi.lastlauncher.LauncherActivity;
import io.github.subhamtyagi.lastlauncher.R;
import io.github.subhamtyagi.lastlauncher.model.Apps;
import io.github.subhamtyagi.lastlauncher.utils.DbUtils;
import io.github.subhamtyagi.lastlauncher.utils.Utils;

/**
 * this the launcher setting Dialog
 */
public class GlobalSettingsDialog extends Dialog implements View.OnClickListener {


    private TextView freezeSize;
    private LauncherActivity launcherActivity;

    private Context context;

    public GlobalSettingsDialog(Context context, LauncherActivity launcherActivity) {
        super(context);
        this.context = context;
        this.launcherActivity = launcherActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // no old title: Last Launcher use Activity class not AppCompatActivity so it show very old title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_global_settings);


        findViewById(R.id.settings_themes).setOnClickListener(this);
        freezeSize = findViewById(R.id.settings_freeze_size);
        freezeSize.setOnClickListener(this);


        findViewById(R.id.settings_fonts).setOnClickListener(this);

        TextView reset = findViewById(R.id.settings_reset_to_defaults);
        reset.setOnClickListener(this);
        reset.setTextColor(Color.parseColor("#E53935"));
        findViewById(R.id.settings_backup).setOnClickListener(this);
        findViewById(R.id.settings_restore).setOnClickListener(this);
        findViewById(R.id.settings_alignment).setOnClickListener(this);
        findViewById(R.id.settings_padding).setOnClickListener(this);
        findViewById(R.id.settings_color_size).setOnClickListener(this);
        findViewById(R.id.settings_sort_app_by).setOnClickListener(this);

        //TODO: remove this var
        TextView colorSniffer = findViewById(R.id.settings_color_sniffer);
        colorSniffer.setOnClickListener(this);

        if (!BuildConfig.enableColorSniffer) {
            if (DbUtils.isRandomColor()) {
                colorSniffer.setText(R.string.fixed_colors);
            } else
                colorSniffer.setText(R.string.random_colors);
        }

        findViewById(R.id.settings_frozen_apps).setOnClickListener(this);
        findViewById(R.id.settings_hidden_apps).setOnClickListener(this);


        //reflect the DB value
        if (DbUtils.isSizeFrozen()) {
            freezeSize.setText(R.string.unfreeze_app_size);
        } else
            freezeSize.setText(R.string.freeze_apps_size);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settings_fonts:
                fontSelection(view);
                break;
            case R.id.settings_themes:
                showThemeDialog();
                break;
            case R.id.settings_color_sniffer: {
                if (BuildConfig.enableColorSniffer)
                    showColorSnifferDialog();
                else randomColor();
            }
            break;

            case R.id.settings_sort_app_by: {
                sortApps(view);
                break;
            }
            case R.id.settings_color_size: {
                showColorAndSizeDialog();
            }
            break;
            case R.id.settings_freeze_size:
                freezeAppsSize();
                break;
            case R.id.settings_hidden_apps:
                hiddenApps();
                break;
            case R.id.settings_frozen_apps:
                frozenApps();
                break;
            case R.id.settings_backup:
                backup();
                break;
            case R.id.settings_restore:
                restore();
                break;
            case R.id.settings_reset_to_defaults:
                defaultSettings();
                break;
            case R.id.settings_alignment:
                setFlowLayoutAlignment(view);
                break;
            case R.id.settings_padding:
                launcherActivity.setPadding();
                cancel();
                break;

        }
    }

    private void sortApps(View view) {
        Context context;
        // set theme
        // if theme wallpaper ie transparent then we have to show other theme
        if (DbUtils.getTheme() == R.style.Wallpaper)
            context = new ContextThemeWrapper(getContext(), R.style.AppTheme);
        else
            context = new ContextThemeWrapper(getContext(), DbUtils.getTheme());

        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.sort_apps_popups, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            cancel();
            switch (menuItem.getItemId()) {
                case R.id.menu_sort_by_name:
                    launcherActivity.sortApps(LauncherActivity.SORT_BY_NAME);
                    break;
                case R.id.menu_sort_by_opening_counts:
                    launcherActivity.sortApps(LauncherActivity.SORT_BY_OPENING_COUNTS);
                    break;
                case R.id.menu_sort_by_color:
                    launcherActivity.sortApps(LauncherActivity.SORT_BY_COLOR);
                    break;
               /* case R.id.menu_sort_by_customs:
                    launcherActivity.sortApps(LauncherActivity.SORT_BY_CUSTOM);
                    break;*/
                case R.id.menu_sort_by_size:
                    launcherActivity.sortApps(LauncherActivity.SORT_BY_SIZE);
                    break;
            }
            return true;
        });
        popupMenu.show();
    }

    private void showColorAndSizeDialog() {
        launcherActivity.setColorsAndSize();
        cancel();
    }

    private void setFlowLayoutAlignment(View view) {

        Context context;
        // set theme
        // if theme wallpaper ie transparent then we have to show other theme
        if (DbUtils.getTheme() == R.style.Wallpaper)
            context = new ContextThemeWrapper(getContext(), R.style.AppTheme);
        else
            context = new ContextThemeWrapper(getContext(), DbUtils.getTheme());

        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.alignment_popup, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_center:
                    launcherActivity.setFlowLayoutAlignment(Gravity.CENTER | Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                    break;
                case R.id.menu_end:
                    launcherActivity.setFlowLayoutAlignment(Gravity.END | Gravity.CENTER_VERTICAL);
                    break;
                case R.id.menu_start:
                    launcherActivity.setFlowLayoutAlignment(Gravity.START | Gravity.CENTER_VERTICAL);
                    break;
            }
            return true;
        });
        popupMenu.show();

    }

    private void randomColor() {
        boolean rColor = !DbUtils.isRandomColor();
        DbUtils.randomColor(rColor);
        cancel();

        int color;
        for (Apps app : LauncherActivity.mAppsList) {

            color = DbUtils.getAppColor(app.getActivityName());

            if (rColor) {
                if (color == DbUtils.NULL_TEXT_COLOR)
                    color = Utils.generateColorFromString(app.getActivityName());
            } else {
                color = DbUtils.getAppColor(app.getActivityName());
            }
            app.getTextView().setTextColor(color);
        }
    }

    private void showColorSnifferDialog() {
        cancel();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage("ryey.colorsniffer");

        // if color snifer app is not installed then send user to install it
        // else show color sniffer option
        if (intent == null) {
            Uri uri = Uri.parse("market://details?id=ryey.colorsniffer");
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else {
            new ColorSnifferDialog(getContext(), launcherActivity).show();

        }
    }

    private void freezeAppsSize() {
        boolean b = DbUtils.isSizeFrozen();
        DbUtils.freezeSize(!b);
        if (!b) {
            freezeSize.setText(R.string.unfreeze_app_size);
        } else
            freezeSize.setText(R.string.freeze_apps_size);
    }

    private void frozenApps() {

        launcherActivity.showFrozenApps();
        cancel();

    }

    //show hidden apps
    private void hiddenApps() {
        launcherActivity.showHiddenApps();
        cancel();
    }

    private void showThemeDialog() {
        cancel();
        new ThemeSelectorDialog(getContext(), launcherActivity).show();
    }

    private void defaultSettings() {
        DbUtils.clearDB();
        launcherActivity.recreate();
    }

    private void backup() {
        if (launcherActivity.isPermissionRequired())
            launcherActivity.requestPermission();
        else {
            boolean b = DbUtils.saveDbTOFile();
            cancel();

            Toast toast = Toast.makeText(getContext(), b ? R.string.backup_saved_to_downloads : R.string.some_error_occurred, Toast.LENGTH_LONG);
            toast.getView().setBackgroundColor(Color.parseColor("#d5e0e2"));
            toast.show();
        }
    }

    private void restore() {
        if (launcherActivity.isPermissionRequired())
            launcherActivity.requestPermission();
        else {
            launcherActivity.browseFile();
            cancel();

            Toast toast = Toast.makeText(getContext(), R.string.choose_old_backup_file, Toast.LENGTH_LONG);
            toast.getView().setBackgroundColor(Color.parseColor("#d5e0e2"));
            toast.show();

        }
    }

    private void setFonts() {
        if (launcherActivity.isPermissionRequired())
            launcherActivity.requestPermission();
        else {
            launcherActivity.browseFonts();
            cancel();
        }
    }

    private void fontSelection(View view) {

        Context context;
        // set theme
        // if theme wallpaper ie transparent then we have to show other theme
        if (DbUtils.getTheme() == R.style.Wallpaper)
            context = new ContextThemeWrapper(getContext(), R.style.AppTheme);
        else
            context = new ContextThemeWrapper(getContext(), DbUtils.getTheme());

        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.font_selection_popup, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_choose_fonts:
                    setFonts();
                    break;
                case R.id.menu_default_font: {
                    if (DbUtils.isFontExists()) {
                        DbUtils.removeFont();
                        launcherActivity.setFont();
                        launcherActivity.loadApps();
                        cancel();
                        break;
                    }
                }
            }
            return true;
        });
        popupMenu.show();

    }

}