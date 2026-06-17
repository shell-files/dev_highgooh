import { useEffect, useState, useCallback } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import * as XLSX from 'xlsx';
import {
    getOutboundHistory,
    getOutboundHistoryDetail,
    setPage,
    closeModal,
    getPartnerList
} from '@stores/outboundHistorySlice';
import { getFirstDay, getLastDayOfMonth, getToday, addOneDay } from '@stores/date';
import '@styles/outhistory.css';

const OutHistory = () => {
    const dispatch = useDispatch();
    const { customers, transports } = useSelector((state) => state.outboundHistory.filters);

    // 1. Redux Store에서 백엔드 데이터 상태 구독
    const { list: historyList, summary, page, totalCount, totalPages, size } = useSelector((state) => state.outboundHistory.view);
    const detailData = useSelector((state) => state.outboundHistory.detailData);
    const isModalOpen = useSelector((state) => state.outboundHistory.isModal);
    const loading = useSelector((state) => state.outboundHistory.loading);
    const detailLoading = useSelector((state) => state.outboundHistory.detailLoading);
    const [searchTrigger, setSearchTrigger] = useState(0);

    const [partnerName, setPartnerName] = useState("");
    const [transName, setTransName] = useState("");
    const [startDay, setStartDay] = useState("");
    const [endDay, setEndDay] = useState("");

    const tableColList = ["출고일자", "주문번호", "고객사","운송사","총 박스 수량","배송 상태"]

    // 2. 가짜 데이터 필드 구조 호환을 위한 맵 변환 가공
    const summaryData = {
        total: summary.total || 0,
        achieved: summary.achieved || 0,
        overdue: summary.overdue || 0
    };




    // 3. 필터 폼 컴포넌트 내부 로컬 상태 관리
    const [formFilters, setFormFilters] = useState({
        startDate: getFirstDay(),
        endDate: getToday(),
        orderNo: '',
        customer: '',
        transport: ''
    });


    // 4. 백엔드 연동 API 액션 디스패치 함수
    const fetchHistoryData = useCallback(() => {
        const credentials = {
            page: page,
            size: size,
            orderStart: formFilters.startDate,  // ← 혹시 "" 아닌지 확인
            orderEnd: addOneDay(formFilters.endDate),
            outboundId: Number(formFilters.orderNo) || 0,
            partnerCompanyId: Number(formFilters.customer) || 0,
            transportCompanyId: Number(formFilters.transport) || 0
        };

        dispatch(getOutboundHistory(credentials));
        setStartDay(formFilters.startDate)
        setEndDay(formFilters.endDate)
    }, [dispatch, page, size, formFilters]);


    // 페이지 번호 및 리로드 트리거 감지
    useEffect(() => {
        fetchHistoryData();
    }, [page, searchTrigger]);

    useEffect(() => {
        dispatch(getPartnerList());
    }, [dispatch]);

    // 5. 컴포넌트 핸들러 함수들 정의 (기존 마크업 id 분기 매핑)
    const handleInputChange = (e) => {
        const { id, value } = e.target;
        setFormFilters(prev => {
            if (id === 'search_start_date') return { ...prev, startDate: value };
            if (id === 'search_end_date') return { ...prev, endDate: value };
            if (id === 'search_order_number') return { ...prev, orderNo: value };
            if (id === 'search_customer') return { ...prev, customer: value };
            if (id === 'search_transport') return { ...prev, transport: value };
            return prev;
        });
    };



    const handleSearchSubmit = (e) => {
        e.preventDefault();
        dispatch(setPage(1));
        setSearchTrigger(prev => prev + 1);
        // fetchHistoryData() 제거
    };
    const handleReset = () => {
        setFormFilters({
            startDate: getFirstDay(),
            endDate: getToday(),
            orderNo: '',
            customer: '',
            transport: ''
        });
        setStartDay(getFirstDay())
        setEndDay(getToday())
        dispatch(setPage(1));

    };

    const openDetailModal = (item) => {
        const outboundId = item.outbound_id || item.id;
        dispatch(getOutboundHistoryDetail(outboundId));
        setPartnerName(item.partner);
        setTransName(item.trans);
    };

    const closeAsnDetailModal = () => {
        dispatch(closeModal());
        setPartnerName("");
        setTransName("");
    };

    // 모달 상단 헤더 렌더링용 변수 가공
    const selectedOrderNo = detailData && detailData.length > 0 ? (detailData[0].outbound_id) : '';


    // modalData 수정
    const modalData = {
        customer: detailData && detailData.length > 0 ? detailData[0].partner_company_name : '-',
        transportCompany: detailData && detailData.length > 0 ? detailData[0].transport_company_name : '-',
        boxes: detailData ? detailData.map(box => ({
            boxNo: box.packing_invoice_number || '-',
            productName: box.name || '상품 정보 없음',
            trackingNo: box.invoice_number || '-',
            deliveryStatus: box.delivery_status === '기한달성' ? '기한 달성' : '기한 초과'  // ← 박스별로 보관
        })) : []
    };

    const handleDownload = (data) => {
        // 1. 데이터를 기반으로 워크시트(Worksheet) 생성

        const replaceData = []
        data.map((v)=> {replaceData.push({"출고일자" : v.updated_at, "주문번호" : v.outbound_id, "고객사" : v.partner, "운송사" : v.trans, "총 박스 수량" : `${v.cnt} EA`, "배송상태" : v.delivery_status})})
        console.log(replaceData)
        
        const worksheet = XLSX.utils.json_to_sheet(replaceData);

        // (선택) 엑셀 시트의 헤더(열 이름)를 한글로 예쁘게 변경하고 싶을 때
        XLSX.utils.sheet_add_aoa(worksheet, [tableColList], { origin: "A1" });

        // 2. 새로운 워크북(Workbook)을 생성하고 워크시트 추가
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, `출고이력_${page}페이지_${startDay}~${endDay}`);

        // 3. 엑셀 파일 작성 및 다운로드 실행
        // 파일명은 원하는 대로 지정할 수 있습니다.
        XLSX.writeFile(workbook, `출고이력_${page}페이지_${startDay}~${endDay}.xlsx`);
    };

    // console.log(historyList)
    return (
        <div id="outHistory-page">
            <div className="page-header-flex">
                <h2 className="page-title">출고이력</h2>
            </div>

            {/* 통계 데이터 현황판 */}
            <div className="order-summary-grid">
                <div className="summary-card-item">
                    <div className="card-info-left">
                        <span className="summary-label">전체</span>
                        <span className="summary-value">{summaryData.total}<small>건</small></span>
                    </div>
                    <div className="card-trend-right">
                        <span className="status-badge bg-all-light text-muted">
                            {getFirstDay() === startDay && getToday() === endDay ? "당월" : "선택"}
                        </span>
                    </div>
                </div>

                <div className="summary-card-item">
                    <div className="card-info-left">
                        <span className="summary-label">기한 달성</span>
                        <span className="summary-value text-green">{summaryData.achieved}<small>건</small></span>
                    </div>
                    <div className="card-trend-right">
                        <span className="status-badge bg-green-light text-green">
                            {getFirstDay() === startDay && getToday() === endDay ? "당월" : "선택"}
                        </span>
                    </div>
                </div>

                <div className="summary-card-item">
                    <div className="card-info-left">
                        <span className="summary-label">기한 초과</span>
                        <span className="summary-value text-red">{summaryData.overdue}<small>건</small></span>
                    </div>
                    <div className="card-trend-right">
                        <span className="status-badge bg-red-light text-red">
                            {getFirstDay() === startDay && getToday() === endDay ? "당월" : "선택"}
                        </span>
                    </div>
                </div>
            </div>

            {/* 검색 필터 영역 */}
            <div className="filter-wrapper-card">
                <form className="search-filter-grid" onSubmit={handleSearchSubmit}>
                    <div className="filter-group group-date-range">
                        <label>출고 기간</label>
                        <div className="date-range-container">
                            <input type="date" id="search_start_date" className="filter-control" value={formFilters.startDate} onChange={handleInputChange} />
                            <span className="date-separator">~</span>
                            <input type="date" id="search_end_date" className="filter-control" value={formFilters.endDate} onChange={handleInputChange} />
                        </div>
                    </div>
                    <div className="filter-group">
                        <label>주문 번호 검색</label>
                        <div className="date-range-container">
                            <input type="text" id="search_order_number" className="filter-control" value={formFilters.orderNo} onChange={handleInputChange} placeholder="주문번호 입력" />
                        </div>
                    </div>

                    <div className="filter-group">
                        <label>고객사 선택</label>
                        <div className="date-range-container">
                            <select id="search_customer" className="filter-control select-control" value={formFilters.customer} onChange={handleInputChange}>
                                <option value="">전체 고객사</option>
                                {customers.map((c) => (
                                    <option key={c.id} value={c.id}>{c.name}</option>
                                ))}


                            </select>
                        </div>
                    </div>

                    <div className="filter-group">
                        <label>운송사 선택</label>
                        <div className="date-range-container">
                            <select id="search_transport" className="filter-control select-control" value={formFilters.transport} onChange={handleInputChange}>
                                <option value="">전체 운송사</option>
                                {transports.map((t) => (
                                    <option key={t.id} value={t.id}>{t.name}</option>
                                ))}
                            </select>
                        </div>
                    </div>

                    <div className="filter-btn-group">
                        <button type="button" className="btn-filter-reset" onClick={handleReset}>초기화</button>
                        <button type="submit" className="btn-filter-search">조회하기</button>
                    </div>
                </form>
            </div>

            {/* 메인 데이터 테이블 */}
            <div className="content-card">
                <div className="table-responsive">
                    <div className="inhistory-btn-group">
                        <button type="button" className="btn-filter-reset" onClick={()=>{handleDownload(historyList)}}>엑셀 다운로드</button>
                        <button type="button" className="btn-filter-search" onClick={fetchHistoryData}>새로고침</button>
                    </div>
                    <table className="history-data-table">
                        <thead>
                            
                            <tr>
                                {
                                    tableColList.map((v,i)=><th key={i}>{v}</th>)
                                }

                            </tr>
                        </thead>
                        <tbody id="historyTableBody">
                            {loading ? (
                                <tr>
                                    <td colSpan="6" className="text-center" style={{ padding: '2rem', color: '#64748b' }}>
                                        데이터를 로딩 중입니다...
                                    </td>
                                </tr>
                            ) : historyList && historyList.length > 0 ? (
                                historyList.map((item) => (
                                    <tr key={item.outbound_id || item.id}>
                                        <td className="text-center">{item.updated_at || item.date || '-'}</td>
                                        <td className="font-bold text-link" style={{ cursor: 'pointer' }} onClick={() => openDetailModal(item)}>
                                            {item.outbound_id || item.id}
                                        </td>
                                        <td className="text-center">{item.partner || item.customer || '-'}</td>
                                        <td className="text-center">{item.trans || item.company || '-'}</td>
                                        <td className="text-center">{item.cnt || item.boxQty || 0} EA</td>
                                        <td className="text-center">
                                            <span className={`status-badge ${(item.delivery_status === '기한달성' || item.status === '기한 달성') ? 'bg-green-light text-green' :
                                                (item.delivery_status === '기한초과' || item.status === '기한 초과') ? 'bg-red-light text-red' : 'bg-blue-light text-blue'
                                                }`}>
                                                {item.delivery_status || item.status}
                                            </span>
                                        </td>
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="6" className="text-center" style={{ padding: '2rem', color: '#94a3b8' }}>
                                        조회된 출고 이력이 없습니다.
                                    </td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>

                {/* 페이지네이션 */}
                <div className="pagination-container">
                    <div className="pagination-info">전체 <span>{totalCount}</span>건</div>
                    <div className="pagination-buttons">
                        <button className="btn-page first" onClick={() => dispatch(setPage(1))} disabled={page <= 1}>&laquo;</button>
                        <button className="btn-page prev" onClick={() => dispatch(setPage(page - 1))} disabled={page <= 1}>&lsaquo;</button>

                        {Array.from({ length: totalPages }, (_, i) => i + 1).map(index => (
                            <button
                                key={index}
                                className={page === index ? 'btn-page-num active' : 'btn-page-num'}
                                onClick={() => dispatch(setPage(index))}
                            >
                                {index}
                            </button>
                        ))}

                        <button className="btn-page next" onClick={() => dispatch(setPage(page + 1))} disabled={page === totalPages}>&rsaquo;</button>
                        <button className="btn-page last" onClick={() => dispatch(setPage(totalPages))} disabled={page === totalPages}>&raquo;</button>
                    </div>
                    <div className="pagination-size-selector" />
                </div>
            </div>

            {/* 출고 상세 정보 조회 모달 */}
            <div className={`modal-overlay ${isModalOpen ? 'active' : ''}`} id="asnDetailModal">
                <div className="modal-container modal-window">
                    <div className="modal-header">
                        <h3>출고 상세 정보 (주문)</h3>
                        <button className="modal-close-btn" onClick={closeAsnDetailModal}>&times;</button>
                    </div>

                    <div className="modal-body">
                        <div className="modal-form-inline-grid">
                            <div className="form-group-item">
                                <label className="modal-form-label">주문 번호</label>
                                <input type="text" className="table-inner-input disabled-input" readOnly value={selectedOrderNo} />
                            </div>
                            <div className="form-group-item">
                                <label className="modal-form-label">고객사</label>
                                <input type="text" className="table-inner-input disabled-input" readOnly value={partnerName} />
                            </div>
                            <div className="form-group-item">
                                <label className="modal-form-label">운송사</label>
                                <input type="text" className="table-inner-input disabled-input" readOnly value={transName} />
                            </div>
                        </div>

                        {/* 박스 정보 테이블 탭 */}
                        <div className="sheet-tab-content active">
                            <div className="excel-table-wrapper">
                                <table className="excel-styled-table">
                                    <thead>
                                        <tr>
                                            <th className="th-no" style={{ width: '60px' }}>No</th>
                                            <th className="th-code">박스 번호</th>
                                            <th className="th-name">제품명</th>
                                            <th className="th-qty">송장번호</th>
                                            <th className="th-status" style={{ width: '100px' }}>배송상태</th>
                                        </tr>
                                    </thead>
                                    <tbody id="detailAsnTableBody">
                                        {modalData.boxes && modalData.boxes.length > 0 ? (
                                            modalData.boxes.map((box, index) => (
                                                <tr key={index}>
                                                    <td className="td-no text-center">{index + 1}</td>
                                                    <td className="td-code font-bold">{box.boxNo}</td>
                                                    <td className="td-name">{box.productName}</td>
                                                    <td className="td-qty text-center">{box.trackingNo}</td>
                                                    <td className="text-center">
                                                        <span className={`status-badge ${box.deliveryStatus === '기한 달성' ? 'bg-green-light text-green' :
                                                            box.deliveryStatus === '기한 초과' ? 'bg-red-light text-red' : 'bg-blue-light text-blue'
                                                            }`}>
                                                            {box.deliveryStatus}
                                                        </span>
                                                    </td>
                                                </tr>
                                            ))
                                        ) : (
                                            <tr>
                                                <td colSpan="5" className="text-center" style={{ padding: '1rem' }}>데이터를 불러오는 중이거나 박스 정보가 없습니다.</td>
                                            </tr>
                                        )}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>

                    <div className="modal-footer">
                        <button type="button" className="btn-pop-cancel" onClick={closeAsnDetailModal}>닫기</button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default OutHistory;