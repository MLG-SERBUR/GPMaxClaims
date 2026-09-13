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

## Config

```yaml
# config.yml
# Fallback when player has no griefprevention.maxclaims.<N> permission.
# -1 = use GriefPrevention's MaximumNumberOfClaimsPerPlayer (0 there = unlimited)
# 0 = unlimited, >0 = exact top-level claim count
default-max-claims: -1
```

Reload without restart: `/gpmaxclaims reload` (needs `gpmaxclaims.admin`).

## Usage with LuckPerms

Original request: `griefprevention.maxclaims.1` per rank.

```bash
# default players: 1 claim total
/lp group default permission set griefprevention.maxclaims.1 true

# vip: 5 claims
/lp group vip permission set griefprevention.maxclaims.5 true

# staff: unlimited
/lp group staff permission set griefprevention.maxclaims.* true
# or: griefprevention.maxclaims.0

# alternative bypass (also unlimited, matches GP config limit):
/lp group admin permission set griefprevention.overrideclaimcountlimit true
```

Notes:
- Highest `N` wins if a player has multiple `griefprevention.maxclaims.N`.
- If you use `-1` for `default-max-claims`, set `GriefPrevention.Claims.MaximumNumberOfClaimsPerPlayer` in GP's `config.yml` as your global fallback.
- Test with a non-OP account in the target group (`/lp user <player> parent set <group>`), then try to create claims with a golden shovel.
- Subdivisions and admin claims don't count toward the limit.
