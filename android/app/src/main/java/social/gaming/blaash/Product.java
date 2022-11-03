package social.gaming.blaash;

public class Product {
    private String productSKU;
    private String productdisplayName;
    private String productShortName;
    private String productURL;
    private String baseImageURL;
    private long productPrice;
    private String formattedProductPrice;
    private String productCategoryNames;
    private String productCategory;
    private String portalProductID;
    private long productId;

    public long getProductId() {
        return productId;
    }

    public String getProductSKU() {
        return productSKU;
    }

    public String getProductdisplayName() {
        return productdisplayName;
    }

    public String getProductShortName() {
        return productShortName;
    }

    public String getProductURL() {
        return productURL;
    }

    public String getBaseImageURL() {
        return baseImageURL;
    }

    public long getProductPrice() {
        return productPrice;
    }

    public String getFormattedProductPrice() {
        return formattedProductPrice;
    }

    public String getProductCategoryNames() {
        return productCategoryNames;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public String getPortalProductID() {
        return portalProductID;
    }
}
