# GPMaxClaims

Per-rank total top-level claim counts for GriefPrevention legacy v16.

Give ranks `griefprevention.maxclaims.<N>`, for example `griefprevention.maxclaims.1`.
`0` and `griefprevention.maxclaims.*` mean unlimited.
Highest numeric permission wins.
`griefprevention.overrideclaimcountlimit` still bypasses all limits.

No permission: uses `default-max-claims` in `config.yml`.
Keep it `-1` to fall back to GriefPrevention's `MaximumNumberOfClaimsPerPlayer`.
Only top-level claims count. Subdivisions and admin claims are ignored.

Requires GriefPrevention 16.18+ (legacy/v16 branch).
