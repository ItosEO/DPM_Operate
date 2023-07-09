package com.itos.dpm;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
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
    public void onCreate() throws RemoteException {
    }
    @Override
    public void onDestroy() throws RemoteException {

    }
    @Override
    public void change_screen_text(String text) throws RemoteException {
        devicePolicyManager.setDeviceOwnerLockScreenInfo(DhizukuVariables.COMPONENT_NAME, text);
    }
    @Override
    public void set_screen_capture(boolean x) throws RemoteException{
        devicePolicyManager.setScreenCaptureDisabled(DhizukuVariables.COMPONENT_NAME, x);
    }

    @Override
    public void set_camera_capture(boolean x) throws RemoteException {
        devicePolicyManager.setCameraDisabled(DhizukuVariables.COMPONENT_NAME, x);
    }

    @Override
    public void set_org_name(String text) throws RemoteException {
        devicePolicyManager.setOrganizationName(DhizukuVariables.COMPONENT_NAME, text);
    }

    @Override
    public void add_user_restriction(String key) throws RemoteException {
        devicePolicyManager.addUserRestriction(DhizukuVariables.COMPONENT_NAME, key);
    }

    @Override
    public void clear_user_restriction(String key) throws RemoteException {
        devicePolicyManager.clearUserRestriction(DhizukuVariables.COMPONENT_NAME, key);
    }

}
