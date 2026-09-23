package com.example.marketplace;

import com.example.marketplace.client.InfraiLogsClient;
import com.example.marketplace.config.InfraiConfig;
import com.example.marketplace.service.MarketplaceLogService;

public final class MarketplaceService {
    public static void main(String[] args) throws Exception {
        MarketplaceLogService service = new MarketplaceLogService(
                new InfraiLogsClient(InfraiConfig.fromEnvironment()));
        String order = args.length > 0 ? args[0] : "order-101";
        System.out.println(service.handoff(order, "course-pack-7", "buyer received the lesson files"));
        System.out.println(service.findOrder(order));
    }
}
