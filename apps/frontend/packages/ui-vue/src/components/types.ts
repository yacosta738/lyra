import { SortType } from '@lyra/vm-core';

export type ExtractComponentProps<TComponent> = TComponent extends new () => {
	$props: infer P;
}
	? P
	: never;

export interface ColumnInfo {
	key: string;
	label: string;
	sortable?: boolean;
}

export type SortedEvent = { sortKey: string; sortType: SortType };
