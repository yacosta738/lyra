import { expect, test } from 'vitest';
import { BasicFilter, Filter } from '../../../../src/components/filter/Filter';
import {
	allFilterOperators,
	FilterOperator,
} from '../../../../src/components/filter/FilterOperator';
import { Property } from '../../../../src/components/filter/Property';

test('apply filter', () => {
	const filter: Filter<string> = new BasicFilter<string>('test');
	filter.addProperty({
		id: crypto.randomUUID(),
		name: 'name',
		label: 'Name',
		type: 'text',
		operator: FilterOperator.EQUAL,
		value: 'John',
		availableOperators: allFilterOperators,
	});
	filter.addProperty({
		id: crypto.randomUUID(),
		name: 'email',
		label: 'Email',
		type: 'email',
		operator: FilterOperator.EQUAL,
		value: 'john.doe@test.com',
		availableOperators: allFilterOperators,
	});
	expect(filter.properties.length).toBe(2);
	expect(filter.toQueryString()).toBe('name=eq:John&email=eq:john.doe@test.com');
});

test('remove property', () => {
	const filter: Filter<string> = new BasicFilter<string>('test');
	const property: Property<string> = {
		id: crypto.randomUUID(),
		name: 'name',
		label: 'Name',
		type: 'text',
		operator: FilterOperator.EQUAL,
		value: 'John',
		availableOperators: allFilterOperators,
	};
	filter.addProperty(property);
	filter.removeProperty(property.id);
	expect(filter.properties.length).toBe(0);
});

test('remove all properties', () => {
	const filter: Filter<string> = new BasicFilter<string>('test');
	filter.addProperty({
		id: crypto.randomUUID(),
		name: 'name',
		label: 'Name',
		type: 'text',
		operator: FilterOperator.EQUAL,
		value: 'John',
		availableOperators: allFilterOperators,
	});
	filter.addProperty({
		id: crypto.randomUUID(),
		name: 'email',
		label: 'Email',
		type: 'email',
		operator: FilterOperator.EQUAL,
		value: 'john.doe@test.com',
		availableOperators: allFilterOperators,
	});
	filter.removeAllProperties();
	expect(filter.properties.length).toBe(0);
});
