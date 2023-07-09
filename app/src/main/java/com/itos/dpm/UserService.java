package com.itos.dpm;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.net.ProxyInfo;
import android.net.Uri;
import android.os.RemoteException;

//import androidx.annotation.Keep;

import com.rosan.dhizuku.shared.DhizukuVariables;

public class UserService extends IUserService.Stub{
    private Context context;
    private DevicePolicyManager devicePolicyManager;

    //@Keep
    public UserService(Context context) {
        this.context = context;
        devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }
    @Override
    public void onCreate() {
    }
    @Override
    public void onDestroy() {

    }
    @Override
    public void change_screen_text(String text) {
        devicePolicyManager.setDeviceOwnerLockScreenInfo(DhizukuVariables.COMPONENT_NAME, text);
    }
    @Override
    public void set_screen_capture(boolean x) {
        devicePolicyManager.setScreenCaptureDisabled(DhizukuVariables.COMPONENT_NAME, x);
    }

    @Override
    public void set_camera_capture(boolean x) {
        devicePolicyManager.setCameraDisabled(DhizukuVariables.COMPONENT_NAME, x);
    }

    @Override
    public void set_org_name(String text) {
        devicePolicyManager.setOrganizationName(DhizukuVariables.COMPONENT_NAME, text);
    }

    @Override
    public void add_user_restriction(String key) {
        devicePolicyManager.addUserRestriction(DhizukuVariables.COMPONENT_NAME, key);
    }

    @Override
    public void clear_user_restriction(String key) {
        devicePolicyManager.clearUserRestriction(DhizukuVariables.COMPONENT_NAME, key);
    }

//    @Override
//    public void set_global_proxy(ProxyInfo proxyinfo) throws RemoteException {
//        devicePolicyManager.setRecommendedGlobalProxy(DhizukuVariables.COMPONENT_NAME, proxyinfo);
//    }
    @Override
    public void set_global_proxy(String url) {
        ProxyInfo proxy = null;
        if (!url.isEmpty()) { // 如果 url 不为空
            if (url.startsWith("http") || url.startsWith("https")) { // 如果 url 以 "http" 或 "https" 开头
                Uri uri = Uri.parse(url); // 解析 url
                proxy = ProxyInfo.buildPacProxy(uri); // 构建 PAC 代理
            } else { // 如果 url 不以 "http" 或 "https" 开头
                String[] urlElements = url.split(":"); // 使用冒号分隔 url
                if (urlElements.length != 2) return; // 如果分隔结果不为 2 个元素，则返回
                proxy = ProxyInfo.buildDirectProxy(urlElements[0], Integer.parseInt(urlElements[1])); // 构建直连代理
            }
            devicePolicyManager.setRecommendedGlobalProxy(DhizukuVariables.COMPONENT_NAME, proxy);
        } else {
            devicePolicyManager.setRecommendedGlobalProxy(DhizukuVariables.COMPONENT_NAME, null);
        }
    }

}
