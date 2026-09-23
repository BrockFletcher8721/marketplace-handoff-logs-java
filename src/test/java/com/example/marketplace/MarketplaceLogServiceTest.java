package com.example.marketplace;

import com.example.marketplace.client.InfraiLogsClient;
import com.example.marketplace.service.MarketplaceLogService;
import java.io.IOException;

public final class MarketplaceLogServiceTest {
    static final class RecordingClient extends InfraiLogsClient {
        String sent;
        RecordingClient() { super(new com.example.marketplace.config.InfraiConfig("http://local", "test")); }
        @Override public String ingest(String payload) { sent = payload; return "{\"ok\":true}"; }
    }
    public static void main(String[] args) throws Exception {
        RecordingClient client = new RecordingClient();
        new MarketplaceLogService(client).handoff("order-7", "asset-2", "ready");
        assert client.sent.contains("handoff-order-7");
        assert client.sent.contains("seller_asset");
        assert client.sent.startsWith("{\"entries\":[{");
        assert client.sent.endsWith("}]}");
        try { new MarketplaceLogService(client).handoff("", "asset-2", "ready"); throw new AssertionError(); }
        catch (IllegalArgumentException expected) { }
        System.out.println("handoff decision test passed");
    }
}
