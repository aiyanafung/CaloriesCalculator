package edu.northeastern.numad22fa_group10;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

public class LoadingBar {
    Context context;
    Dialog dialog;

    public LoadingBar(Context context) {
        this.context = context;
    }
    public void showDialog(String title) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.progress_bar);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView titleView = dialog.findViewById(R.id.textView);
        titleView.setText(title);
        dialog.create();
        dialog.show();
    }

    public void hideDialog(){
        dialog.dismiss();
    }
}
