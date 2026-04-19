# IP Geolocation with MaxMind GeoLite2

Opportune uses the **MaxMind GeoLite2-City** database to perform fully offline IP geolocation.
The country code, country name, and city are stored in the `Session` record whenever a user logs in.

---

## How It Works

1. `GeoIpConfiguration` loads `GeoLite2-City.mmdb` from the configured path **once at startup** and exposes it as a Spring bean (`DatabaseReader`).
2. `GeoLocationServiceImpl` wraps the reader: it resolves the client IP (already extracted from `X-Forwarded-For` / `remoteAddr`) and returns a `GeoLocationResult(countryCode, countryName, cityName)`.
3. `SymmetricJwtTokenServiceImpl` calls `geoLocationService.format(...)` and stores the result as `clientGeo` in the `SessionDTO` (e.g. `"US, United States, Mountain View"`).

---

## Setup

### 1. Create a MaxMind Account (free)

Go to <https://www.maxmind.com/en/geolite2/signup> and sign up for a free GeoLite2 account.

### 2. Download GeoLite2-City.mmdb

From your MaxMind account dashboard, download **GeoLite2-City** → **Binary / MaxMind DB format** (`.mmdb`).

### 3. Configure the Path

Set the environment variable (or override in your profile YAML):

```bash
OPPORTUNE_GEOIP_DB_PATH=/opt/geoip/GeoLite2-City.mmdb
```

Or in `application-dev.yml` / `application-prod.yml`:

```yaml
application:
  geoip:
    db-path: /opt/geoip/GeoLite2-City.mmdb
    enabled: true
```

The default path (when the variable is not set) is `./GeoLite2-City.mmdb` (relative to the working directory).

---

## Disabling Geolocation

Set `application.geoip.enabled: false` (or `OPPORTUNE_GEOIP_DB_PATH` to a non-existent file).
The service will silently skip all lookups and store `null` in `clientGeo`.
Tests use `application.geoip.enabled: false` by default.

---

## Keeping the Database Up to Date

MaxMind releases an updated GeoLite2-City database **every Tuesday**.
Replace the `.mmdb` file and **restart the application** — the reader is loaded at startup.

### Automated Update (optional)

MaxMind provides the [GeoIP Update](https://github.com/maxmind/geoipupdate) tool that can be run as a cron job:

```bash
# /etc/cron.weekly/geoipupdate
/usr/bin/geoipupdate
systemctl restart opportune   # or send SIGHUP if you implement live reload
```

Configure `/etc/GeoIP.conf`:
```
AccountID YOUR_ACCOUNT_ID
LicenseKey YOUR_LICENSE_KEY
EditionIDs GeoLite2-City
```

---

## Handling Edge Cases

| Situation | Behaviour |
|---|---|
| Loopback / private IP (`127.0.0.1`, `10.x.x.x`) | `lookup()` returns `null`; `clientGeo` is stored as `null` |
| IP behind VPN / proxy | Geo-lookup is best-effort on the outermost IP from `X-Forwarded-For` |
| Database file missing at startup | Warning logged; all lookups return `null` gracefully |
| `application.geoip.enabled=false` | Warning logged; all lookups return `null` gracefully |
| Corrupt or unreadable MMDB file | Error logged; all lookups return `null` gracefully |

