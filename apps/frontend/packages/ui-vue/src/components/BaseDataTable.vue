<script setup lang="ts" generic="T extends ItemProps">
import { computed, ref } from 'vue';
import type { ColumnInfo, SortedEvent } from './types';
import { SortType } from '@lyra/vm-core';

export type BaseProps = {
	id: string | number;
};

export type ItemProps = Record<string, string | number | Date | null | undefined> & BaseProps;

export interface BaseDataTableProps<T> {
	columns: ColumnInfo[];
	items: T[];
}

const props = withDefaults(defineProps<BaseDataTableProps<T>>(), {
	columns: () => [],
	items: () => [],
});

const emit = defineEmits<{
	(evt: 'sorted', val: SortedEvent): void;
}>();

// define and export slots
defineSlots<
	{
		[K in keyof T as `cell:${K & string}`]: (props: { value: T[K] }) => T[K];
	} & {
		top?: () => unknown;
		header?: () => unknown;
		loader?: () => unknown;
		'no-data'?: () => unknown;
		footer?: () => unknown;
	}
>();
const defaultSortKey = props.columns.find((column) => column.sortable)?.key ?? '';
const sortKey = ref(defaultSortKey);
const sortDesc = ref(false);

function sortProperties(
	aValue: string | number | Date | null | undefined,
	bValue: string | number | Date | null | undefined
) {
	// Check if values are numbers
	if (!isNaN(Number(aValue)) && !isNaN(Number(bValue))) {
		return sortDesc.value ? Number(bValue) - Number(aValue) : Number(aValue) - Number(bValue);
	}

	// Check if values are dates
	if (Date.parse(aValue as string) && Date.parse(bValue as string)) {
		return sortDesc.value
			? new Date(bValue as string).getTime() - new Date(aValue as string).getTime()
			: new Date(aValue as string).getTime() - new Date(bValue as string).getTime();
	}

	// Default to string comparison
	if ((aValue as string) < (bValue as string)) {
		return sortDesc.value ? 1 : -1;
	}
	if ((aValue as string) > (bValue as string)) {
		return sortDesc.value ? -1 : 1;
	}
	return 0;
}

const sortItems = computed(() => {
	if (sortKey.value) {
		const sortedItems = [...props.items];
		sortedItems.sort((a: T, b: T) => {
			const aValue = a[sortKey.value];
			const bValue = b[sortKey.value];
			return sortProperties(aValue, bValue);
		});
		return sortedItems;
	}
	return props.items;
});

const onColumnHeaderClick = (column: ColumnInfo) => {
	if (column.sortable) {
		sortKey.value = column.key;
		sortDesc.value = !sortDesc.value;
		const sortedEvent: SortedEvent = {
			sortKey: sortKey.value,
			sortType: sortDesc.value ? SortType.DESC : SortType.ASC,
		};
		emit('sorted', sortedEvent);
	}
};
</script>

