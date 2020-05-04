package com.example.notes;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

public class exampleItem {

    private String titleText;
    private String subText;
    private Boolean checked;

    public exampleItem(String tText, String sText, Boolean check) {
        titleText = tText;
        subText = sText;
        checked = check;
    }

    public String getTitleText() {
        return titleText;
    }

    public String getSubText() {
        return subText;
    }

    public Boolean getChecked() {
        return checked;
    }
}
