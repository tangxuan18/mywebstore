package dao.impl;

import bean.Admin;
import dao.AdminDao;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import utils.DruidUtils;

import java.sql.SQLException;
import java.util.List;

public class AdminDaoImpl implements AdminDao {

    private QueryRunner runner = new QueryRunner(DruidUtils.getDataSource());

    @Override
    public Admin findLoginAdmin(Admin admin) {
        Admin currentAdmin = null;
        try {
            currentAdmin =  runner.query("select id, username, password from t_admin where username = ? and password = ?",
                    new BeanHandler<>(Admin.class), admin.getUsername(), admin.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return currentAdmin;
    }

    @Override
    public int insertAdmin(Admin admin) {
        try {
            runner.update("insert into t_admin values(null, ?, ?)", admin.getUsername(), admin.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    @Override
    public List<Admin> findAllAdmin() {
        List<Admin> list = null;
        try {
            list = runner.query("select id, username, password from t_admin", new BeanListHandler<>(Admin.class));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int deleteOneAdmin(String aid) {
        try {
            runner.update("delete from t_admin where id = ?", aid);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    @Override
    public int updateAdmin(Admin admin) {
        try {
            runner.update("update t_admin set username = ?, password = ? where id = ?", admin.getUsername(), admin.getPassword(), admin.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }
}
