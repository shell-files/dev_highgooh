import { useState, useEffect, useCallback } from 'react';
import '@styles/outhistory.css';

// ==========================================
// [백엔드 DB 대용] 전체 원본 마스터 더미 데이터
// ==========================================
const MASTER_HISTORY_DATA = [
    { id: 1, date: '2026-05-28', orderNo: 'ORD-20260602-001', customer: '현대모빌리티', company: '대한택배', boxQty: 3, status: '기한 달성' },
    { id: 2, date: '2026-05-26', orderNo: 'ORD-20260602-002', customer: '기아테크', company: '경동화물', boxQty: 2, status: '기한 달성' },
    { id: 3, date: '2026-05-26', orderNo: 'ORD-20260602-003', customer: '르노솔루션', company: '대신정기화물', boxQty: 1, status: '기한 초과' },
    { id: 4, date: '2026-06-11', orderNo: 'ORD-20260611-001', customer: '현대모빌리티', company: '대한택배', boxQty: 5, status: '기한 달성' },
    { id: 5, date: '2026-06-15', orderNo: 'ORD-20260615-002', customer: '기아테크', company: '경동화물', boxQty: 4, status: '기한 초과' },
    // { id: 6, date: '2026-05-10', orderNo: 'ORD-20260510-001', customer: '르노솔루션', company: '대한택배', boxQty: 2, status: '기한 달성' },
    // { id: 7, date: '2026-05-01', orderNo: 'ORD-20260501-001', customer: '현대모빌리티', company: '대신정기화물', boxQty: 1, status: '기한 초과' },
];

const MASTER_DETAIL_DATA = {
    'ORD-20260602-001': [
        { boxNo: 'BOX-001-01', productName: 'Al 시트레일 압출재 (6063-T5)', trackingNo: 'TRK-402910293' },
        { boxNo: 'BOX-001-02', productName: '조립용 고정 볼트 (M6)', trackingNo: 'TRK-402910294' },
        { boxNo: 'BOX-001-03', productName: '알루미늄 플레이트 (5052)', trackingNo: 'TRK-402910295' }
    ],
    'ORD-20260602-002': [
        { boxNo: 'BOX-002-01', productName: '고강도 알루미늄 배론 (7075)', trackingNo: 'TRK-773019203' },
        { boxNo: 'BOX-002-02', productName: 'Al 시트레일 압출재 (6063-T5)', trackingNo: 'TRK-773019204' }
    ],
    'ORD-20260602-003': [
        { boxNo: 'BOX-003-01', productName: '고강도 알루미늄 배론 (7075)', trackingNo: 'TRK-882910392' }
    ],
    'ORD-20260611-001': [
        { boxNo: 'BOX-004-01', productName: '배터리 케이스 프레임', trackingNo: '양하 준비중' }
    ],
    'ORD-20260615-002': [
        { boxNo: 'BOX-005-01', productName: '범퍼 가드 레일', trackingNo: '양하 준비중' }
    ]
};

