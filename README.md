# M Chat — GitHub Pages + Firebase

Arabic RTL private messaging prototype with Firebase Authentication, Firestore live chats, voice-note upload (Firebase Storage), and WebRTC audio-call signaling.

## Setup
1. Firebase Console > Authentication > Sign-in method > enable Email/Password.
2. Create Firestore Database and publish `firebase/firestore.rules`.
3. Firebase Storage > Get started > publish `firebase/storage.rules`.
4. Authentication > Settings > Authorized domains: add your GitHub Pages domain (`USERNAME.github.io`).
5. Upload the contents of this folder to repository `m-chat` (root), enable GitHub Pages on `main` / root.
6. Open the HTTPS site on two devices/accounts, create accounts, start a chat using the other account's email, then call.

## Important
- Never place Firebase Admin/service-account private keys in this client project or GitHub. Revoke any exposed service-account key in Google Cloud IAM immediately.
- WebRTC uses public STUN only. Some carrier/NAT networks require a TURN server for reliable audio calls; configure TURN credentials via a secure backend (never publish credentials).
- Web Push needs Firebase Cloud Messaging setup and a trusted server/Cloud Function to send notifications. The included service worker only handles incoming push events; it does not send push by itself.
- This is a feature-rich starter, not a production WhatsApp clone. Add App Check, stricter rules, rate limits, reporting/blocking, encryption strategy, and privacy policy before public launch.
