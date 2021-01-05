package kitchenpos.application;

import static kitchenpos.common.TestFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import kitchenpos.common.BaseTest;
import kitchenpos.domain.OrderTable;
import kitchenpos.dto.OrderTableRequest;
import kitchenpos.dto.OrderTableResponse;
import kitchenpos.repository.OrderTableRepository;

@DisplayName("TableService 테스트")
class TableServiceTest extends BaseTest {

	@Autowired
	private TableService tableService;

	@Autowired
	private OrderTableRepository orderTableRepository;

	@DisplayName("주문테이블을 생성할 수 있다.")
	@Test
	void create() {
		OrderTableResponse table = tableService.create();

		OrderTable savedTable = orderTableRepository.findById(table.getId()).orElse(null);

		assertAll(
			() -> assertThat(savedTable.getId()).isNotNull(),
			() -> assertThat(savedTable.isEmpty()).isTrue()
		);
	}

	@DisplayName("주문테이블을 조회할 수 있다.")
	@Test
	void list() {
		List<OrderTableResponse> orderTables = tableService.list();

		assertThat(orderTables).hasSize(8);
	}

	@DisplayName("빈테이블 설정 또는 해지 수 있다.")
	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void changeEmpty(boolean isEmpty) {
		OrderTableRequest orderTableRequest = OrderTableRequest.of(빈테이블ID, isEmpty);
		tableService.changeEmpty(빈테이블ID, orderTableRequest);

		OrderTable savedTable = orderTableRepository.findById(빈테이블ID).orElse(null);

		assertThat(savedTable.isEmpty()).isEqualTo(isEmpty);
	}

	@DisplayName("없는 테이블에는 빈테이블 지정 또는 해지할 수 없다.")
	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void changeEmptyThrow1(boolean isEmpty) {
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> {
				OrderTableRequest orderTableRequest = OrderTableRequest.of(존재하지않는_테이블ID, isEmpty);
				tableService.changeEmpty(존재하지않는_테이블ID, orderTableRequest);
			});
	}

	@DisplayName("단체지정된 테이블은 빈테이블 지정 또는 해지할 수 없다.")
	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void changeEmptyThrow2(boolean isEmpty) {
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> {
				OrderTableRequest orderTableRequest = OrderTableRequest.of(단체지정_테이블ID, isEmpty);
				tableService.changeEmpty(단체지정_테이블ID, orderTableRequest);
			});
	}

	@DisplayName("주문 테이블 상태가 주문 상태가 조리 주문 테이블은 빈 테이블 설정 또는 해지할 수 없다.")
	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void changeEmptyThrow3(boolean isEmpty) {
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> {
				OrderTableRequest orderTableRequest = OrderTableRequest.of(주문상태_조리인_테이블ID, isEmpty);
				tableService.changeEmpty(주문상태_조리인_테이블ID, orderTableRequest);
			});
	}

	@DisplayName("주문 테이블 상태가 주문 상태가 식사 주문 테이블은 빈 테이블 설정 또는 해지할 수 없다.")
	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void changeEmptyThrow4(boolean isEmpty) {
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> {
				OrderTableRequest orderTableRequest = OrderTableRequest.of(주문상태_식사인_테이블ID, isEmpty);
				tableService.changeEmpty(주문상태_식사인_테이블ID, orderTableRequest);
			});
	}

	@DisplayName("주문테이블에 손님수를 설정할 수 있다.")
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
	void changeNumberOfGuests(int numberOfGuest) {
		OrderTableRequest orderTableRequest = OrderTableRequest.of(주문상태_조리인_테이블ID, numberOfGuest);
		tableService.changeNumberOfGuests(주문상태_조리인_테이블ID, orderTableRequest);

		OrderTable savedTable = orderTableRepository.findById(주문상태_조리인_테이블ID).orElse(null);

		assertThat(savedTable.getNumberOfGuests()).isEqualTo(numberOfGuest);
	}

	@DisplayName("0보다 작은 손님수를 입력할 수 없다.")
	@ParameterizedTest
	@ValueSource(ints = {-1, -2, -3, -4, -5, -100})
	void changeNumberOfGuestsThrow1(int numberOfGuest) {
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> {
				OrderTableRequest orderTableRequest = OrderTableRequest.of(주문상태_조리인_테이블ID, numberOfGuest);
				tableService.changeNumberOfGuests(주문상태_조리인_테이블ID, orderTableRequest);
			});
	}

	@DisplayName("없는 테이블에 손님수를 설정할 수 없다.")
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
	void changeNumberOfGuestsThrow2(int numberOfGuest) {
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> {
				OrderTableRequest orderTableRequest = OrderTableRequest.of(존재하지않는_테이블ID, numberOfGuest);
				tableService.changeNumberOfGuests(존재하지않는_테이블ID, orderTableRequest);
			});
	}

	@DisplayName("빈테이블 상태인 경우 손님수를 설정할 수 없다.")
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
	void changeNumberOfGuestsThrow3(int numberOfGuest) {
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> {
				OrderTableRequest orderTableRequest = OrderTableRequest.of(빈테이블ID, numberOfGuest);
				tableService.changeNumberOfGuests(빈테이블ID, orderTableRequest);
			});
	}

}