package service;

import bean.Cart;

public interface CartService {
    int saveCartItem(int uid, int pid, int productCount);

    int updateOneProductCount(int cartItemId, String addOrDelete);

    Cart getCart(int uid);
}
