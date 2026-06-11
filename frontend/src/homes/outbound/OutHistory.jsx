import { useState, useEffect } from 'react';
import '@styles/outhistory.css';

const OutHistory = () => {
    // 데이터 및 페이지네이션 상태 관리
    const [historyList, setHistoryList] = useState([]); 
    const [mockAsnDetails, setMockAsnDetails] = useState({}); 
    const [transportList, setTransportList] = useState([]); 
    
    const [page, setPage] = useState(1); 
    const [totalCount, setTotalCount] = useState(0); 
    const [totalPages, setTotalPages] = useState(0); 
    const [size] = useState(20); 

    // 모달 관련 상태
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedAsnNo, setSelectedAsnNo] = useState('');
    const [modalData, setModalData] = useState({ transportCompany: '', status: '출고완료', boxes: [] });

    useEffect(() => {
        // 1. 메인 테이블 리스트 데이터
        const allDummyHistory = [
            { id: 1, date: '2026-05-28', asnNo: 'ASN-20260602-001', company: '대한택배', boxQty: 3, status: '출고완료' },
            { id: 2, date: '2026-05-26', asnNo: 'ASN-20260602-002', company: '경동화물', boxQty: 2, status: '출고완료' },
            { id: 3, date: '2026-05-26', asnNo: 'ASN-20260602-003', company: '대신정기화물', boxQty: 1, status: '출고완료' }
        ];

        // 2. 모달 상세 데이터 (요구사항 반영: 제품명, 송장번호 변경)
        const dummyDetails = {
            'ASN-20260602-001': {
                status: '출고완료',
                boxes: [
                    { boxNo: 'BOX-001-01', productName: 'Al 시트레일 압출재 (6063-T5)', trackingNo: 'TRK-402910293' },
                    { boxNo: 'BOX-001-02', productName: '조립용 고정 볼트 (M6)', trackingNo: 'TRK-402910294' },
                    { boxNo: 'BOX-001-03', productName: '알루미늄 플레이트 (5052)', trackingNo: 'TRK-402910295' }
                ]
            },
            'ASN-20260602-002': {
                status: '출고완료',
                boxes: [
                    { boxNo: 'BOX-002-01', productName: '고강도 알루미늄 배론 (7075)', trackingNo: 'TRK-773019203' },
                    { boxNo: 'BOX-002-02', productName: 'Al 시트레일 압출재 (6063-T5)', trackingNo: 'TRK-773019204' }
                ]
            },
            'ASN-20260602-003': {
                status: '출고완료',
                boxes: [
                    { boxNo: 'BOX-003-01', productName: '고강도 알루미늄 배론 (7075)', trackingNo: 'TRK-882910392' }
                ]
            }
        };

        const dummyTransports = [
            { id: 'TRA01', value: '대한택배', label: '대한택배' },
            { id: 'TRA02', value: '경동화물', label: '경동화물' },
            { id: 'TRA03', value: '대신정기화물', label: '대신정기화물' }
        ];

        // 페이징 연산
        const calculatedTotalCount = allDummyHistory.length;
        const calculatedTotalPages = Math.ceil(calculatedTotalCount / size) || 1;

        const indexOfLastItem = page * size;
        const indexOfFirstItem = indexOfLastItem - size;
        const currentItems = allDummyHistory.slice(indexOfFirstItem, indexOfLastItem);

        setTotalCount(calculatedTotalCount);
        setTotalPages(calculatedTotalPages);
        setHistoryList(currentItems);
        setMockAsnDetails(dummyDetails);
        setTransportList(dummyTransports); 
    }, [page, size]);

    // 모달 오픈 함수
    const openDetailModal = (item) => {
        const asnDetail = mockAsnDetails[item.asnNo];
        if (!asnDetail) {
            alert('해당 매니페스트의 상세 정보를 찾을 수 없습니다.');
            return;
        }

        setSelectedAsnNo(item.asnNo);
        setModalData({
            transportCompany: item.company, 
            status: '출고완료',            
            boxes: asnDetail.boxes          
        });
        setIsModalOpen(true);
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
                        <span className="summary-label">출고 확정</span>
                        <span className="summary-value text-green">34<small>건</small></span>
                    </div>
                    <div className="card-trend-right">
                        <span className="status-badge bg-green-light text-green">당월</span>
                    </div>
                </div>
                <div className="summary-card-item">
                    <div className="card-info-left">
                        <span className="summary-label">기한 달성률</span>
                        <span className="summary-value text-blue">95<small>%</small></span>
                    </div>
                    <div className="card-trend-right">
                        <span className="status-badge bg-blue-light text-blue">당월</span>
                    </div>
                </div>
                <div className="summary-card-item">
                    <div className="card-info-left">
                        <span className="summary-label">기한 임박 발생률</span>
                        <span className="summary-value text-orange">5<small>%</small></span>
                    </div>
                    <div className="card-trend-right">
                        <span className="status-badge bg-orange-light text-orange">당월</span>
                    </div>
                </div>
                <div className="summary-card-item">
                    <div className="card-info-left">
                        <span className="summary-label">기한 초과 발생률</span>
                        <span className="summary-value text-red">3<small>%</small></span>
                    </div>
                    <div className="card-trend-right">
                        <span className="status-badge bg-red-light text-red">당월</span>
                    </div>
                </div>
            </div>

            {/* 검색 필터 영역 */}
            <div className="filter-wrapper-card">
                <form className="search-filter-grid" onSubmit={(e) => e.preventDefault()}>
                    <div className="filter-group group-date-range">
                        <label>출고 기간</label>
                        <div className="date-range-container">
                            <input type="date" id="search_start_date" className="filter-control" />
                            <span className="date-separator">~</span>
                            <input type="date" id="search_end_date" className="filter-control" />
                        </div>
                    </div>
                    <div className="filter-group">
                        <label>매니페스트 번호 검색</label>
                        <div className="date-range-container">
                            <input type="text" id="search_order_number" className="filter-control" />
                        </div>
                    </div>
                    
                    <div className="filter-group">
                        <label>운송사 선택</label>
                        <div className="date-range-container">
                            <select id="search_transport" className="filter-control select-control">
                                <option value="">전체 운송사</option>
                                {transportList.map((transport) => (
                                    <option key={transport.id} value={transport.value}>
                                        {transport.label}
                                    </option>
                                ))}
                            </select>
                        </div>
                    </div>

                    <div className="filter-btn-group">
                        <button type="reset" className="btn-filter-reset">초기화</button>
                        <button type="submit" className="btn-filter-search">조회하기</button>
                    </div>
                </form>
            </div>

            {/* 메인 데이터 테이블 */}
            <div className="content-card">
                <div className="table-responsive">
                    <div className="inhistory-btn-group">
                        <button type="button" className="btn-filter-reset">엑셀 다운로드</button>
                        <button type="button" className="btn-filter-search">새로고침</button>
                    </div>
                    <table className="history-data-table">
                        <thead>
                            <tr>
                                <th>출고일자</th>
                                <th>매니페스트 번호</th>
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
                                        <td className="font-bold text-link" onClick={() => openDetailModal(item)}>
                                            {item.asnNo}
                                        </td>
                                        <td className="text-center">{item.company}</td>
                                        <td className="text-center">{item.boxQty} EA</td>
                                        <td className="text-center">
                                            <span className="status-badge bg-green-light text-green">{item.status}</span>
                                        </td>
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="5" className="text-center" style={{ padding: '2rem', color: '#94a3b8' }}>
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

            {/* 매니페스트 상세 정보 조회 모달 */}
            <div className={`modal-overlay ${isModalOpen ? 'active' : ''}`} id="asnDetailModal">
                <div className="modal-container modal-window">
                    <div className="modal-header">
                        <h3>출고 상세 정보 (매니페스트)</h3>
                        <button className="modal-close-btn" onClick={closeAsnDetailModal}>&times;</button>
                    </div>

                    <div className="modal-body">
                        <div className="modal-form-inline-grid">
                            <div className="form-group-item">
                                <label className="modal-form-label">매니페스트 번호</label>
                                <input type="text" className="table-inner-input disabled-input" readOnly value={selectedAsnNo} />
                            </div>
                            <div className="form-group-item">
                                <label className="modal-form-label">운송사</label>
                                <input type="text" className="table-inner-input disabled-input" readOnly value={modalData.transportCompany} />
                            </div>
                            <div className="form-group-item">
                                <label className="modal-form-label">진행상태</label>
                                <select className="table-inner-input disabled-input" disabled value={modalData.status}>
                                    <option value="출고완료">출고완료</option>
                                    <option value="배송중">배송중</option>
                                    <option value="배송완료">배송완료</option>
                                </select>
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
                                        </tr>
                                    </thead>
                                    <tbody id="detailAsnTableBody">
                                        {modalData.boxes && modalData.boxes.map((box, index) => (
                                            <tr key={index}>
                                                <td className="td-no text-center">{index + 1}</td>
                                                <td className="td-code font-bold">{box.boxNo}</td>
                                                {/* 변경된 컬럼 매핑: 제품명 및 송장번호 */}
                                                <td className="td-name">{box.productName}</td>
                                                <td className="td-qty text-center font-bold" style={{ color: '#2563eb' }}>
                                                    {box.trackingNo}
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>

                    <div className="modal-footer">
                        <button type="button" className="btn-pop-cancel" onClick={closeAsnDetailModal}>
                            닫기
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default OutHistory;