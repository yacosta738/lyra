import { BACKEND_API_URL } from '@/constants';
import type { OffsetPage, Subscriber } from '@lyra/vm-core';

interface SubscriberFilter {
	email?: string;
	name?: string;
	status?: string;
}

class SubscriberService {
	private static instance: SubscriberService;

	private constructor() {}

	public static getInstance(): SubscriberService {
		if (!SubscriberService.instance) {
			SubscriberService.instance = new SubscriberService();
		}
		return SubscriberService.instance;
	}

	public async getSubscribers(
		filter?: SubscriberFilter,
		sort?: string,
		size: number = 10,
		page: number = 0
	): Promise<OffsetPage<Subscriber>> {
		const url = new URL(`${BACKEND_API_URL}newsletter/subscribers`);

		const params = new URLSearchParams();
		if (filter) {
			Object.entries(filter).forEach(([key, value]) => {
				params.append(key, value);
			});
		}
		if (sort) {
			params.append('sort', sort);
		}
		params.append('size', size.toString());
		params.append('page', page.toString());

		url.search = params.toString();

		const response = await fetch(url);
		if (!response.ok) {
			throw new Error('Network response was not ok');
		}
		return await response.json();
	}
}

export default SubscriberService;
