package social.gaming.blaash;

public class ChannelStatusJson {
    private final String CustomerId;
    private final String CustomerName;
    private final String ChannelArn;
    private final String Status;
    private final long ProductId;

    public ChannelStatusJson(String customerId, String customerName, String channelArn, String status, long productId) {
        CustomerId = customerId;
        CustomerName = customerName;
        ChannelArn = channelArn;
        Status = status;
        this.ProductId = productId;
    }
}
