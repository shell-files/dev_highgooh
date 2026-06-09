package cloud.weareithero.api.order.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import cloud.weareithero.api.order.dto.OrderCustomerDTO;
import cloud.weareithero.api.order.dto.OrderDTO;
import cloud.weareithero.api.order.dto.OrderDetailProductDTO;
import cloud.weareithero.api.order.dto.OrderProductDTO;
import cloud.weareithero.api.order.dto.OrderRequestDTO;
import cloud.weareithero.api.order.dto.OrderSummaryDTO;

@Mapper
public interface OrderMapper {

  /**
   * 1. Order 화면 상단 통계 블록 계산
   * OrderSummaryDTO의 필드명(total, newOrder, inProgress, completed)에 완벽 바인딩
   * 공통코드 기준: 7(출고대기->신규), 8(출고처리중->진행중), 9(출고완료->완료)
   */
  @Select("<script>" +
      """
          SELECT
            COUNT(*) AS `total`,
            SUM(CASE WHEN `opl`.`state_code` = 7 THEN 1 ELSE 0 END) AS `newOrder`,
            SUM(CASE WHEN `opl`.`state_code` = 8 THEN 1 ELSE 0 END) AS `inProgress`,
            SUM(CASE WHEN `opl`.`state_code` = 9 THEN 1 ELSE 0 END) AS `completed`
          FROM `ORDER_PRODUCT_LIST` `opl`
          JOIN `PARTNER_COMPANY_MASTER` `pcm`
            ON `opl`.`partner_company_id` = `pcm`.`id`
          """ +
      "<where>" +
      "<if test='orderStart != null and orderEnd != null'>" +
      " AND `opl`.`order_date` BETWEEN #{orderStart} AND #{orderEnd} " +
      "</if>" +
      "<if test='orderNo != null and orderNo != \"\"'>" +
      " AND `opl`.`id` LIKE CONCAT('%', #{orderNo}, '%') " +
      "</if>" +
      "<if test='customerName != null and customerName != \"\"'>" +
      " AND `pcm`.`name` LIKE CONCAT('%', #{customerName}, '%') " +
      "</if>" +
      "</where>" +
      "</script>")
  public OrderSummaryDTO findSummary(OrderRequestDTO orderRequestDTO);

  /**
   * 2. Order 메인 그리드 목록 조회
   * 화면 컬럼: 주문번호, 고객사명, 제품명, 수량, 금액, 주문일, 마감일 매핑
   * PageRequestDTO의 페이징(offset, size) 연동
   */
  @Select("<script>" +
      """
          SELECT
            `opl`.`id` AS `orderId`,
            `opl`.`partner_company_id` AS `partnerId`,
            `pcm`.`name` AS `partnerName`,
            (SELECT `opm`.`name`
             FROM `ORDER_PRODUCT` `op`
             JOIN `OUTBOUND_PRODUCT_MASTER` `opm` ON `op`.`outbound_product_id` = `opm`.`id`
             WHERE `op`.`order_product_list_id` = `opl`.`id` LIMIT 1) AS `orderProductName`,
            (SELECT SUM(`op`.`quantity`)
             FROM `ORDER_PRODUCT` `op`
             WHERE `op`.`order_product_list_id` = `opl`.`id`) AS `itemCount`,
            (SELECT SUM(CAST(`op`.`total_price` AS UNSIGNED))
             FROM `ORDER_PRODUCT` `op`
             WHERE `op`.`order_product_list_id` = `opl`.`id`) AS `price`,
            `opl`.`order_date` AS `orderDate`,
            `opl`.`deadline` AS `deadline`,
            `cc`.`name` AS `stateCode`
          FROM `ORDER_PRODUCT_LIST` `opl`
          JOIN `PARTNER_COMPANY_MASTER` `pcm` ON `opl`.`partner_company_id` = `pcm`.`id`
          LEFT JOIN `COMMON_CODE` `cc` ON `opl`.`state_code` = `cc`.`id`
          """ +
      "<where>" +
      "<if test='orderStart != null and orderEnd != null'>" +
      " AND `opl`.`order_date` BETWEEN #{orderStart} AND #{orderEnd} " +
      "</if>" +
      "<if test='orderNo != null and orderNo != \"\"'>" +
      " AND `opl`.`id` LIKE CONCAT('%', #{orderNo}, '%') " +
      "</if>" +
      "<if test='customerName != null and customerName != \"\"'>" +
      " AND `pcm`.`name` LIKE CONCAT('%', #{customerName}, '%') " +
      "</if>" +
      "</where>" +
      "ORDER BY `opl`.`id` DESC LIMIT #{offset}, #{size}" +
      "</script>")
  public List<OrderDTO> findAll(OrderRequestDTO orderRequestDTO);

