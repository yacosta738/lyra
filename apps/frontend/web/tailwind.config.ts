// tailwind config is required for editor support
import type { Config } from 'tailwindcss';

import sharedConfig from '../packages/css-config/tailwind.config.mts';

const config: Pick<Config, 'presets'> = {
	presets: [sharedConfig],
};

export default config;
