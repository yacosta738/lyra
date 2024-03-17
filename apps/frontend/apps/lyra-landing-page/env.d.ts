/// <reference path="../.astro/types.d.ts" />
/// <reference types="astro/client" />

interface ImportMetaEnv {
	readonly BACKEND_URL: string;
	readonly BACKEND_API: string;
	readonly APP_CLIENT_URL: string;
}

// eslint-disable-next-line no-unused-vars
interface ImportMeta {
	readonly env: ImportMetaEnv;
}
