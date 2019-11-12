package dao.impl;

import bean.Page;
import bean.User;
import dao.UserDao;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import utils.DruidUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * @author GFS
 */
public class UserDaoImpl implements UserDao {

    private QueryRunner runner = new QueryRunner(DruidUtils.getDataSource());

    @Override
    public User getLoginUser(User user) {
        User loginUser = null;
        try {
            loginUser = runner.query("select * from t_user where username = ? and password = ?", new BeanHandler<>(User.class),
                    user.getUsername(), user.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loginUser;
    }

    @Override
    public User getUserByUsername(String username) {
        User user = null;
        try {
            user = runner.query("select * from t_user where username = ?", new BeanHandler<>(User.class), username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public int register(User user) {
        try {
            runner.update("insert into t_user(username, password, nickname, email, birthday, registTime, activationCode, activationStatus) values(?, ?, ?, ?, ?, ?, ?, ?)",
                    user.getUsername(), user.getPassword(), user.getNickname(), user.getEmail(), user.getBirthday(),
                    user.getRegistTime(), user.getActivationCode(), user.getActivationStatus());
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    @Override
    public User getUserByActivationCode(String activationCode) {
        User user = null;
        try {
            user = runner.query("select * from t_user where activationCode = ?", new BeanHandler<>(User.class), activationCode);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public int updateActivationStatus(User user) {
        try {
            runner.update("update t_user set activationStatus = ? where uid = ?", "Y", user.getUid());
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    @Override
    public List<User> listPageUsers(int currentPageNum) {
        List<User> userList = null;
        try {
            userList = runner.query("select * from t_user order by uid desc limit ? offset ?",
                    new BeanListHandler<>(User.class), Page.getPageSize(), (currentPageNum - 1) * Page.getPageSize()); // 偏移量
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    @Override
    public int countTotalUserCount() {
        Long totalCount = 0L; //Long型后加大L
        try {
            totalCount = (Long) runner.query("select count(uid) from t_user", new ScalarHandler());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalCount.intValue();
    }

    @Override
    public int updateUser(String sql) {
        try {
            runner.update(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }
}
