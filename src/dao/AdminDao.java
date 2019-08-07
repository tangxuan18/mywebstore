package dao;

import bean.Admin;

import java.util.List;

public interface AdminDao {
    Admin findLoginAdmin(Admin admin);

    int insertAdmin(Admin admin);

    List<Admin> findAllAdmin();

    int deleteOneAdmin(String aid);

    int updateAdmin(Admin admin);
}
