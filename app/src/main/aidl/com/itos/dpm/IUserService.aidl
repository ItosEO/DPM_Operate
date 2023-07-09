package com.itos.dpm;

interface IUserService {
    void onCreate() = 1;
    void onDestroy() = 2;
    void change_screen_text(String text) = 21;
    void set_screen_capture(boolean x) = 22;
    void set_camera_capture(boolean x) = 23;
    void set_org_name(String text) = 24;
    void add_user_restriction(String key) = 25;
    void clear_user_restriction(String key) = 26;
    void set_global_proxy(in ProxyInfo proxyinfo) = 27;
    // 20以内的transact code是保留给未来的Dhizuku APi使用的。
}
