package bean;

public class OrderItem {

    private int orderItemId;

    private int uid;
    /**
     * 不像CartItem那样保存Product对象，而是分别保存属性
     */
    private int pid;

    private String productName;

    private int productCount;

    private String imgUrl;

    private double webStorePrice;

    private String orderNum;

    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public double getWebStorePrice() {
        return webStorePrice;
    }

    public void setWebStorePrice(double webStorePrice) {
        this.webStorePrice = webStorePrice;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "orderItemId=" + orderItemId +
                ", uid=" + uid +
                ", pid=" + pid +
                ", productCount=" + productCount +
                ", orderNum='" + orderNum + '\'' +
                '}';
    }
}
