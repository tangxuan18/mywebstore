package service.impl;

import bean.Admin;
import dao.AdminDao;
import dao.impl.AdminDaoImpl;
import service.AdminService;

import java.util.List;

public class AdminServiceImpl implements AdminService {

    private AdminDao adminDao = new AdminDaoImpl();

    @Override
    public Admin findLoginAdmin(Admin admin) {
        return adminDao.findLoginAdmin(admin);
    }

    @Override
    public int addAdmin(Admin admin) {
        return adminDao.insertAdmin(admin);
    }

    @Override
    public List<Admin> findAllAdmin() {
        return adminDao.findAllAdmin();
    }

    @Override
    public int deleteOneAdmin(String aid) {
        return adminDao.deleteOneAdmin(aid);
    }

    @Override
    public int updateAdmin(Admin admin) {
        return adminDao.updateAdmin(admin);
    }
}
