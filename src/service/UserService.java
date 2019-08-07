package service;

import bean.Page;
import bean.User;

public interface UserService {
    User getLoginUser(User user);

    User getUserByUsername(String username);

    int register(User user);

    User getUserByActivationCode(String activationCode);

    int updateActivationStatus(User user);

    Page listPageUsers(int currentPageNum);

    int updateUser(User user);
}
