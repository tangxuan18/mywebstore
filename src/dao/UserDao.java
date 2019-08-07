package dao;

import bean.User;

import java.util.List;

public interface UserDao {
    User getLoginUser(User user);

    User getUserByUsername(String username);

    int register(User user);

    User getUserByActivationCode(String activationCode);

    int updateActivationStatus(User user);

    List<User> listPageUsers(int currentPageNum);

    int countTotalUserCount();

    int updateUser(String sql);
}
