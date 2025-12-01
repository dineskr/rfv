# Personal Activity Logger (Android) – Design Overview

## Goals
- Automatically log call start/end times as calendar events synced to a self-hosted CalDAV server.
- Track foreground app usage intervals (screen time) in the background.
- Allow manual log creation with editable start/end, notes, and map-selected location using an open-source map provider.
- Provide notifications and widgets for active timers/quick actions.
- Target Android 8 (API 26) and above, requesting necessary permissions for call, usage access, and location.

## Core Features
### Call Logging
- Listen for incoming/outgoing call state changes via `TelephonyManager` + `PhoneStateListener` (API 26+) or `TelecomManager` callbacks where available.
- Capture call start/end timestamps and normalize to UTC.
- Create a calendar event per call with title template `Call with <number|contact>` and duration.
- Calendar writes use Android Calendar Provider, then synced to self-hosted CalDAV via selected account/calendar ID.
- Provide opt-in toggle for automatic call logging and a whitelist/blacklist for numbers (future enhancement).

### Foreground App Usage (Screen Time)
- Request Usage Access to read `UsageStatsManager` events.
- Background service polls usage events (e.g., every 15–30s) to detect app open/close intervals and aggregates into sessions.
- Store sessions locally; offer per-app toggles (log all by default) and daily summaries.
- Provide quick pause/resume from notification.

### Manual Logs
- Form with fields: title (optional), notes, start datetime, end datetime, location (map point), duration auto-calculated.
- Default start/end to `now()` with ability to edit.
- Location picked by tapping/long-pressing an open-source map (MapLibre/OSMDroid) with OSM tiles; reverse geocode via Nominatim-compatible service when online.
- Save as calendar events and local entries; sync via CalDAV.

### Notifications & Widgets
- Foreground service notification when tracking calls/usage to keep process alive (Android 8+ requirement).
- Ongoing timer notification for manual/active sessions with actions: Pause/Resume, Stop, Add Note.
- Home-screen widgets: quick-start manual timer, add note, pause/resume app tracking, and today summary.

## Data Model
- `Event`: id, type (`call`, `screen_time`, `manual`), title, notes, start_ts, end_ts, duration, app_package (optional), phone_number (optional), location_lat/lng (optional), location_name (optional), calendar_event_id, sync_state.
- Local storage via Room database; indices on `type`, `start_ts`, `app_package`.
- `SyncState`: pending, in_progress, synced, error (with last_error).

## Sync Strategy (CalDAV)
- In-app CalDAV credentials UI (server base URL, username, app password/token) with validation ping; credentials stored via EncryptedSharedPreferences/Keystore, never logged.
- Onboarding includes CalDAV login screen; allow re-authentication and logout in Settings.
- After login, fetch calendars and let user pick target calendar ID.
- For each event, create/update corresponding Calendar Provider event with UID-based sync key to prevent duplicates.
- Use `SyncAdapter` or WorkManager periodic task to push local changes to CalDAV (respecting backoff when offline).
- Conflict resolution: last-write-wins with manual override; maintain `updated_at` timestamp; surface auth errors in notification/Settings and prompt re-login.

## Permissions
- `READ_PHONE_STATE`, `READ_CALL_LOG`, `READ/WRITE_CALENDAR` (call logging & calendar events).
- `PACKAGE_USAGE_STATS` (screen time), requested via Settings intent.
- `ACCESS_FINE_LOCATION`/`COARSE` for map and reverse geocoding (optional but recommended).
- Foreground service permission (Android 9+) for persistent notification.

## Architecture
- **Layers**: UI (Jetpack Compose/Fragments), ViewModel (state), Use Cases, Repository (Room + Calendar Provider + CalDAV sync), Data sources (Call listener, Usage stats, Location/Map).
- **Background work**: WorkManager for periodic sync and screen-time aggregation; Foreground service for active timers.
- **Dependency Injection**: Hilt for component scoping.
- **Testing**: Unit tests for repositories/use cases; instrumentation tests for calendar writes and usage events where possible.

## UX Outline
1. **Onboarding**: Request permissions (call, calendar, usage access, location), sign in to CalDAV with server URL + credentials, pick CalDAV calendar, enable services.
2. **Home**: Today timeline combining calls, app sessions, and manual logs; filters and summaries.
3. **Manual Entry**: Start/stop timer or set custom times; map picker modal for location.
4. **History**: Calendar/list view with search by app/number/tags.
5. **Settings**: CalDAV calendar selection, call logging toggle, per-app tracking, notification/widget preferences, data export (CSV/ICS) as stretch goal.

## Offline & Privacy
- Operate fully offline; queue sync until network available.
- No third-party analytics; logs stored locally and to user’s CalDAV only.
- Allow data export/delete; clear call logs/app sessions on demand.

## Roadmap (MVP -> Next)
1. MVP: call logging to calendar, usage tracking, manual logs with map picker, CalDAV sync, foreground notification.
2. Next: widgets, per-app filters, CSV/ICS export, reverse geocoding cache, number whitelist/blacklist, offline map tiles.

## Open Questions / Assumptions
- Reverse geocoding service availability (e.g., self-hosted Nominatim) assumed.
- Widget design TBD based on launcher constraints.
