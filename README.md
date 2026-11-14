## Firebase security notes
Rules are a starting point, not production-complete. Tighten post update rules (likes should use a separate likes subcollection to prevent abuse), validate all fields and sizes, add App Check, rate limits, moderation, and server-side notification triggers before public launch.
