package service.impl;

import bean.Page;
import bean.User;
import dao.UserDao;
import dao.impl.UserDaoImpl;
import service.UserService;

public class UserServiceImpl implements UserService {

    private UserDao userDao = new UserDaoImpl();
    @Override
    public User getLoginUser(User user) {
        return userDao.getLoginUser(user);
    }

    @Override
    public User getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    @Override
    public int register(User user) {
        return userDao.register(user);
    }

    @Override
    public User getUserByActivationCode(String activationCode) {
        return userDao.getUserByActivationCode(activationCode);
    }

    @Override
    public int updateActivationStatus(User user) {
        return userDao.updateActivationStatus(user);
    }

    @Override
    public Page listPageUsers(int currentPageNum) {
        Page<User> page = new Page<User>();
        page.setList(userDao.listPageUsers(currentPageNum));
        page.setTotalRecordsNum(userDao.countTotalUserCount());
        page.setCurrentPageNum(currentPageNum);
        page.setTotalPageNum(((int) Math.ceil(page.getTotalRecordsNum() * 1.0 / Page.getPageSize())));
        page.setPrevPageNum(currentPageNum - 1);
        page.setNextPageNum(currentPageNum + 1);
        return page;
    }

    @Override
    public int updateUser(User user) {
        String sql = "update t_user set nickname = '" + user.getNickname() + "', `password` = '" + user.getPassword() +
                "', email = '" + user.getEmail() + "', birthday = '" + user.getBirthday() + "', headicon = '" + user.getHeadicon() +
                "', address = '" + user.getAddress() + "', mobilePhone = '" + user.getMobilePhone() + "', qq = '" + user.getQq() +
                "' where uid = " + user.getUid();
        return userDao.updateUser(sql);
    }

    @Override
    public int addUserBackground(User user) {
        return userDao.register(user);
    }
}
