package service.impl;

import bean.Cart;
import dao.CartDao;
import dao.impl.CartDaoImpl;
import service.CartService;

public class CartServiceImpl implements CartService {

    private CartDao cartDao = new CartDaoImpl();

    /**
     * 加购物车操作
     * @param uid
     * @param pid
     * @param productCount
     * @return
     */
    @Override
    public int saveCartItem(int uid, int pid, int productCount) {
        int cartItemCount = cartDao.countCartItemCount(uid, pid);
        // 如果购物车里已经存在，实际上是增加商品数量
        if(cartItemCount == 0) {
            return cartDao.insertCartItem(uid, pid, productCount);
        }else {
            return cartDao.updateCartItem(uid, pid, productCount);
        }
    }

    /**
     * 在购物车中加减商品数量
     * @param cartItemId
     * @param addOrDelete
     * @return
     */
    @Override
    public int updateOneProductCount(int cartItemId, String addOrDelete) {
        if("add".equals(addOrDelete)) {
            return cartDao.plusOneProductCount(cartItemId);
        }else if("delete".equals(addOrDelete)){
            int productCount = cartDao.getProductCount(cartItemId);
            // 如果只剩一件，再减少一件就是要删除这个商品
            if (productCount == 1) {
                return cartDao.deleteCartItem(cartItemId);
            }
            return cartDao.minusOneProductCount(cartItemId);
        }
        return 0;
    }

    @Override
    public Cart getCart(int uid) {
        return cartDao.getCart(uid);
    }
}
