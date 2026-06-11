import '@styles/outbound.css';
import { useState, useEffect } from 'react';

const Outbound = () => {
    // --- 1. 상태 관리 ---
    const [viewMode, setViewMode] = useState('box'); // 'box' | 'manifest'
    const [modalOpen, setModalOpen] = useState({
        invoice: false,
        vehicle: false,
        detail: false
    });

    // 메인 데이터 상태 State
    const [boxData, setBoxData] = useState([]);
    const [manifestData, setManifestData] = useState([]);
    const [mockOrderDetails, setMockOrderDetails] = useState({});
    const [isLoading, setIsLoading] = useState(true);

    // 💡 Asn.jsx 스타일의 페이지네이션 상태 추가
    const [page, setPage] = useState(1);
    const [size] = useState(20); // 기본 페이지당 노출 건수 설정

    // 체크박스 선택 상태 관리 (박스용 / 매니페스트용 분리)
    const [checkedBoxes, setCheckedBoxes] = useState([]);
    const [checkedManifests, setCheckedManifests] = useState([]);

    // 💡 박스 테이블에서 처음 선택된 운송사를 저장할 상태 변수
    const [selectedCarrier, setSelectedCarrier] = useState(null);

    // 프로세스 흐름 제어 상태
    const [processStatus, setProcessStatus] = useState({
        vehicleAssigned: false,
        invoiceIssued: false
    });

    // 모달 활성화 컨텍스트 컨테이너
    const [selectedDetail, setSelectedDetail] = useState(null);
    const [selectedInvoiceBox, setSelectedInvoiceBox] = useState(null);
    const [activeInvoiceBoxes, setActiveInvoiceBoxes] = useState([]); // 현재 팝업에 출력할 박스 목록
    
    // 💡 송장 슬라이더 제어를 위한 현재 인덱스 상태 추가
    const [currentSlide, setCurrentSlide] = useState(0);

    // --- 2. useEffect 기반 로딩 및 데이터 관계 매핑 ---
    useEffect(() => {
        const fetchOutboundData = async () => {
            try {
                setIsLoading(true);

                const dummyBoxes = [
                    { id: 1, due: '17분 남음', boxNo: 'BOX-20260604-001', orderNo: 'PO-20260602-X01', customer: '삼성전자', carrier: 'CJ대한통운', status: '출고대기', statusClass: 'text-blue bg-blue-light', invNo: 'IVC-20260604-001', time: '16:30', selectable: false, manifestNo: 'MNF-20260604-001' },
                    { id: 2, due: '5분 남음', boxNo: 'BOX-20260604-002', orderNo: 'PO-20260601-M04', customer: 'LG전자', carrier: '경동택배', status: '차량배정', statusClass: 'badge-pending', invNo: 'IVC-20260604-002', time: '16:20', selectable: false, manifestNo: 'MNF-20260604-002' },
                    { id: 2, due: '5분 남음', boxNo: 'BOX-20260604-003', orderNo: 'PO-20260601-M04', customer: 'LG전자', carrier: '경동택배', status: '차량배정', statusClass: 'badge-pending', invNo: 'IVC-20260604-002', time: '16:20', selectable: false, manifestNo: 'MNF-20260604-002' },
                    { id: 2, due: '5분 남음', boxNo: 'BOX-20260604-004', orderNo: 'PO-20260601-M04', customer: 'LG전자', carrier: '경동택배', status: '차량배정', statusClass: 'badge-pending', invNo: 'IVC-20260604-002', time: '16:20', selectable: false, manifestNo: 'MNF-20260604-002' },
                    { id: 2, due: '5분 남음', boxNo: 'BOX-20260604-005', orderNo: 'PO-20260601-M04', customer: 'LG전자', carrier: '경동택배', status: '차량배정', statusClass: 'badge-pending', invNo: 'IVC-20260604-002', time: '16:20', selectable: false, manifestNo: 'MNF-20260604-002' },
                    { id: 2, due: '5분 남음', boxNo: 'BOX-20260604-006', orderNo: 'PO-20260601-M04', customer: 'LG전자', carrier: '경동택배', status: '차량배정', statusClass: 'badge-pending', invNo: 'IVC-20260604-002', time: '16:20', selectable: false, manifestNo: 'MNF-20260604-003' },
                    { id: 2, due: '5분 남음', boxNo: 'BOX-20260604-007', orderNo: 'PO-20260601-M04', customer: 'LG전자', carrier: '경동택배', status: '차량배정', statusClass: 'badge-pending', invNo: 'IVC-20260604-002', time: '16:20', selectable: false, manifestNo: 'MNF-20260604-003' },
                    { id: 3, due: '5분 남음', boxNo: 'BOX-20260604-008', orderNo: 'PO-20260602-X01', customer: 'LG전자', carrier: '경동택배', status: '미배정', statusClass: 'badge-dark', invNo: '', time: '16:20', selectable: true, manifestNo: null },
                    { id: 4, due: '5분 남음', boxNo: 'BOX-20260604-009', orderNo: 'PO-20260601-M04', customer: 'LG전자', carrier: '경동택배', status: '미배정', statusClass: 'badge-dark', invNo: '', time: '16:20', selectable: true, manifestNo: null },
                    { id: 5, due: '5분 남음', boxNo: 'BOX-20260604-010', orderNo: 'PO-20260601-M04', customer: 'LG전자', carrier: 'CJ대한통운', status: '미배정', statusClass: 'badge-dark', invNo: '', time: '16:20', selectable: true, manifestNo: null },
                    { id: 6, due: '5분 남음', boxNo: 'BOX-20260604-011', orderNo: 'PO-20260601-M04', customer: 'LG전자', carrier: 'CJ대한통운', status: '미배정', statusClass: 'badge-dark', invNo: '', time: '16:20', selectable: true, manifestNo: null },
                ];

                const dummyManifests = [
                    { id: 1, due: '17분 남음', mnfNo: 'MNF-20260604-001', carrier: 'CJ대한통운', vehicle: '11가 1234 (1톤 탑차)', boxes: '17 BOX', status: '출고대기', statusClass: 'text-blue bg-blue-light', time: '16:30' },
                    { id: 2, due: '15분 초과', mnfNo: 'MNF-20260604-002', carrier: '경동택배', vehicle: '22나 5678 (5톤 카고)', boxes: '1 BOX', status: '차량배정', statusClass: 'text-red bg-red-light', time: '16:00' },
                    { id: 3, due: '15분 초과', mnfNo: 'MNF-20260604-003', carrier: '경동택배', vehicle: '22나 5678 (5톤 카고)', boxes: '1 BOX', status: '차량배정', statusClass: 'text-red bg-red-light', time: '16:00' },
                ];

                const dummyOrderDetails = {
                    'PO-20260602-X01': {
                        customer: '(주)한성자재마트', status: '처리중',
                        products: [
                            { name: 'Al 시트레일 압출재 (6063-T5)', qty: 1200, price: 15000000, invoiceNo: 'IVC-20260602-001A', boxQty: 12 },
                            { name: '알루미늄 플레이트 (5052)', qty: 450, price: 6000000, invoiceNo: 'IVC-20260602-001B', boxQty: 5 }
                        ]
                    },
                    'PO-20260601-M04': {
                        customer: '대한알루미늄공업', status: '신규',
                        products: [
                            { name: 'Al 시트레일 압출재 (6063-T5)', qty: 1000, price: 12500000, invoiceNo: 'IVC-20260601-004A', boxQty: 10 },
                            { name: '조립용 고정 볼트 (M6)', qty: 5000, price: 5350000, invoiceNo: 'IVC-20260601-004B', boxQty: 2 }
                        ]
                    }
                };

                setBoxData(dummyBoxes);
                setManifestData(dummyManifests);
                setMockOrderDetails(dummyOrderDetails);
            } catch (error) {
                console.error("데이터 로드 실패:", error);
            } finally {
                setIsLoading(false);
            }
        };

        fetchOutboundData();
    }, [page]); // 💡 페이지가 변경될 때마다 데이터를 다시 불러올 수 있도록 의존성 배열 추가

    // 💡 현재 뷰모드에 따라 전체 데이터 수와 전체 페이지 수를 동적으로 계산
    const currentTotalCount = viewMode === 'box' ? boxData.length : manifestData.length;
    const totalPages = Math.ceil(currentTotalCount / size) || 1;

    // 💡 탭(뷰모드) 전환 시 페이지를 1페이지로 초기화
    const handleViewModeChange = (mode) => {
        setViewMode(mode);
        setPage(1);
    };

    // --- 3. 이벤트 핸들러 명세 ---
    
    const handleBoxCheck = (boxNo, carrier) => {
        setCheckedBoxes(prev => {
            const isExist = prev.includes(boxNo);
            let nextChecked;
            
            if (isExist) {
                nextChecked = prev.filter(b => b !== boxNo);
                if (nextChecked.length === 0) {
                    setSelectedCarrier(null);
                }
            } else {
                nextChecked = [...prev, boxNo];
                if (prev.length === 0) {
                    setSelectedCarrier(carrier);
                }
            }
            return nextChecked;
        });
    };

    const handleManifestCheck = (mnfNo) => {
        setCheckedManifests(prev => prev.includes(mnfNo) ? prev.filter(m => m !== mnfNo) : [...prev, mnfNo]);
    };

    const openVehicleModal = () => {
        if (viewMode !== 'box') return alert('박스 탭에서만 차량 배정이 가능합니다.');
        if (checkedBoxes.length === 0) return alert('미배정 박스를 선택해주세요.');
        setModalOpen({ ...modalOpen, vehicle: true });
    };

    const openInvoiceModal = (singleMnfNo = null) => {
        let linkedBoxes = [];

        if (viewMode === 'box') {
            if (checkedBoxes.length === 0) return alert('송장을 발행할 박스를 선택해주세요.');
            linkedBoxes = checkedBoxes;
        } 
        else {
            let targetManifests = [];

            if (singleMnfNo && typeof singleMnfNo === 'string') {
                targetManifests = [singleMnfNo];
            } else {
                if (checkedManifests.length === 0) return alert('송장을 발행할 매니페스트를 선택해주세요.');
                targetManifests = checkedManifests;
            }
            
            linkedBoxes = boxData
                .filter(box => targetManifests.includes(box.manifestNo))
                .map(box => box.boxNo);
            
            if (linkedBoxes.length === 0) {
                return alert('선택한 매니페스트에 연동된 박스 데이터가 존재하지 않습니다.');
            }
        }
        
        setActiveInvoiceBoxes(linkedBoxes);
        setSelectedInvoiceBox(linkedBoxes[0]);
        setCurrentSlide(0); // 💡 모달이 열릴 때 첫 번째 슬라이드로 초기화
        setModalOpen({ ...modalOpen, invoice: true });
    };

    // 💡 리스트 클릭 시 해당 슬라이드로 이동하는 핸들러
    const handleListBoxClick = (boxNo, index) => {
        setSelectedInvoiceBox(boxNo);
        setCurrentSlide(index);
    };

    // 💡 슬라이더 네비게이션 핸들러
    const handlePrevSlide = () => {
        if (currentSlide > 0) {
            const nextIdx = currentSlide - 1;
            setCurrentSlide(nextIdx);
            setSelectedInvoiceBox(activeInvoiceBoxes[nextIdx]);
        }
    };

    const handleNextSlide = () => {
        if (currentSlide < activeInvoiceBoxes.length - 1) {
            const nextIdx = currentSlide + 1;
            setCurrentSlide(nextIdx);
            setSelectedInvoiceBox(activeInvoiceBoxes[nextIdx]);
        }
    };

    if (isLoading) {
        return <div className="loading-container">데이터 연동 중...</div>;
    }

    return (
        <div id="outbound-page">
            <div className="page-header-flex">
                <h2 className="page-title">출고/송장</h2>
            </div>

            {/* 통계 서머리 대시보드 */}
            <div className="order-summary-grid">
                <div className="summary-card-item">
                    <div className="card-info-left"><span className="summary-label">출고 예정</span><span className="summary-value">42<small>건</small></span></div>
                    <div className="card-trend-right"><span className="status-badge bg-all-light text-muted">전체</span></div>
                </div>
                <div className="summary-card-item">
                    <div className="card-info-left"><span className="summary-label">출고 확정</span><span className="summary-value text-green">34<small>건</small></span></div>
                    <div className="card-trend-right"><span className="status-badge bg-green-light text-green">전체</span></div>
                </div>
                <div className="summary-card-item">
                    <div className="card-info-left"><span className="summary-label">기한 임박</span><span className="summary-value text-orange">5<small>건</small></span></div>
                    <div className="card-trend-right"><span className="status-badge bg-orange-light text-orange">전체</span></div>
                </div>
                <div className="summary-card-item">
                    <div className="card-info-left"><span className="summary-label">기한 초과</span><span className="summary-value text-red">3<small>건</small></span></div>
                    <div className="card-trend-right"><span className="status-badge bg-red-light text-red">전체</span></div>
                </div>
                <div className="summary-card-item">
                    <div className="card-info-left"><span className="summary-label">미배정</span><span className="summary-value text-dark">3<small>건</small></span></div>
                    <div className="card-trend-right"><span className="status-badge table-badge badge-dark">전체</span></div>
                </div>
            </div>

            {/* 통합 필터 바 */}
            <div className="filter-wrapper-card">
                <form className="search-filter-grid">
                    <div className="filter-group"><label>주문번호 검색</label><input type="text" className="filter-control" /></div>
                    <div className="filter-group"><label>고객사 검색</label><input type="text" className="filter-control" /></div>
                    <div className="filter-group">
                        <label>진행 상태</label>
                        <select className="filter-control">
                            <option value="">전체 상태</option>
                            <option value="approved">미배정</option>
                            <option value="pending">차량배정</option>
                            <option value="rejected">출고대기</option>
                        </select>
                    </div>
                    <div className="filter-btn-group">
                        <button type="reset" className="btn-filter-reset">초기화</button>
                        <button type="button" className="btn-filter-search">조회하기</button>
                    </div>
                </form>
            </div>

            {/* 메인 콘텐츠 카드 */}
            <div className="content-card">
                <div className="view-toggle-container">
                    <div className="toggle-left">
                        <button className={`btn-toggle-view ${viewMode === 'box' ? 'active' : ''}`} onClick={() => handleViewModeChange('box')}>박스</button>
                        <button className={`btn-toggle-view ${viewMode === 'manifest' ? 'active' : ''}`} onClick={() => handleViewModeChange('manifest')}>매니페스트</button>
                    </div>
                    
                    <div className="toggle-right">
                        {viewMode === 'box' && (
                            <button className="btn-action-gray" onClick={openVehicleModal}>차량 배정</button>
                        )}
                        {viewMode === 'manifest' && (
                            <>
                                <button className="btn-action-blue" onClick={() => openInvoiceModal()}>송장 출력</button>
                                <button className="btn-action-green" onClick={() => alert('출고 처리가 확정 처리되었습니다.')}>출고 확정</button>
                            </>
                        )}
                    </div>
                </div>

                {/* 그리드 데이터 테이블 구역 */}
                <div className="table-responsive">
                    {viewMode === 'box' ? (
                        <table className="order-data-table">
                            <thead>
                                <tr>
                                    <th className="table-header-checkbox"></th>
                                    <th>기한</th>
                                    <th>박스번호</th>
                                    <th>주문번호</th>
                                    <th>발주처</th>
                                    <th>운송사</th>
                                    <th>상태</th>
                                    <th>송장번호</th>
                                    {/* <th>예상출발시간</th> */}
                                </tr>
                            </thead>
                            <tbody>
                                {boxData.map((item, idx) => {
                                    const isCarrierDisabled = selectedCarrier !== null && selectedCarrier !== item.carrier;
                                    const isDisabled = !item.selectable || isCarrierDisabled;

                                    return (
                                        <tr key={`${item.id}-${idx}`}>
                                            <td className="text-center">
                                                <input 
                                                    type="checkbox" 
                                                    className="box-check" 
                                                    disabled={isDisabled} 
                                                    onChange={() => handleBoxCheck(item.boxNo, item.carrier)} 
                                                    checked={checkedBoxes.includes(item.boxNo)} 
                                                />
                                            </td>
                                            <td className={`text-center ${item.due.includes('초과') || item.due.includes('5분') ? 'text-red' : ''}`}>{item.due}</td>
                                            <td className="text-center font-bold text-green">{item.boxNo}</td>
                                            <td className="text-center">{item.orderNo}</td>
                                            <td>{item.customer}</td>
                                            <td>{item.carrier}</td>
                                            <td className="text-center"><span className={`table-badge ${item.statusClass}`}>{item.status}</span></td>
                                            <td className="text-center">{item.invNo || '-'}</td>
                                            {/* <td className="text-center">{item.time}</td> */}
                                        </tr>
                                    );
                                })}
                            </tbody>
                        </table>
                    ) : (
                        <table className="order-data-table">
                            <thead>
                                <tr>
                                    <th className="table-header-checkbox"></th>
                                    <th>기한</th>
                                    <th>매니페스트번호</th>
                                    <th>운송사</th>
                                    <th>차량</th>
                                    <th>박스</th>
                                    <th>상태</th>
                                    {/* <th>예상출발시간</th> */}
                                </tr>
                            </thead>
                            <tbody>
                                {manifestData.map(item => (
                                    <tr key={item.id}>
                                        <td className="text-center">
                                            <input 
                                                type="checkbox" 
                                                className="manifest-check" 
                                                disabled={item.status !== '차량배정'} 
                                                onChange={() => handleManifestCheck(item.mnfNo)} 
                                                checked={checkedManifests.includes(item.mnfNo)} 
                                            />
                                        </td>
                                        <td className={`text-center ${item.due.includes('초과') ? 'text-red' : ''}`}>{item.due}</td>
                                        <td className="text-center font-bold text-link manifest-no-link" onClick={() => openInvoiceModal(item.mnfNo)}>
                                            {item.mnfNo}
                                        </td>
                                        <td>{item.carrier}</td>
                                        <td>{item.vehicle}</td>
                                        <td className="text-center">{item.boxes}</td>
                                        <td className="text-center"><span className={`table-badge ${item.statusClass}`}>{item.status}</span></td>
                                        {/* <td className="text-center">{item.time}</td> */}
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    )}
                </div>

                {/* 💡 Asn.jsx 스타일로 변경된 페이지네이션 인터페이스 구역 */}
                <div className="pagination-container">
                    <div className="pagination-info">전체 <span>{currentTotalCount}</span>건</div>
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
                </div>
            </div>

            {/* --- 1. 차량 배정 모달 --- */}
            {modalOpen.vehicle && (
                <div className="modal-overlay active">
                    <div className="modal-container vehicle-modal-container">
                        <div className="modal-header">
                            <h3>차량 배정 신청</h3>
                            <button type="button" className="modal-close-btn" onClick={() => setModalOpen({ ...modalOpen, vehicle: false })}>&times;</button>
                        </div>
                        <div className="modal-body">
                            <div className="modal-form-grid">
                                <div className="form-group"><label>운송사</label><select className="modal-input"><option>CJ대한통운</option><option>경동택배</option></select></div>
                                <div className="form-group"><label>차량</label><select className="modal-input"><option>1톤 탑차</option><option>5톤 카고</option></select></div>
                                {/* <div className="form-group col-span-2"><label>예상 출발 일시</label><input type="datetime-local" className="modal-input" /></div> */}
                            </div>
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn-pop-cancel" onClick={() => setModalOpen({ ...modalOpen, vehicle: false })}>취소</button>
                            <button type="button" className="btn-pop-submit" onClick={() => { setProcessStatus({ ...processStatus, vehicleAssigned: true }); setModalOpen({ ...modalOpen, vehicle: false }); alert('차량 배정이 완료되었습니다.'); }}>저장</button>
                        </div>
                    </div>
                </div>
            )}

            {/* --- 2. 송장 발급 모달 (슬라이더 고도화) --- */}
            {modalOpen.invoice && (
                <div className="modal-overlay active">
                    <div className="modal-container invoice-modal-container">
                        <div className="modal-header">
                            <h3>송장 발급 관리 ({viewMode === 'manifest' ? '매니페스트 연동' : '개별박스 선택'})</h3>
                            <button type="button" className="modal-close-btn" onClick={() => setModalOpen({ ...modalOpen, invoice: false })}>&times;</button>
                        </div>
                        <div className="modal-body">
                            <div className="invoice-manager-layout">
                                {/* 왼쪽 박스 목록 구역 */}
                                <div className="invoice-box-list">
                                    <div className="invoice-list-title">대상 박스 목록 ({activeInvoiceBoxes.length}건)</div>
                                    <ul id="invoiceBoxList">
                                        {activeInvoiceBoxes.map((boxNo, index) => (
                                            <li 
                                                key={boxNo} 
                                                className={`invoice-list-item ${selectedInvoiceBox === boxNo ? 'active' : ''}`} 
                                                onClick={() => handleListBoxClick(boxNo, index)}
                                            >
                                                {boxNo}
                                            </li>
                                        ))}
                                    </ul>
                                </div>
                                
                                {/* 오른쪽 송장 캐러셀 슬라이더 구역 */}
                                <div className="invoice-preview-panel slider-mode">
                                    {/* 슬라이더 제어 상단 컨트롤러 */}
                                    <div className="slider-controls">
                                        <button 
                                            type="button" 
                                            className="btn-slide-nav" 
                                            onClick={handlePrevSlide} 
                                            disabled={currentSlide === 0}
                                        >
                                            이전
                                        </button>
                                        <span className="slide-indicator">
                                            <strong>{currentSlide + 1}</strong> / {activeInvoiceBoxes.length}
                                        </span>
                                        <button 
                                            type="button" 
                                            className="btn-slide-nav" 
                                            onClick={handleNextSlide} 
                                            disabled={currentSlide === activeInvoiceBoxes.length - 1}
                                        >
                                            다음 
                                        </button>
                                    </div>

                                    {/* 슬라이드 윈도우 뷰포트 */}
                                    <div className="slider-viewport slider-viewport-window">
                                        <div 
                                            className="slider-track slider-track-animate" 
                                            style={{ transform: `translateX(-${currentSlide * 100}%)` }}
                                        >
                                            {activeInvoiceBoxes.map((boxNo) => (
                                                <div className="slide-item slide-item-card" key={`slide-${boxNo}`}>
                                                    <div className="invoice-preview-card invoice-card-centered">
                                                        <div className="invoice-box-inner">
                                                            <div className="invoice-header-title">택배 운송장</div>
                                                            <table className="invoice-mini-table">
                                                                <tbody>
                                                                    <tr><th>운송장번호</th><td>{`INV-${boxNo.split('-')[2]}`}</td></tr>
                                                                    <tr><th>도착지코드</th><td>SEOUL-001</td></tr>
                                                                    <tr><th>발송인</th><td>하이고 물류센터</td></tr>
                                                                    <tr><th>수취인</th><td>{`${boxNo} 고객 사내 수취인`}</td></tr>
                                                                    <tr><th>중량 (kg)</th><td>5.0 kg</td></tr>
                                                                </tbody>
                                                            </table>
                                                            <div className="barcode-wrapper">
                                                                <div className="barcode-lines">
                                                                    <span className="b-w-1"></span><span className="b-w-2"></span><span className="b-w-3"></span>
                                                                    <span className="b-w-4"></span><span className="b-w-1"></span>
                                                                </div>
                                                                <div className="barcode-text">*{boxNo}*</div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            ))}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn-main-action" onClick={() => alert(`총 ${activeInvoiceBoxes.length}건의 송장 인쇄 공정을 진행합니다.`)}>전체 인쇄</button>
                            <button type="button" className="btn-pop-cancel" onClick={() => setModalOpen({ ...modalOpen, invoice: false })}>취소</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Outbound;