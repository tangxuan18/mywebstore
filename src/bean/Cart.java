package bean;

import java.util.List;

/**
 * 表中并没有cart表，只是对CartItem的封装
 */
public class Cart {
    /**
     * 包含多个cartItem
     */
    private List<CartItem> cartItems;

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "cartItems=" + cartItems +
                '}';
    }
}