  /**
   * 3. 특정 주문 마스터 단건 조회 (상세 팝업 Master 정보용)
   */
  @Select("""
      SELECT
        `opl`.`id` AS `orderId`,
        `opl`.`partner_company_id` AS `partnerId`,
        `pcm`.`name` AS `partnerName`,
        `opl`.`order_date` AS `orderDate`,
        `opl`.`deadline` AS `deadline`,
        `cc`.`name` AS `stateCode`
      FROM `ORDER_PRODUCT_LIST` `opl`
      JOIN `PARTNER_COMPANY_MASTER` `pcm` ON `opl`.`partner_company_id` = `pcm`.`id`
      LEFT JOIN `COMMON_CODE` `cc` ON `opl`.`state_code` = `cc`.`id`
      WHERE `opl`.`id` = #{orderId}
      """)
  public OrderDTO findByOrderId(int orderId);

  /**
   * 4. 상세 팝업 내부의 완제품 목록 리스트 조회
   * 화면 컬럼: 제품명(itemName), 수량(setCount), 금액(price) 매핑
   */
  @Select("""
      SELECT
        `op`.`id` AS `no`,
        `op`.`order_product_list_id` AS `orderId`,
        `op`.`outbound_product_id` AS `itemNo`,
        `opm`.`name` AS `itemName`,
        `op`.`quantity` AS `setCount`,
        `op`.`total_price` AS `price`
      FROM `ORDER_PRODUCT` `op`
      JOIN `OUTBOUND_PRODUCT_MASTER` `opm`
        ON `op`.`outbound_product_id` = `opm`.`id`
      WHERE `op`.`order_product_list_id` = #{orderId}
      """)
  public List<OrderDetailProductDTO> findOne(int orderId);

  /**
   * 5. 주문서 마스터 신규 생성
   * DB 설계에 맞게 신규 등록 시 기본 상태코드는 '출고대기(7)'로 기본 할정
   * useGeneratedKeys를 통해 생성된 AI 자동증가 ID를 orderId 필드에 자동으로 채워줌
   */
  @Insert("""
      INSERT INTO `ORDER_PRODUCT_LIST` (
        `partner_company_id`,
        `order_date`,
        `deadline`
      ) VALUES (
        #{partnerId},
        #{orderDate},
        #{deadline}
      )
      """)
  @Options(useGeneratedKeys = true, keyProperty = "orderId", keyColumn = "id")
  public int add(OrderDTO orderDTO);

  /**
   * 6. 주문 품목 상세 데이터(ORDER_PRODUCT) 저장
   */
  @Insert("""
      INSERT INTO `ORDER_PRODUCT` (
        `order_product_list_id`, -- DB 실제 컬럼명
        `outbound_product_id`,   -- DB 실제 컬럼명
        `quantity`,              -- DB 실제 컬럼명
        `total_price`            -- DB 실제 컬럼명
      ) VALUES (
        #{orderId},              -- DTO의 orderId 변수값 바인딩
        #{itemNo},               -- DTO의 itemNo 변수값 바인딩
        #{setCount},             -- DTO의 setCount 변수값 바인딩
        #{price}                 -- DTO의 price 변수값 바인딩
      )
      """)
  public int addOrderProduct(OrderDetailProductDTO orderDetailProductDTO);

  /**
   * 7. 주문 등록 모달 및 검색 팝업용 고객사 리스트 조회 (customer_yn_code가 1인 협력사)
   */
  @Select("""
      SELECT
        `id`,
        `name`
      FROM `PARTNER_COMPANY_MASTER`
      WHERE `customer_yn_code` = 1
      """)
  public List<OrderCustomerDTO> findByCustomer();

  /**
   * 8. 주문 등록 시 모달 그리드에 뿌려줄 출고 완제품 리스트 조회
   */
  @Select("""
      SELECT
        `id`,
        `name`
      FROM `OUTBOUND_PRODUCT_MASTER`
      """)
  public List<OrderProductDTO> findByProduct();

}