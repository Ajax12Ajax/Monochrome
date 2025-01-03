package com.pixelvalue.monochrome;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import java.util.Objects;

public class MonochromeTileService extends TileService {

    boolean permission = true;
    boolean active = false;

    @Override
    public void onStartListening() {
        Tile tile = getQsTile();
        permission = this.checkSelfPermission(android.Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        if (tile != null) {
            if (!permission) {
                tile.setState(Tile.STATE_ACTIVE);
            } else if (Objects.equals(Settings.Secure.getString(this.getContentResolver(), "accessibility_display_daltonizer_enabled"), "1") &&
                    Objects.equals(Settings.Secure.getString(this.getContentResolver(), "accessibility_display_daltonizer"), "0")) {
                tile.setState(Tile.STATE_ACTIVE);
                active = true;
            } else {
                tile.setState(Tile.STATE_INACTIVE);
                active = false;
            }
            tile.updateTile();
        }
        super.onStartListening();
    }

    @Override
    public void onClick() {
        if (!permission) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Tile tile = getQsTile();
            if (!active) {
                Settings.Secure.putString(this.getContentResolver(), "accessibility_display_daltonizer_enabled", "1");
                Settings.Secure.putString(this.getContentResolver(), "accessibility_display_daltonizer", "0");
                tile.setState(Tile.STATE_ACTIVE);
            } else {
                Settings.Secure.putString(this.getContentResolver(), "accessibility_display_daltonizer_enabled", "0");
                Settings.Secure.putString(this.getContentResolver(), "accessibility_display_daltonizer", "-1");
                tile.setState(Tile.STATE_INACTIVE);
            }
            tile.updateTile();
        }
    }
}
