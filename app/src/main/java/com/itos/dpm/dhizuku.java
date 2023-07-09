package com.itos.dpm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;
import android.content.ActivityNotFoundException;

import com.rosan.dhizuku.api.Dhizuku;

public class dhizuku {


    private static Activity activity;
    private static AlertDialog dialog;


//    public static void initialize(Context c) {
//        if (Dhizuku.init()) {
//            Toast.makeText(c, "Dhizuku已授权", Toast.LENGTH_SHORT).show();
//        } else {
//
//        }
//    }

    public static void setActivity(Activity activity) {
        dhizuku.activity = activity;
    }

    public static void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Dhizuku异常");
        builder.setMessage(message);
        builder.setPositiveButton("下载Dhizuku最新版 (密码9g7p)", (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://iamr0s.lanzoul.com/b02ki3edg"));
            activity.startActivity(intent);
            activity.finish();
        });
        builder.setNegativeButton("加入QQ群", (dialog, which) -> {
            Uri uri = Uri.parse("mqqapi://card/show_pslcard?src_type=internal&version=1&uin=262040855&card_type=group&source=qrcode");
            try {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                activity.finish();
            } catch (ActivityNotFoundException e) {
                Toast.makeText(activity, "未安装QQ或版本不支持", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setCancelable(false);
        dialog = builder.create();
        View dialogView = dialog.getWindow().getDecorView();
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(45);
        drawable.setColor(Color.WHITE);
        dialogView.setBackground(drawable);
        dialog.show();
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                activity.finish();
            }
        }, 5000);
    }
}