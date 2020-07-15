package com.sncity.zealo.sungnamgift.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sncity.zealo.sungnamgift.R;

/**
 * Created by zealo on 2017-09-29.
 */

public class FeedBackDialog {

    public static AlertDialog makeDialog(final Context context, Activity activity, final String storeName) {

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_custom, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();

        Button btn_gps = (Button)view.findViewById(R.id.btnGpsStart);
        Button btn_close = (Button)view.findViewById(R.id.btnClose);
        btn_gps.setText("보내기");

        TextView tx_title = (TextView)view.findViewById(R.id.Tx_dialog_title);
        tx_title.setText("신고하기");
        TextView tx_text = (TextView)view.findViewById(R.id.Tx_dialog_text);
        tx_text.setText("메일을 보내시겠습니까?");

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] address = {"ksw4015@gmail.com"};

                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setData(Uri.parse("mailto:"));
                i.putExtra(Intent.EXTRA_EMAIL, address);
                i.putExtra(Intent.EXTRA_SUBJECT, "잘못된 가맹점 신고");
                i.putExtra(Intent.EXTRA_TEXT, "상호 : " + storeName);
                context.startActivity(Intent.createChooser(i, "메일을 보내기위해 사용할 앱을 선택하세요."));
            }
        });

        dialog.setView(view);
        return dialog;
    }
}
