package social.gaming.blaash;

public class ProductDetailsToPost {
    private String Sku;
    private String PortalProductId;
    private String ProductName;
    private String URL;

    public String getSku() {
        return Sku;
    }

    public String getPortalProductId() {
        return PortalProductId;
    }

    public String getProductName() {
        return ProductName;
    }

    public String getURL() {
        return URL;
    }

    public void setSku(String sku) {
        Sku = sku;
    }

    public void setPortalProductId(String portalProductId) {
        PortalProductId = portalProductId;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
