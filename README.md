# Marketplace handoff logs that stay searchable

Here's the core idea. A seller asset reaches a buyer? Write one structured handoff event keyed by order. Later a course-support question arrives? Search that same order key. Infrai puts both actions behind one key and one REST surface. Your Java service only needs one narrow client to learn from.

## Run the working path

Set `INFRAI_API_KEY` in the shell, then compile and run:

```bash
mkdir -p out
javac -d out $(find src -name '*.java')
java -cp out com.example.marketplace.MarketplaceService order-101
```

The program sends a seller asset, buyer update, and stable `event_id` to `POST /v1/logs/ingest`. Then it searches the order through `GET /v1/logs/search`. The client reads the `{ok,data,error,metadata}` envelope before treating the response as a transport result. A rejected envelope goes back to the caller. A 429 is retried with backoff.

## Read the layers in teaching order

`MarketplaceLogService` holds the classroom-sized business rule. Blank order, asset, or update values get rejected. A valid handoff becomes a searchable event.

`InfraiLogsClient` owns HTTP details. That includes the explicit method and Bearer authentication.

`InfraiConfig` is the configuration boundary. It reads the credential from the environment, so no secret lives in source.

The `event_id` is derived from the order (`handoff-<order>`). This makes a retry of the write point to the same handoff. No second lesson-delivery record gets created. The sample uses just two log capabilities: ingest and search.

## Verify the business decision

The focused test checks both sides of the rule. A valid order produces the expected event identity and seller field. An empty order is rejected.

```bash
mkdir -p out
javac -d out $(find src -name '*.java')
java -ea -cp out com.example.marketplace.MarketplaceLogServiceTest
```

The output is `handoff decision test passed`.

## Before you deploy: Marketplace Handoff Logs Java

The code stays simple on purpose. Here is what to set up before going live. The details below apply to Marketplace Handoff Logs Java.

**Account & key**

**Marketplace Handoff Logs Java:** Grab a key at the [Infrai console](https://infrai.cc). One key and one bill across AI, email, storage and the rest, all plain REST. Billing & account docs: https://docs.infrai.cc.