const OutHistory = () => {
    // ------------------------------------------
    // 상태 관리 (State)
    // ------------------------------------------
    const [historyList, setHistoryList] = useState([]); 
    const [transportList, setTransportList] = useState([]); 
    const [customerList, setCustomerList] = useState([]); 
    
    // 현황판 통계 데이터 상태
    const [summaryData, setSummaryData] = useState({ total: 0, scheduled: 0, achieved: 0, overdue: 0 });
    const [summaryBadge, setSummaryBadge] = useState('당월'); // '당월' 또는 '선택'

    // 실제 검색에 적용된 필터 조건 상태 (조회하기 버튼 클릭 시 업데이트)
    const [appliedFilters, setAppliedFilters] = useState({ startDate: '', endDate: '', orderNo: '', customer: '', transport: '' });
    // 입력 폼 제어용 임시 상태
    const [formFilters, setFormFilters] = useState({ startDate: '', endDate: '', orderNo: '', customer: '', transport: '' });

    // 페이지네이션
    const [page, setPage] = useState(1); 
    const [totalCount, setTotalCount] = useState(0); 
    const [totalPages, setTotalPages] = useState(0); 
    const [size] = useState(20);

    // 모달 관련 상태
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedOrderNo, setSelectedOrderNo] = useState('');
    const [modalData, setModalData] = useState({ customer: '', transportCompany: '', status: '', boxes: [] });

    // ------------------------------------------
    // 최초 1회 실행: 필터 옵션(고객사/운송사) 리스트업
    // ------------------------------------------
    useEffect(() => {
        const dummyTransports = [
            { id: 'TRA01', value: '대한택배', label: '대한택배' },
            { id: 'TRA02', value: '경동화물', label: '경동화물' },
            { id: 'TRA03', value: '대신정기화물', label: '대신정기화물' }
        ];
        const uniqueCustomers = Array.from(new Set(MASTER_HISTORY_DATA.map(item => item.customer)));
        
        setTransportList(dummyTransports);
        setCustomerList(uniqueCustomers);
    }, []);

    // ------------------------------------------
    // 핵심 비즈니스 로직: API 호출 함수 모의화 (Fetch Data)
    // ------------------------------------------
    const fetchHistoryData = useCallback(() => {
        // 백엔드 API에 appliedFilters와 page, size를 보냈다고 가정하는 시뮬레이션입니다.
        let filtered = [...MASTER_HISTORY_DATA];

        // 1. 필터링 조건 적용
        if (appliedFilters.startDate) {
            filtered = filtered.filter(item => item.date >= appliedFilters.startDate);
        }
        if (appliedFilters.endDate) {
            filtered = filtered.filter(item => item.date <= appliedFilters.endDate);
        }
        if (appliedFilters.orderNo) {
            filtered = filtered.filter(item => item.orderNo.toLowerCase().includes(appliedFilters.orderNo.toLowerCase()));
        }
        if (appliedFilters.customer) {
            filtered = filtered.filter(item => item.customer === appliedFilters.customer);
        }
        if (appliedFilters.transport) {
            filtered = filtered.filter(item => item.company === appliedFilters.transport);
        }

        // 2. 통계 데이터(현황판) 계산 로직 (API에서 테이블 데이터와 함께 내려주는 구조 구현)
        const total = filtered.length;
        const scheduled = filtered.filter(item => item.status === '출고 예정').length;
        const achieved = filtered.filter(item => item.status === '기한 달성').length;
        const overdue = filtered.filter(item => item.status === '기한 초과').length;

        setSummaryData({ total, scheduled, achieved, overdue });

        // 출고 기간 필터 유무에 따른 현황판 뱃지 텍스트 변경 적용
        if (appliedFilters.startDate || appliedFilters.endDate) {
            setSummaryBadge('선택');
        } else {
            setSummaryBadge('당월');
        }

        // 3. 페이징 연산 후 데이터 세팅
        const calculatedTotalCount = filtered.length;
        const calculatedTotalPages = Math.ceil(calculatedTotalCount / size) || 1;

        const indexOfLastItem = page * size;
        const indexOfFirstItem = indexOfLastItem - size;
        const currentItems = filtered.slice(indexOfFirstItem, indexOfLastItem);

        setTotalCount(calculatedTotalCount);
        setTotalPages(calculatedTotalPages);
        setHistoryList(currentItems);
    }, [appliedFilters, page, size]);

    // 필터 조건이나 페이지가 바뀔 때마다 테이블 및 대시보드 리로드 수행
    useEffect(() => {
        fetchHistoryData();
    }, [fetchHistoryData]);

    // ------------------------------------------
    // 이벤트 핸들러 (Event Handlers)
    // ------------------------------------------
    const handleInputChange = (e) => {
        const { id, value } = e.target;
        // input 요소들의 id에 맞춰 formFilters 상태 매핑 업데이트
        const keyMap = {
            search_start_date: 'startDate',
            search_end_date: 'endDate',
            search_order_number: 'orderNo',
            search_customer: 'customer',
            search_transport: 'transport'
        };
        setFormFilters(prev => ({ ...prev, [keyMap[id]]: value }));
    };

    const handleSearchSubmit = (e) => {
        e.preventDefault();
        setPage(1); // 검색 시 첫 페이지로 리셋
        setAppliedFilters({ ...formFilters }); // 현재 폼 입력값을 실 적용 필터 상태로 전송 -> useEffect 트리거
    };

    const handleReset = () => {
        const clearedFilters = { startDate: '', endDate: '', orderNo: '', customer: '', transport: '' };
        setFormFilters(clearedFilters);
        setAppliedFilters(clearedFilters);
        setPage(1);
    };

    // [주문번호 클릭] 특정 주문의 상세 박스 정보를 가져오는 단일 API 호출 모의 기능
    const openDetailModal = async (item) => {
        setSelectedOrderNo(item.orderNo);
        
        try {
            // 가상의 API 통신 구조 (상세 정보 Lazy Loading 요청)
            // const response = await axios.get(`/api/out-history/${item.orderNo}`);
            const responseBoxes = MASTER_DETAIL_DATA[item.orderNo] || [];

            setModalData({
                customer: item.customer, 
                transportCompany: item.company, 
                status: item.status, 
                boxes: responseBoxes          
            });
            setIsModalOpen(true);
        } catch (error) {
            alert('상세 정보를 가져오는 중 오류가 발생했습니다.');
        }
    };

    const closeAsnDetailModal = () => {
        setIsModalOpen(false);
    };

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
                        <span className={`status-badge bg-all-light text-muted`}>
                            {summaryBadge}
                        </span>
                    </div>
                </div>
                {/* <div className="summary-card-item">
                    <div className="card-info-left">
                        <span className="summary-label">출고 예정</span>
                        <span className="summary-value text-green">{summaryData.scheduled}<small>건</small></span>
                    </div>
                    <div className="card-trend-right">
                        <span className={`status-badge ${summaryBadge === '당월' ? 'bg-green-light text-green' : 'bg-blue-light text-blue'}`}>
                            {summaryBadge}
                        </span>
                    </div>
                </div> */}
                <div className="summary-card-item">
                    <div className="card-info-left">
                        <span className="summary-label">기한 달성</span>
                        <span className="summary-value text-green">{summaryData.achieved}<small>건</small></span>
                    </div>
                    <div className="card-trend-right">
                        <span className={`status-badge bg-green-light text-green`}>
                            {summaryBadge}
                        </span>
                    </div>
                </div>
                <div className="summary-card-item">
                    <div className="card-info-left">
                        <span className="summary-label">기한 초과</span>
                        <span className="summary-value text-red">{summaryData.overdue}<small>건</small></span>
                    </div>
                    <div className="card-trend-right">
                        <span className={`status-badge bg-red-light text-red`}>
                            {summaryBadge}
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
                                {customerList.map((customer, index) => (
                                    <option key={index} value={customer}>{customer}</option>
                                ))}
                            </select>
                        </div>
                    </div>
                    
                    <div className="filter-group">
                        <label>운송사 선택</label>
                        <div className="date-range-container">
                            <select id="search_transport" className="filter-control select-control" value={formFilters.transport} onChange={handleInputChange}>
                                <option value="">전체 운송사</option>
                                {transportList.map((transport) => (
                                    <option key={transport.id} value={transport.value}>{transport.label}</option>
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
                        <button type="button" className="btn-filter-reset">엑셀 다운로드</button>
                        <button type="button" className="btn-filter-search" onClick={fetchHistoryData}>새로고침</button>
                    </div>
                    <table className="history-data-table">
                        <thead>
                            <tr>
                                <th>출고일자</th>
                                <th>주문 번호</th>
                                <th>고객사</th>
                                <th>운송사</th>
                                <th>총 박스 수량</th>
                                <th>배송 상태</th>
                            </tr>
                        </thead>
                        <tbody id="historyTableBody">
                            {historyList.length > 0 ? (
                                historyList.map((item) => (
                                    <tr key={item.id}>
                                        <td className="text-center">{item.date}</td>
                                        <td className="font-bold text-link" style={{ cursor: 'pointer' }} onClick={() => openDetailModal(item)}>
                                            {item.orderNo}
                                        </td>
                                        <td className="text-center">{item.customer}</td>
                                        <td className="text-center">{item.company}</td>
                                        <td className="text-center">{item.boxQty} EA</td>
                                        <td className="text-center">
                                            <span className={`status-badge ${
                                                item.status === '기한 달성' ? 'bg-green-light text-green' : 
                                                item.status === '기한 초과' ? 'bg-red-light text-red' : 'bg-blue-light text-blue'
                                            }`}>
                                                {item.status}
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
                        <button className="btn-page first" onClick={() => setPage(1)} disabled={page <= 1}>&laquo;</button>
                        <button className="btn-page prev" onClick={() => setPage(p => p - 1)} disabled={page <= 1}>&lsaquo;</button>
                        
                        {Array.from({ length: totalPages }, (_, i) => i + 1).map(index => (
                            <button
                                key={index}
                                className={page === index ? 'btn-page-num active' : 'btn-page-num'}
                                onClick={() => setPage(index)}
                            >
                                {index}
                            </button>
                        ))}
                        
                        <button className="btn-page next" onClick={() => setPage(p => p + 1)} disabled={page === totalPages}>&rsaquo;</button>
                        <button className="btn-page last" onClick={() => setPage(totalPages)} disabled={page === totalPages}>&raquo;</button>
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
                                <input type="text" className="table-inner-input disabled-input" readOnly value={modalData.customer} />
                            </div>
                            <div className="form-group-item">
                                <label className="modal-form-label">운송사</label>
                                <input type="text" className="table-inner-input disabled-input" readOnly value={modalData.transportCompany} />
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
                                                        <span className={`status-badge ${
                                                            modalData.status === '기한 달성' ? 'bg-green-light text-green' : 
                                                            modalData.status === '기한 초과' ? 'bg-red-light text-red' : 'bg-blue-light text-blue'
                                                        }`}>
                                                            {modalData.status}
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