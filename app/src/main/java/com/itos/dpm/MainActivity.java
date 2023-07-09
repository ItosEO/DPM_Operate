package com.itos.dpm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.rosan.dhizuku.api.Dhizuku;
import com.rosan.dhizuku.api.DhizukuRequestPermissionListener;
import com.rosan.dhizuku.api.DhizukuUserServiceArgs;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends Activity implements View.OnClickListener {
    DevicePolicyManager mDevicePolicyManager;
    private IUserService service;
    ComponentName componentName;
    private static final String[] REQUIRED_DELEGATED_SCOPES = new String[]{
            DevicePolicyManager.DELEGATION_BLOCK_UNINSTALL,
            DevicePolicyManager.DELEGATION_PACKAGE_ACCESS,
    };

    EditText pkg_editText, screen_editText;

    String[] options = {"安装APP", "卸载APP", "ADB调试"};
    Spinner spinner;
    String UserRestriction;
    String selectedItem;

    private boolean isDeviceOwner(){
        boolean isDeviceOwnerApp = mDevicePolicyManager.isDeviceOwnerApp(componentName.getPackageName());
        Log.d("TAG", "isDeviceOwnerApp: " + isDeviceOwnerApp);
        return isDeviceOwnerApp;
    }
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置当前页状态栏为白色
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white));

        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        if (!Dhizuku.init(this)) {
            ShowToastL(this, "Dhizuku 初始化失败,请安装或启动 Dhizuku 应用程序,然后重新启动本APP。");
            finish();
            return;
        }
        if (Dhizuku.getVersionCode() < 5) {
            ShowToastL(this, "请更新您的 Dhizuku 版本(需要2.8及以上)");
            finish();
            return;
        }
        if (!Dhizuku.isPermissionGranted()) {
            Dhizuku.requestPermission(new DhizukuRequestPermissionListener() {
                @Override
                public void onRequestPermission(int grantResult) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED){
                        setDelegatedScopes();
                        bindUserService();
                    } else{
                        ShowToastL(MainActivity.this, String.valueOf(R.string.dhizuku_permission_denied));
                        finish();
                    }
                }
            });
        } else {
            bindUserService();
            setDelegatedScopes();
        }

        pkg_editText = findViewById(R.id.pkg_edit_text);
        screen_editText = findViewById(R.id.screen_edit_text);
        findViewById(R.id.unblock_uninstall).setOnClickListener(this);
        findViewById(R.id.block_uninstall).setOnClickListener(this);
        findViewById(R.id.check_unblock_uninstall).setOnClickListener(this);
        findViewById(R.id.change_screen_text).setOnClickListener(this);
        findViewById(R.id.screen_capture_disabled).setOnClickListener(this);
        findViewById(R.id.screen_capture_enabled).setOnClickListener(this);
        findViewById(R.id.camera_disabled).setOnClickListener(this);
        findViewById(R.id.camera_enabled).setOnClickListener(this);
        findViewById(R.id.user_restriction_disabled).setOnClickListener(this);
        findViewById(R.id.user_restriction_enabled).setOnClickListener(this);
        findViewById(R.id.set_org_name).setOnClickListener(this);
        findViewById(R.id.join).setOnClickListener(this);

        spinner = findViewById(R.id.spinner);
        // 创建适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 设置适配器
        spinner.setAdapter(adapter);
        // 设置选择项监听
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals(options[0])){
                    UserRestriction=UserManager.DISALLOW_INSTALL_APPS;
                } else if (selectedItem.equals(options[1])) {
                    UserRestriction=UserManager.DISALLOW_UNINSTALL_APPS;
                } else {
                    UserRestriction=UserManager.DISALLOW_DEBUGGING_FEATURES;
                }
                Toast.makeText(MainActivity.this, "选择了：" + selectedItem, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}

        });
    }

    void bindUserService() {
        DhizukuUserServiceArgs args = new DhizukuUserServiceArgs(new ComponentName(this, UserService.class));
        boolean bind = Dhizuku.bindUserService(args, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder iBinder) {
                ShowToastS(MainActivity.this,"已连接到 UserService");
                service = IUserService.Stub.asInterface(iBinder);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                ShowToastS(MainActivity.this,"UserService 已断开链接");
            }
        });
        if (bind) return;
        ShowToastS(MainActivity.this,"start user service failed");
    }
    private boolean checkDelegatedScopes() {
        return new ArrayList<>(Arrays.asList(Dhizuku.getDelegatedScopes())).containsAll(Arrays.asList(REQUIRED_DELEGATED_SCOPES));
    }

    private void setDelegatedScopes() {
        if (checkDelegatedScopes()) return;
        Dhizuku.setDelegatedScopes(REQUIRED_DELEGATED_SCOPES);
    }

    public void ShowToastS(final Context context, final String text) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
    public void ShowToastL(final Context context, final String text) {
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    private String pkg2name(String pakeage){
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(pakeage, 0);
            return packageManager.getApplicationLabel(applicationInfo).toString();
            // 在这里使用appName变量，即应用程序的名称
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /****************
     *
     * 发起添加群流程。群号：IQOO⭐️交流群(262040855) 的 key 为： SqLJvDGqjKNDvc_O5dx6A164eLSo4QBG
     * 调用 joinQQGroup(SqLJvDGqjKNDvc_O5dx6A164eLSo4QBG) 即可发起手Q客户端申请加群 IQOO⭐️交流群(262040855)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回false表示呼起失败
     ******************/
    private boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        String packageName = pkg_editText.getText().toString();
        String screen_text = screen_editText.getText().toString();
        if (id == R.id.block_uninstall) {
            if (!pkg2name(packageName).equals("")) {
                mDevicePolicyManager.setUninstallBlocked(componentName, packageName, true);
                ShowToastS(this, "已尝试限制 " + pkg2name(packageName) + " 卸载");
            } else {
                ShowToastS(this, "未安装此软件");
            }
        } else if (id == R.id.unblock_uninstall) {
            if (!pkg2name(packageName).equals("")) {
                mDevicePolicyManager.setUninstallBlocked(componentName, packageName, false);
                ShowToastS(this, "已尝试解除限制 " + pkg2name(packageName) + " 卸载");
            } else {
                ShowToastS(this, "未安装此软件");
            }
        } else if (id == R.id.check_unblock_uninstall) {
            if (!pkg2name(packageName).equals("")) {
                boolean pkg_block_status = mDevicePolicyManager.isUninstallBlocked(componentName, packageName);
                if (pkg_block_status){
                    ShowToastS(this, pkg2name(packageName)+" 无法被卸载");
                } else {
                    ShowToastS(this, pkg2name(packageName)+" 可以被卸载");
                }
            } else {
                ShowToastS(this, "未安装此软件");
            }
        } else if (id == R.id.change_screen_text){
            try {
                service.change_screen_text(screen_text);
                ShowToastS(this,"已设置锁屏提示文字为: "+screen_text);
            } catch (RemoteException e) {
                ShowToastS(this,"设置失败");
            }
        } else if (id == R.id.screen_capture_disabled){
            try {
                service.set_screen_capture(true);
                ShowToastS(this, "已尝试禁用截屏");
            } catch (RemoteException e) {
                ShowToastS(this, "禁用截屏失败");
            }
        } else if (id == R.id.screen_capture_enabled){
            try {
                service.set_screen_capture(false);
                ShowToastS(this, "已尝试启用截屏");
            } catch (RemoteException e) {
                ShowToastS(this, "启用截屏失败");
            }
        } else if (id == R.id.camera_disabled){
            try {
                service.set_camera_capture(true);
                ShowToastS(this, "已尝试禁用相机");
            } catch (RemoteException e) {
                ShowToastS(this, "禁用相机失败");
            }
        } else if (id == R.id.camera_enabled){
            try {
                service.set_camera_capture(false);
                ShowToastS(this, "已尝试启用相机");
            } catch (RemoteException e) {
                ShowToastS(this, "启用相机失败");
            }
        } else if (id == R.id.user_restriction_enabled){
            try {
                service.add_user_restriction(UserRestriction);
                ShowToastS(this, "已尝试禁用"+selectedItem);
            } catch (RemoteException e) {
                ShowToastS(this, "禁用"+selectedItem+"失败");
            }
        } else if (id == R.id.user_restriction_disabled){
            try {
                service.clear_user_restriction(UserRestriction);
                ShowToastS(this, "已尝试启用"+selectedItem);
            } catch (RemoteException e) {
                ShowToastS(this, "启用"+selectedItem+"失败");
            }
        } else if (id == R.id.set_org_name){
            try {
                service.set_org_name(screen_text);
                ShowToastS(this,"已设置组织名为: "+screen_text);
            } catch (RemoteException e) {
                ShowToastS(this,"设置失败");
            }
        } else if (id == R.id.join){
            boolean is_join_succeed = joinQQGroup("SqLJvDGqjKNDvc_O5dx6A164eLSo4QBG");
            if (!is_join_succeed){
                Toast.makeText(this,"未安装手Q或安装的版本不支持",Toast.LENGTH_SHORT).show();
            }
        }
    }
}

