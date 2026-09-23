package com.example.marketplace.service;

import com.example.marketplace.client.InfraiLogsClient;
import java.io.IOException;

public final class MarketplaceLogService {
    private final InfraiLogsClient logs;
    public MarketplaceLogService(InfraiLogsClient logs) { this.logs = logs; }

    public String handoff(String orderId, String sellerAsset, String buyerUpdate)
            throws IOException, InterruptedException {
        if (orderId.isBlank() || sellerAsset.isBlank() || buyerUpdate.isBlank())
            throw new IllegalArgumentException("orderId, sellerAsset, and buyerUpdate are required");
        String eventId = "handoff-" + orderId;
        String json = "{\"entries\":[{\"event_id\":\"" + eventId + "\",\"level\":\"info\","
                + "\"message\":\"order handoff\",\"order_id\":\"" + orderId
                + "\",\"seller_asset\":\"" + sellerAsset + "\",\"buyer_update\":\""
                + buyerUpdate + "\"}]}";
        return logs.ingest(json);
    }

    public String findOrder(String orderId) throws IOException, InterruptedException {
        return logs.search(orderId);
    }
}
