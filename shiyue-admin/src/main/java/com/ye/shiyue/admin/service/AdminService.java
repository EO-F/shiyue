package com.ye.shiyue.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ye.shiyue.admin.pojo.Admin;

public interface AdminService extends IService<Admin> {
    Admin login(Admin admin);

    boolean register(Admin admin);
}
