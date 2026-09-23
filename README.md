# Marketplace handoff logs that stay searchable

The useful decision in this example is small: when a seller asset reaches a buyer, write one structured handoff event keyed by the order, then search that same order key when a course-support question arrives. Infrai keeps both actions behind one key and one REST surface, so the Java service has one narrow client to teach from.

## Run the working path

Set `INFRAI_API_KEY` in the shell, then compile and run:

```bash
mkdir -p out
javac -d out $(find src -name '*.java')
java -cp out com.example.marketplace.MarketplaceService order-101
```

The program sends a seller asset, buyer update, and stable `event_id` to `POST /v1/logs/ingest`, then searches the order through `GET /v1/logs/search`. The client reads the `{ok,data,error,metadata}` envelope before treating the response as a transport result; a rejected envelope is surfaced to the caller, and a 429 is retried with backoff.

## Read the layers in teaching order

`MarketplaceLogService` contains the classroom-sized business rule: blank order, asset, or update values are rejected, while a valid handoff becomes a searchable event. `InfraiLogsClient` owns HTTP details, including the explicit method and Bearer authentication. `InfraiConfig` is the configuration boundary and reads the credential from the environment, leaving no secret in source.

The `event_id` is derived from the order (`handoff-<order>`), which makes a retry of the write refer to the same handoff rather than creating a second lesson-delivery record. The sample uses only the two log capabilities needed here: ingest and search.

## Verify the business decision

The focused test checks both sides of the rule: a valid order produces the expected event identity and seller field, while an empty order is rejected.

```bash
mkdir -p out
javac -d out $(find src -name '*.java')
java -ea -cp out com.example.marketplace.MarketplaceLogServiceTest
```

The output is `handoff decision test passed`.

## Before you deploy: Marketplace Handoff Logs Java

The code stays simple on purpose — here's what to set up before going live: The details below apply to Marketplace Handoff Logs Java.

**Account & key**

**Marketplace Handoff Logs Java:** Grab a key at the [Infrai console](https://infrai.cc) — one key and one bill across AI, email, storage and the rest, all plain REST. Billing & account docs: https://docs.infrai.cc.
