package com.nebulacompanies.nebula.CustomerBooking.Utils.Model;

import android.graphics.drawable.Drawable;
import android.net.Uri;

public interface ChipInterface {

    Object getId();
    Uri getAvatarUri();
    Drawable getAvatarDrawable();
    String getLabel();
    String getInfo();
}
