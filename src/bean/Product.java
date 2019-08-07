package bean;

public class Product {

    private int id;

    private int cid;

    /*如果没有cname属性，前端无法显示cname？？？*/
    private String cname;

    private String productNum;

    private int totalStockCount;

    private String productName;

    private double webStorePrice;

    private double marketPrice;

    private String imgUrl;

    private String description;

    /*用于searchProduct的属性，不妨碍其他属性的封装*/
    //难点 String 还是 double类型
    private String minWebStorePrice;
    private String maxWebStorePrice;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getProductNum() {
        return productNum;
    }

    public void setProductNum(String productNum) {
        this.productNum = productNum;
    }

    public int getTotalStockCount() {
        return totalStockCount;
    }

    public void setTotalStockCount(int totalStockCount) {
        this.totalStockCount = totalStockCount;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getWebStorePrice() {
        return webStorePrice;
    }

    public void setWebStorePrice(double webStorePrice) {
        this.webStorePrice = webStorePrice;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMinWebStorePrice() {
        return minWebStorePrice;
    }

    public void setMinWebStorePrice(String minWebStorePrice) {
        this.minWebStorePrice = minWebStorePrice;
    }

    public String getMaxWebStorePrice() {
        return maxWebStorePrice;
    }

    public void setMaxWebStorePrice(String maxWebStorePrice) {
        this.maxWebStorePrice = maxWebStorePrice;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", cid=" + cid +
                ", cname='" + cname + '\'' +
                ", productNum='" + productNum + '\'' +
                ", totalStockCount=" + totalStockCount +
                ", productName='" + productName + '\'' +
                ", webStorePrice=" + webStorePrice +
                ", marketPrice=" + marketPrice +
                ", imgUrl='" + imgUrl + '\'' +
                ", description='" + description + '\'' +
                ", minWebStorePrice=" + minWebStorePrice +
                ", maxWebStorePrice=" + maxWebStorePrice +
                '}';
    }
}
