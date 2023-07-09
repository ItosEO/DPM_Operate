package com.itos.dpm;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.net.ProxyInfo;
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

    @Override
    public void set_global_proxy(ProxyInfo proxyinfo) throws RemoteException {
        devicePolicyManager.setRecommendedGlobalProxy(DhizukuVariables.COMPONENT_NAME, proxyinfo);
    }

}
