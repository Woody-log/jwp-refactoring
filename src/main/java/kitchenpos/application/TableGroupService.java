package kitchenpos.application;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;
import kitchenpos.dto.TableGroupRequest;
import kitchenpos.dto.TableGroupResponse;
import kitchenpos.exception.NotFoundException;
import kitchenpos.repository.OrderRepository;
import kitchenpos.repository.OrderTableRepository;
import kitchenpos.repository.TableGroupRepository;

@Service
public class TableGroupService {
	private final OrderRepository orderRepository;
	private final OrderTableRepository orderTableRepository;
	private final TableGroupRepository tableGroupRepository;

	public TableGroupService(final OrderRepository orderRepository, final OrderTableRepository orderTableRepository, final TableGroupRepository tableGroupRepository) {
		this.orderRepository = orderRepository;
		this.orderTableRepository = orderTableRepository;
		this.tableGroupRepository = tableGroupRepository;
	}

	@Transactional
	public TableGroupResponse create(final TableGroupRequest tableGroupRequest) {
		final List<Long> orderTableIds = tableGroupRequest.getOrderTableIds();

		if (CollectionUtils.isEmpty(orderTableIds) || orderTableIds.size() < 2) {
			throw new IllegalArgumentException();
		}

		final List<OrderTable> savedOrderTables = orderTableRepository.findAllByIdIn(orderTableIds);

		if (orderTableIds.size() != savedOrderTables.size()) {
			throw new IllegalArgumentException();
		}
		final TableGroup savedTableGroup = tableGroupRepository.save(TableGroup.create(savedOrderTables));
		return TableGroupResponse.of(savedTableGroup);
	}

	@Transactional
	public void ungroup(final Long tableGroupId) {
		TableGroup tableGroup = tableGroupRepository.findById(tableGroupId)
			.orElseThrow(() -> new NotFoundException("단체 테이블 정보를 찾을 수 없습니다."));
		final List<OrderTable> orderTables = tableGroup.getOrderTables();

		final List<Long> orderTableIds = orderTables.stream()
			.map(OrderTable::getId)
			.collect(Collectors.toList());

		//memo [2021-01-5 20:43] TableService와 동일한 코드가 있는데 어떻게 빼면 좋을까
		if (orderRepository.existsByOrderTableIdInAndOrderStatusIn(
			orderTableIds, Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))) {
			throw new IllegalArgumentException();
		}

		tableGroup.ungroup();

	}
}
