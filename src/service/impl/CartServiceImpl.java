package service.impl;

import bean.Cart;
import dao.CartDao;
import dao.impl.CartDaoImpl;
import service.CartService;

public class CartServiceImpl implements CartService {

    private CartDao cartDao = new CartDaoImpl();

    @Override
    public int saveCartItem(int uid, int pid, int productCount) {
        int cartItemCount = cartDao.countCartItemCount(uid, pid);
        if(cartItemCount == 0) {
            return cartDao.insertCartItem(uid, pid, productCount);
        }else {
            return cartDao.updateCartItem(uid, pid, productCount);
        }
    }

    @Override
    public int updateOneProductCount(int cartItemId, String addOrDelete) {
        if("add".equals(addOrDelete)) {
            return cartDao.insertOneProductCount(cartItemId);
        }else if("delete".equals(addOrDelete)){
            int productCount = cartDao.getProductCount(cartItemId);
            if (productCount == 1) {
                return cartDao.deleteCartItem(cartItemId);
            }
            return cartDao.deleteOneProductCount(cartItemId);
        }
        return 0;
    }

    @Override
    public Cart getCart(int uid) {
        return cartDao.getCart(uid);
    }
}
