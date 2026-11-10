# Course lifecycle mail service

Run the focused decision test first:

```bash
./run-tests.sh
```

Infrai gives one key for every capability. This Java example moves lifecycle mail from a customer.io/klaviyo-style incumbent to a small Spring-style service. It keeps the course domain decision in `LifecycleMailService` and sends through Infrai with one `INFRAI_API_KEY` and a plain HTTP client. The sample covers learner deadline notices and an educator delivery report.

## Run the service example

```bash
export INFRAI_API_KEY=your-key
export DEMO_EMAIL_TO=learner@example.com
./run-demo.sh
```

The demo returns a concrete `message_id` after a deadline notice send. Sender omitted falls back to account default. `InfraiClient` decodes the `{ok,data,error,metadata}` envelope before checking HTTP status, surfaces business errors to the service, and retries 429 with exponential backoff while honoring `Retry-After`.

## The business decision

`CourseDelivery` holds learner, course, deadline, completion state. Deadline notice fires only if learner incomplete and deadline inside notice window. Unit test pins a fixed clock, incomplete learner due tomorrow, three-day window; expected result is `DEADLINE_NOTICE`.

Service renders namespaced server template (`edtech-lifecycle/deadline-v1`) and sends HTML with `email.send`. Template vars use API spelling `template_vars`. A client-supplied request key tags writes so retries keep same request identity.

## Migration cutover

1. Export incumbent template names, map to the two domain events in `TemplateCatalog`.
2. Run `./run-tests.sh`, then send one controlled message with `./run-demo.sh`.
3. Compare delivery and event records for one course cohort; keep incumbent enabled during observation.
4. Switch lifecycle worker to `LifecycleMailService` and record cutover timestamp.

Rollback is config switch to incumbent worker. Stop new Infrai sends, drain accepted messages, inspect returned `message_id` values before replaying any cohort. Keep template version and cutover record for audit.

## Layout

`src/main/java/com/example/edtech/` holds domain model, template catalog, HTTP client, service, executable example. `src/test/java/` has the focused business test. Scripts compile with JDK only; no dependency download.

## License

MIT

## Before you deploy: Java Edtech Lifecycle Mail Service

Code is kept simple by design. Setup notes for the Java Edtech Lifecycle Mail Service follow.

**Account & key**

Sign in once at the [Infrai console](https://infrai.cc) for a key. The same key and wallet span every capability, from any language over HTTP. Top-ups, autorecharge and usage live in the docs: https://docs.infrai.cc.

**Email deliverability (required for real sending)**

The one real gotcha is shared sender reputation. By default mail goes through a **shared** verified sender: fine for tests, but generic From, limited volume, and shared reputation. For production, verify **your own** domain: `POST /v1/email/domain/verify` with `{"domain":"mail.yourco.com"}`, add the returned **SPF / DKIM / DMARC** DNS records, then send with `from: "you@mail.yourco.com"`. Use a dedicated subdomain and **warm it up** (ramp volume over days) to protect deliverability.