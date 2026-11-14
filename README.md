# M Chat — Firebase starter
Arabic RTL dark mobile chat. Signup/login, start a direct conversation using registered email, Firestore realtime text messaging and chat list.
## Deploy
Upload contents to GitHub repository root → Settings → Pages → deploy main/root.
## Firebase
Project webchat-e4b06 Web config is embedded in index.html. Enable Authentication Email/Password, create Firestore, deploy firebase/firestore.rules, add GitHub Pages domain under Auth authorized domains.
## SECURITY
A service account private key was pasted into a chat. Revoke/delete that key immediately in Google Cloud IAM → Service Accounts → Keys. Do NOT commit service-account JSON/private keys. Web API key is not an Admin credential; secure with rules.
## Scope
Working base after Firebase setup: auth, user records, direct chats, realtime text messages. Not full WhatsApp yet: attachments, read receipts, typing, push notification server triggers, WebRTC calls, groups, E2E encryption and production protections require further development.