<template>
	<div
		class="border-tertiary-200 dark:border-tertiary-700 bg-tertiary-100 dark:bg-tertiary-900 block items-center justify-between rounded border-b p-4 sm:flex lg:mt-1.5"
	>
		<div class="mb-1 w-full">
			<slot name="top" />
		</div>
	</div>
	<div class="flex h-full flex-col">
		<div class="h-full overflow-x-auto">
			<div class="inline-block h-full min-w-full align-middle">
				<div class="relative block h-full items-center overflow-hidden shadow-md">
					<slot name="header" />
					<table
						v-if="items && items.length > 0"
						class="divide-tertiary-200 dark:divide-tertiary-600 h-full min-h-max w-full min-w-full table-fixed divide-y"
					>
						<thead class="bg-tertiary-100 dark:bg-tertiary-900">
							<tr>
								<th
									v-for="column in columns"
									:key="column.key"
									scope="col"
									class="text-tertiary-500 dark:text-tertiary-400 p-4 text-left text-xs font-medium uppercase"
								>
									<div class="flex items-center">
										{{ column.label }}
										<svg
											v-if="column.sortable"
											xmlns="http://www.w3.org/2000/svg"
											:class="{
												'border-primary-500/50 text-primary-500 dark:border-primary-300/50 dark:text-primary-300 rounded border':
													sortKey === column.key,
											}"
											class="ml-1 h-6 w-6 cursor-pointer p-0.5"
											fill="currentColor"
											viewBox="0 0 256 256"
											@click="onColumnHeaderClick(column)"
										>
											<path
												v-if="sortDesc"
												fill="currentColor"
												d="M42 128a6 6 0 0 1 6-6h72a6 6 0 0 1 0 12H48a6 6 0 0 1-6-6m6-58h56a6 6 0 0 0 0-12H48a6 6 0 0 0 0 12m136 116H48a6 6 0 0 0 0 12h136a6 6 0 0 0 0-12m44.24-102.24l-40-40a6 6 0 0 0-8.48 0l-40 40a6 6 0 0 0 8.48 8.48L178 62.49V144a6 6 0 0 0 12 0V62.49l29.76 29.75a6 6 0 0 0 8.48-8.48"
											/>
											<path
												v-else
												fill="currentColor"
												d="M126 128a6 6 0 0 1-6 6H48a6 6 0 0 1 0-12h72a6 6 0 0 1 6 6M48 70h136a6 6 0 0 0 0-12H48a6 6 0 0 0 0 12m56 116H48a6 6 0 0 0 0 12h56a6 6 0 0 0 0-12m124.24-22.24a6 6 0 0 0-8.48 0L190 193.51V112a6 6 0 0 0-12 0v81.51l-29.76-29.75a6 6 0 0 0-8.48 8.48l40 40a6 6 0 0 0 8.48 0l40-40a6 6 0 0 0 0-8.48"
											/>
										</svg>
									</div>
								</th>
							</tr>
						</thead>

						<tbody
							class="divide-tertiary-200 dark:divide-tertiary-700 dark:bg-tertiary-800 h-full divide-y bg-white"
						>
							<tr
								v-for="item in sortItems"
								:key="item.id"
								class="hover:bg-tertiary-100 dark:hover:bg-tertiary-700"
							>
								<td
									v-for="column in columns"
									:key="column.key"
									class="text-tertiary-500 dark:text-tertiary-400 whitespace-nowrap p-4 text-sm font-normal"
								>
									<div class="text-tertiary-900 text-base font-semibold dark:text-white">
										<!--										<slot :name="column.key" :item="item">-->
										<!--											{{ item[column.key] }}-->
										<!--										</slot>-->
										<slot :name="`cell:${column.key}`" :value="item[column.key] as T[string]">
											{{ item[column.key] }}
										</slot>
									</div>
								</td>
							</tr>
						</tbody>
					</table>
					<template v-else>
						<slot name="no-data">
							<div class="flex h-full flex-col items-center justify-center py-12">
								<svg
									class="h-12 w-12"
									fill="currentColor"
									viewBox="0 0 256 256"
									xmlns="http://www.w3.org/2000/svg"
								>
									<path
										fill="currentColor"
										d="M128 26c-52.71 0-94 23.72-94 54v96c0 30.28 41.29 54 94 54s94-23.72 94-54V80c0-30.28-41.29-54-94-54m0 12c44.45 0 82 19.23 82 42s-37.55 42-82 42s-82-19.23-82-42s37.55-42 82-42m82 138c0 22.77-37.55 42-82 42s-82-19.23-82-42v-21.21C62 171.16 92.37 182 128 182s66-10.84 82-27.21Zm0-48c0 22.77-37.55 42-82 42s-82-19.23-82-42v-21.21C62 123.16 92.37 134 128 134s66-10.84 82-27.21Z"
									/>
								</svg>

								<p class="text-tertiary-500 dark:text-tertiary-400 mt-2 text-sm font-medium">
									No data available
								</p>
							</div>
						</slot>
					</template>
					<slot name="loader">
						<span class="sr-only">Loading...</span>
					</slot>
				</div>
			</div>
		</div>
	</div>
	<slot name="footer" />
</template>
