package dao;

import bean.Cart;
import bean.CartItem;

import java.util.List;

public interface CartDao {
    int countCartItemCount(int uid, int pid);

    int insertCartItem(int uid, int pid, int productCount);

    int insertOneProductCount(int cartItemId);

    int updateCartItem(int uid, int pid, int productCount);

    Cart getCart(int uid);


    int deleteOneProductCount(int cartItemId);

    int getProductCount(int cartItemId);

    int deleteCartItem(int cartItemId);

    List<CartItem> listSelectedCartItems(String selectedCartItemIdArray);
}
