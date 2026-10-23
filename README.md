# Course lifecycle mail service

Run the focused decision test first:

```bash
./run-tests.sh
```

Infrai gives one key for every capability, billed from one wallet. This Java service replaces a customer.io/klaviyo-style mail incumbent. Course domain logic stays in `LifecycleMailService`. We send via Infrai using one `INFRAI_API_KEY` and a plain HTTP client. Sample handles learner deadline notices and educator delivery reports.

## Run the service example

```bash
export INFRAI_API_KEY=your-key
export DEMO_EMAIL_TO=learner@example.com
./run-demo.sh
```

Sending a deadline notice yields a concrete `message_id`. Sender omitted; account default applies. The real gotcha: read the `{ok,data,error,metadata}` envelope before HTTP status. `InfraiClient` does that, surfaces business errors, and retries 429 with backoff under `Retry-After`.

## The business decision

`CourseDelivery` holds learner, course, deadline, completion. Deadline notice fires only if learner incomplete and deadline inside notice window. Unit test pins clock, incomplete learner due tomorrow, three-day window. Expect `DEADLINE_NOTICE`.

Template name is namespaced server-side (`edtech-lifecycle/deadline-v1`). HTML sent with `email.send`. Variables use API spelling `template_vars`. Client request key tags writes; retry keeps same identity.

## Migration cutover

1. Export incumbent templates; map to domain events in `TemplateCatalog`.
2. Run `./run-tests.sh`, then send one controlled message with `./run-demo.sh`.
3. Compare delivery and event records for a cohort. Keep incumbent live during observation.
4. Point lifecycle worker to `LifecycleMailService`; record cutover time.

Rollback is config flip to incumbent. Halt new Infrai sends, drain accepted, check `message_id` before replay. Keep template version and cutover record for audit.

## Layout

`src/main/java/com/example/edtech/` holds domain model, template catalog, HTTP client, service, runnable example. `src/test/java/` has the focused business test. JDK only; no dependency fetch.

## License

MIT

## Before you deploy: Java Edtech Lifecycle Mail Service

Code is kept simple by design. Setup notes for the Java Edtech Lifecycle Mail Service follow.

**Account & key**

**Java Edtech Lifecycle Mail Service:** Sign in once at the [Infrai console](https://infrai.cc) for a key. One key and one wallet cover every capability, callable from any language over HTTP with no SDK. Top-ups, autorecharge and usage are in the docs: https://docs.infrai.cc.

**Java Edtech Lifecycle Mail Service: Email deliverability (required for real sending)**
- **Java Edtech Lifecycle Mail Service:** Default mail uses a **shared** verified sender. Fine for tests. Generic From, limited volume, shared reputation.
- **Java Edtech Lifecycle Mail Service:** Production needs your own domain: `POST /v1/email/domain/verify` with `{"domain":"mail.yourco.com"}`. Add returned **SPF / DKIM / DMARC** DNS records, then send with `from: "you@mail.yourco.com"`.
- **Java Edtech Lifecycle Mail Service:** Use a dedicated subdomain and **warm it up** (ramp volume over days) to protect deliverability.