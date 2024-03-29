<script lang="ts" setup>
import { computed, ref, toRef } from 'vue';
import { onClickOutside } from '@vueuse/core';
import type { DropdownPlacement } from './types';
import { useDropdownClasses } from './composables/useDropdownClasses';
import SlotListener from '@/components/slot-listener/SlotListener.vue';

const visible = defineModel({
	type: Boolean,
	default: false,
});

const props = withDefaults(
	defineProps<{
		placement?: DropdownPlacement;
		text?: string;
		transition?: string;
		closeInside?: boolean;
	}>(),
	{
		placement: 'bottom',
		text: '',
		transition: '',
		closeInside: false,
	}
);

const emit = defineEmits<{
	(evt: 'onHide'): void;
	(evt: 'onToggle'): void;
}>();

const onHide = () => {
	if (props.closeInside) visible.value = false;
	emit('onHide');
};
const onToggle = () => {
	visible.value = !visible.value;
	emit('onToggle');
};

const placementTransitionMap: Record<DropdownPlacement, string> = {
	bottom: 'to-bottom',
	left: 'to-left',
	right: 'to-right',
	top: 'to-top',
};

const transitionName = computed(() => {
	if (props.transition === null) return placementTransitionMap[props.placement];
	return props.transition;
});

const content = ref<HTMLDivElement>();
const wrapper = ref<HTMLDivElement>();

const { contentClasses, contentStyles } = useDropdownClasses({
	placement: toRef(props, 'placement'),
	visible,
	contentRef: content,
});

onClickOutside(wrapper, () => {
	if (!visible.value) return;
	visible.value = false;
	emit('onHide');
});
</script>

<template>
	<div ref="wrapper" class="relative inline-flex">
		<div class="inline-flex items-center">
			<SlotListener @click="onToggle">
				<slot name="trigger">
					<button
						class="inline-flex items-center rounded-lg bg-blue-700 px-5 py-2.5 text-center text-sm font-medium text-white hover:bg-blue-800 focus:outline-none focus:ring-4 focus:ring-blue-300 dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
						type="button"
					>
						{{ props.text }}
						<svg
							class="ml-2 h-4 w-4"
							fill="none"
							stroke="currentColor"
							viewBox="0 0 24 24"
							xmlns="http://www.w3.org/2000/svg"
						>
							<path
								d="M19 9l-7 7-7-7"
								stroke-linecap="round"
								stroke-linejoin="round"
								stroke-width="2"
							/>
						</svg>
					</button>
				</slot>
			</SlotListener>
		</div>
		<transition :name="transitionName">
			<div v-if="visible" ref="content" :class="[contentClasses]" :style="contentStyles">
				<SlotListener @click="onHide">
					<slot />
				</SlotListener>
			</div>
		</transition>
	</div>
</template>

<style scoped>
/* transitions */
.to-bottom-enter-active,
.to-bottom-leave-active,
.to-left-enter-active,
.to-left-leave-active,
.to-right-enter-active,
.to-right-leave-active,
.to-top-enter-active,
.to-top-leave-active {
	transition: all 250ms;
}

/* to top */
.to-top-enter-active,
.to-top-leave-to {
	opacity: 0;
	transform: translateY(10px);
}

.to-top-leave,
.to-top-enter-to {
	opacity: 1;
	transform: translateY(0);
}

/* to right */
.to-right-enter-active,
.to-right-leave-to {
	opacity: 0;
	transform: translateX(-10px);
}

.to-right-leave,
.to-right-enter-to {
	opacity: 1;
	transform: translateX(0);
}

/* to bottom */
.to-bottom-enter-active,
.to-bottom-leave-to {
	opacity: 0;
	transform: translateY(-10px);
}

.to-bottom-leave,
.to-bottom-enter-to {
	opacity: 1;
	transform: translateY(0);
}

/* to left */
.to-left-enter-active,
.to-left-leave-to {
	opacity: 0;
	transform: translateX(10px);
}

.to-left-leave,
.to-left-enter-to {
	opacity: 1;
	transform: translateX(0);
}
</style>
