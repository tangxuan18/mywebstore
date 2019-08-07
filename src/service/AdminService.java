package service;

import bean.Admin;

import java.util.List;

public interface AdminService {
    Admin findLoginAdmin(Admin admin);

    int addAdmin(Admin admin);

    List<Admin> findAllAdmin();

    int deleteOneAdmin(String aid);

    int updateAdmin(Admin admin);
}
