export default {
	overrides: [
		{
			files: ['**/*.scss'],
			customSyntax: 'postcss-scss',
		},
	],
	extends: ['stylelint-config-standard'],
	rules: {
		'at-rule-no-unknown': [
			true,
			{
				ignoreAtRules: ['tailwind', 'apply', 'variants', 'responsive', 'screen'],
			},
		],
	},
};
