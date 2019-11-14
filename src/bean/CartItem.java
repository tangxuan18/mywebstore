package bean;

/**
 * 表中有cartItem表，保存id uid pid productCount
 */
public class CartItem {

    private int cartItemId;

    private int uid;

    /**
     * 商品对象
     */
    private Product product;

    /**
     * 商品数量
     */
    private int productCount;

//    private Map<Product, Integer> cartItem;

    public int getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(int cartItemId) {
        this.cartItemId = cartItemId;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "cartItemId=" + cartItemId +
                ", uid=" + uid +
                ", product=" + product +
                ", productCount=" + productCount +
                '}';
    }
}
