package dev.eduardo.minipix.api.util;

public final class Descriptions {

    private Descriptions() {}

    public static final String RESOLVE = """
            Pre-confirmation step for a Pix transfer.

            Resolves a Pix key and returns the receiver's account data for the payer to review \
            before confirming the transaction. Follows the **BACEN DICT specification** and is \
            required by regulation before any funds are transferred.

            ### What happens internally

            The following checks are executed **in parallel** using virtual threads:

            - **DICT** — resolves the Pix key to account, receiver, and institution data
            - **Anti-fraud** — checks whether the key or the sender is flagged
            - **Limits** — verifies whether the sender has sufficient limit for the requested amount

            ### Authentication

            The sender's document is extracted from the `Authorization` JWT bearer token (`sub` claim). \
            It is never sent directly in the request body.

            ### Error scenarios

            - Anti-fraud or limits returning a negative result will reject the request
            - Any external service failure results in `503 Service Unavailable`

            ### Required headers

            | Header | Description |
            |---|---|
            | `Authorization` | JWT bearer token — sender's document extracted from `sub` |
            | `correlation-id` | UUID for distributed tracing across all internal services |
            | `idempotency-key` | UUID to prevent duplicate processing on retries |
            | `device-id` | Device fingerprint used by the anti-fraud service |
            | `forwarded-for` | Original client IP, also consumed by anti-fraud |

            ### Test headers

            ```
            Authorization:    Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1Mjk5ODIyNDcyNSIsIm5hbWUiOiJFZHVhcmRvIEZvbnRlbmVsZSIsImlhdCI6MTUxNjIzOTAyMn0.ZEY0hTMxUHyjLhTet_sld-64t-mIET4brsfbDLu522o
            correlation-id:   f47ac10b-58cc-4372-a567-0e02b2c3d479
            idempotency-key:  3d3b5a1e-1c2d-4e5f-8a9b-0c1d2e3f4a5b
            device-id:        device-test-001
            forwarded-for:    189.120.45.32
            ```
            """;
}
