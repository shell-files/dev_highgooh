import '@styles/outbound.css';
import { useState, useEffect, useRef } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
    getOutboundList,
    getOutboundDetail,
    assignOutboundVehicle,
    issueOutboundInvoice,
    confirmOutboundShipment,
    setOutboundPage
} from '@/stores/outboundSlice.js';

const Outbound = () => {
    const dispatch = useDispatch();

    // --- 1. 컴포넌트 로컬 상태 관리 ---
    const [viewMode, setViewMode] = useState('box'); // 'box' | 'manifest'
    const [modalOpen, setModalOpen] = useState({
        invoice: false,
        vehicle: false,
        detail: false
    });

    // 체크박스 추적 (정수 ID 식별자 관리)
    const [checkedBoxes, setCheckedBoxes] = useState([]);         // 정수형 packingId
    const [checkedManifests, setCheckedManifests] = useState([]);     // 정수형 transportationId

    // 박스 테이블 제어용 운송사 고정 필터링 상태
    const [selectedCarrier, setSelectedCarrier] = useState(null);

    // 모달 내부 캐러셀 상태
    const [selectedInvoiceBox, setSelectedInvoiceBox] = useState(null);
    const [activeInvoiceBoxes, setActiveInvoiceBoxes] = useState([]); // 팝업 슬라이더용 packingId 배열
    const [currentSlide, setCurrentSlide] = useState(0);

    // 차량 배정용 폼 
    const [vehicleForm, setVehicleForm] = useState({
        carrierId: 1,
        vehicleId: 1,
        lpn: '',
        driver: '',
        etd: ''
    });

    // --- 2. Redux 상태 구독 (슬라이스 규격 정밀 매핑) ---
    const { loading, view = {}, detailData } = useSelector((state) => state.outbound || {});

    // 💡 크리티컬 포인트 해결: list를 쪼개지 말고, 슬라이스가 저장해 둔 전용 배열 상태를 직접 구독합니다.
    const {
        summary,
        boxList,
        manifestList,
        page,
        totalCount,
        totalPages,
        size = 20
    } = useSelector((state) => state.outbound.view);

    // 필터 제어용 useRef
    const orderNoRef = useRef();
    const customerRef = useRef();
    const stateCodeRef = useRef();

    // --- 3. 데이터 페칭 로직 ---
    const getData = () => {
        const filters = {
            page: page,
            size: size,
            viewMode: viewMode, // 'box' 또는 'manifest'
            orderNo: orderNoRef.current?.value || '',
            customer: customerRef.current?.value || '',
            stateCode: stateCodeRef.current?.value ? Number(stateCodeRef.current.value) : null
        };
        dispatch(getOutboundList(filters));
    };

    useEffect(() => {
        getData();
    }, [page, viewMode]);

    // 탭 전환 핸들러
    const handleViewModeChange = (mode) => {
        setViewMode(mode);
        setCheckedBoxes([]);
        setCheckedManifests([]);
        setSelectedCarrier(null);
        dispatch(setOutboundPage(1)); // 페이지 번호 초기화 및 데이터 리로드 유도
    };

    // --- 4. 변환 및 안전한 헬퍼 유틸 함수 ---
    const formatDeadline = (deadlineStr) => {
        if (!deadlineStr) return '-';
        const now = new Date();
        const target = new Date(deadlineStr);
        const diffMs = target - now;
        const diffMins = Math.ceil(diffMs / (1000 * 60));

        if (diffMins < 0) return `${Math.abs(diffMins)}분 초과`;
        return `${diffMins}분 남음`;
    };

    const getStatusStyle = (stateName) => {
        if (!stateName) return 'badge-dark'; // 💡 방어 코드 삽입
        switch (stateName) {
            case '출고대기': return 'text-blue bg-blue-light';
            case '차량배정': return 'badge-pending';
            case '미배정': return 'badge-dark';
            default: return 'badge-dark';
        }
    };

    // --- 5. 체크박스 핸들러 ---
    const handleBoxCheck = (packingId, carrierName) => {
        setCheckedBoxes(prev => {
            const isExist = prev.includes(packingId);
            let nextChecked;

            if (isExist) {
                nextChecked = prev.filter(id => id !== packingId);
                if (nextChecked.length === 0) setSelectedCarrier(null);
            } else {
                nextChecked = [...prev, packingId];
                if (prev.length === 0) setSelectedCarrier(carrierName);
            }
            return nextChecked;
        });
    };

    const handleManifestCheck = (transportationId) => {
        setCheckedManifests(prev =>
            prev.includes(transportationId)
                ? prev.filter(id => id !== transportationId)
                : [...prev, transportationId]
        );
    };

    // --- 6. 비동기 백엔드 트랜잭션 핸들러 ---
    const openVehicleModal = () => {
        if (viewMode !== 'box') return alert('박스 탭에서만 차량 배정이 가능합니다.');
        if (checkedBoxes.length === 0) return alert('미배정 박스를 선택해주세요.');
        setModalOpen({ ...modalOpen, vehicle: true });
    };

    const handleVehicleSubmit = async () => {
        const payload = {
            packingIds: checkedBoxes,
            ...vehicleForm
        };
        const result = await dispatch(assignOutboundVehicle(payload));
        if (result.meta.requestStatus === 'fulfilled') {
            setModalOpen({ ...modalOpen, vehicle: false });
            setCheckedBoxes([]);
            setSelectedCarrier(null);
            getData();
        }
    };

    const openInvoiceModal = (singleTransId = null) => {
        let linkedBoxes = [];

        if (viewMode === 'box') {
            if (checkedBoxes.length === 0) return alert('송장을 발행할 박스를 선택해주세요.');
            linkedBoxes = checkedBoxes;
        } else {
            let targetManifests = [];
            if (singleTransId) {
                targetManifests = [singleTransId];
            } else {
                if (checkedManifests.length === 0) return alert('송장을 발행할 매니페스트를 선택해주세요.');
                targetManifests = checkedManifests;
            }

            // 💡 boxList 상태를 안전하게 순회하여 연결된 상자를 추적합니다.
            linkedBoxes = boxList
                .filter(box => targetManifests.includes(box.transportationId))
                .map(box => box.packingId);

            if (linkedBoxes.length === 0) {
                return alert('선택한 매니페스트에 배정된 리얼 박스 데이터 매핑 정보를 찾을 수 없습니다.');
            }
        }

        setActiveInvoiceBoxes(linkedBoxes);
        setSelectedInvoiceBox(linkedBoxes[0]);
        setCurrentSlide(0);
        setModalOpen({ ...modalOpen, invoice: true });
    };

    const handleInvoiceSubmit = async () => {
        const result = await dispatch(issueOutboundInvoice({ packingIds: activeInvoiceBoxes }));
        if (result.meta.requestStatus === 'fulfilled') {
            setModalOpen({ ...modalOpen, invoice: false });
            setCheckedBoxes([]);
            setCheckedManifests([]);
            getData();
        }
    };

    const handleConfirmShipment = async () => {
        if (checkedManifests.length === 0) return alert('출고를 확정할 매니페스트를 선택해주세요.');
        if (window.confirm(`선택한 ${checkedManifests.length}건의 출고 공정을 최종 확정 승인하시겠습니까?`)) {
            const result = await dispatch(confirmOutboundShipment({ transportationIds: checkedManifests }));
            if (result.meta.requestStatus === 'fulfilled') {
                setCheckedManifests([]);
                getData();
            }
        }
    };

    const handleOrderDetailView = (outboundId) => {
        dispatch(getOutboundDetail(outboundId));
        setModalOpen({ ...modalOpen, detail: true });
    };

    // 캐러셀 유틸리티
    const handleListBoxClick = (packingId, index) => {
        setSelectedInvoiceBox(packingId);
        setCurrentSlide(index);
    };

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

    return (
        <div id="outbound-page">
            <div className="page-header-flex">
                <h2 className="page-title">출고/송장</h2>
            </div>

            {/* 통계 서머리 대시보드 */}
            <div className="order-summary-grid">
                <div className="summary-card-item">
                    <div className="card-info-left"><span className="summary-label">출고 예정</span><span className="summary-value">{summary.expectedToday}<small>건</small></span></div>
                    <div className="card-trend-right"><span className="status-badge bg-all-light text-muted">전체</span></div>
                </div>
                <div className="summary-card-item">
                    <div className="card-info-left"><span className="summary-label">출고 확정</span><span className="summary-value text-green">{summary.confirmedToday}<small>건</small></span></div>
                    <div className="card-trend-right"><span className="status-badge bg-green-light text-green">전체</span></div>
                </div>
                <div className="summary-card-item">
                    <div className="card-info-left"><span className="summary-label">기한 임박</span><span className="summary-value text-orange">{summary.nearDeadline}<small>건</small></span></div>
                    <div className="card-trend-right"><span className="status-badge bg-orange-light text-orange">전체</span></div>
                </div>
                <div className="summary-card-item">
                    <div className="card-info-left"><span className="summary-label">기한 초과</span><span className="summary-value text-red">{summary.overdue}<small>건</small></span></div>
                    <div className="card-trend-right"><span className="status-badge bg-red-light text-red">전체</span></div>
                </div>
                <div className="summary-card-item">
                    <div className="card-info-left"><span className="summary-label">미배정</span><span className="summary-value text-dark">{summary.unassigned}<small>건</small></span></div>
                    <div className="card-trend-right"><span className="status-badge table-badge badge-dark">전체</span></div>
                </div>
            </div>

            {/* 통합 필터 바 */}
            <div className="filter-wrapper-card">
                <form className="search-filter-grid" onSubmit={(e) => { e.preventDefault(); dispatch(setOutboundPage(1)); getData(); }}>
                    <div className="filter-group"><label>주문번호 검색</label><input type="text" ref={orderNoRef} className="filter-control" /></div>
                    <div className="filter-group"><label>고객사 검색</label><input type="text" ref={customerRef} className="filter-control" /></div>
                    <div className="filter-group">
                        <label>진행 상태</label>
                        <select ref={stateCodeRef} className="filter-control">
                            <option value="">전체 상태</option>
                            <option value="10">미배정</option>
                            <option value="21">차량배정</option>
                            <option value="22">출고대기</option>
                        </select>
                    </div>
                    <div className="filter-btn-group">
                        <button type="reset" className="btn-filter-reset" onClick={() => dispatch(setOutboundPage(1))}>초기화</button>
                        <button type="submit" className="btn-filter-search">조회하기</button>
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
                                <button className="btn-action-green" onClick={handleConfirmShipment}>출고 확정</button>
                            </>
                        )}
                    </div>
                </div>

                {/* 그리드 데이터 테이블 구역 */}
                <div className="table-responsive">
                    {loading ? (
                        <div className="loading-container">데이터 연동 중...</div>
                    ) : viewMode === 'box' ? (
                        <table className="order-data-table">
                            <thead>
                                <tr>
                                    <th className="table-header-checkbox"></th>
                                    <th>기한</th>
                                    <th>박스번호</th>
                                    <th>주문번호</th>
                                    <th>고객사</th>
                                    <th>운송사</th>
                                    <th>상태</th>
                                    <th>송장번호</th>
                                </tr>
                            </thead>
                            <tbody>
                                {boxList.map((item) => {
                                    const isCarrierDisabled = selectedCarrier !== null && selectedCarrier !== item.carrierName;
                                    const isDisabled = item.transportationId !== null || isCarrierDisabled;

                                    return (
                                        <tr key={item.packingId}>
                                            <td className="text-center">
                                                <input
                                                    type="checkbox"
                                                    className="box-check"
                                                    disabled={isDisabled}
                                                    onChange={() => handleBoxCheck(item.packingId, item.carrierName)}
                                                    checked={checkedBoxes.includes(item.packingId)}
                                                />
                                            </td>
                                            <td className="text-center">{formatDeadline(item.deadline)}</td>
                                            <td className="text-center font-bold text-green">{item.packingInvoiceNumber}</td>
                                            <td className="text-center text-link" onClick={() => handleOrderDetailView(item.outboundId)}>{item.outboundCode}</td>
                                            <td>{item.customerName}</td>
                                            <td>{item.carrierName}</td>
                                            <td className="text-center">
                                                <span className={`table-badge ${getStatusStyle(item.stateName)}`}>
                                                    {item.stateName}
                                                </span>
                                            </td>
                                            <td className="text-center">{item.invoiceNumber || '-'}</td>
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
                                    <th>매니페스트번호</th>
                                    <th>운송사</th>
                                    <th>차량</th>
                                    <th>박스 수</th>
                                    <th>상태</th>
                                </tr>
                            </thead>
                            <tbody>
                                {manifestList.map(item => (
                                    <tr key={item.transportationId}>
                                        <td className="text-center">
                                            <input
                                                type="checkbox"
                                                className="manifest-check"
                                                disabled={item.stateName !== '차량배정'}
                                                onChange={() => handleManifestCheck(item.transportationId)}
                                                checked={checkedManifests.includes(item.transportationId)}
                                            />
                                        </td>
                                        <td className="text-center font-bold text-link manifest-no-link" onClick={() => openInvoiceModal(item.transportationId)}>
                                            {item.manifestNo}
                                        </td>
                                        <td>{item.carrierName}</td>
                                        <td>{item.vehicleInfo || '-'}</td>
                                        <td className="text-center">{item.boxCount} BOX</td>
                                        <td className="text-center">
                                            <span className={`table-badge ${getStatusStyle(item.stateName)}`}>
                                                {item.stateName}
                                            </span>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    )}
                </div>

                {/* 페이지네이션 인터페이스 구역 */}
                <div className="pagination-container">
                    <div className="pagination-info">전체 <span>{totalCount}</span>건</div>
                    <div className="pagination-buttons">
                        <button className="btn-page first" onClick={() => dispatch(setOutboundPage(1))} disabled={page <= 1}>&laquo;</button>
                        <button className="btn-page prev" onClick={() => dispatch(setOutboundPage(page - 1))} disabled={page <= 1}>&lsaquo;</button>
                        {Array.from({ length: totalPages }, (_, i) => i + 1).map(index => (
                            <button
                                key={index}
                                className={page === index ? 'btn-page-num active' : 'btn-page-num'}
                                onClick={() => dispatch(setOutboundPage(index))}
                            >
                                {index}
                            </button>
                        ))}
                        <button className="btn-page next" onClick={() => dispatch(setOutboundPage(page + 1))} disabled={page === totalPages}>&rsaquo;</button>
                        <button className="btn-page last" onClick={() => dispatch(setOutboundPage(totalPages))} disabled={page === totalPages}>&raquo;</button>
                    </div>
                </div>
            </div>

            {/* --- 차량 배정 모달 --- */}
            {modalOpen.vehicle && (
                <div className="modal-overlay active">
                    <div className="modal-container vehicle-modal-container">
                        <div className="modal-header">
                            <h3>차량 배정 신청</h3>
                            <button type="button" className="modal-close-btn" onClick={() => setModalOpen({ ...modalOpen, vehicle: false })}>&times;</button>
                        </div>
                        <div className="modal-body">
                            <div className="modal-form-grid">
                                <div className="form-group">
                                    <label>선택된 박스 수</label>
                                    <input type="text" className="modal-input" value={`${checkedBoxes.length} 건`} disabled />
                                </div>
                                <div className="form-group">
                                    <label>지정 운송사</label>
                                    <input type="text" className="modal-input" value={selectedCarrier || ''} disabled />
                                </div>
                                <div className="form-group">
                                    <label>차량 번호</label>
                                    <input type="text" className="modal-input" value={vehicleForm.lpn} onChange={(e) => setVehicleForm({ ...vehicleForm, lpn: e.target.value })} placeholder="예: 11가 1234" />
                                </div>
                                <div className="form-group">
                                    <label>운전자 명</label>
                                    <input type="text" className="modal-input" value={vehicleForm.driver} onChange={(e) => setVehicleForm({ ...vehicleForm, driver: e.target.value })} placeholder="홍길동 기사님" />
                                </div>
                            </div>
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn-pop-cancel" onClick={() => setModalOpen({ ...modalOpen, vehicle: false })}>취소</button>
                            <button type="button" className="btn-pop-submit" onClick={handleVehicleSubmit}>배정 확정</button>
                        </div>
                    </div>
                </div>
            )}

            {/* --- 송장 발급 모달 --- */}
            {modalOpen.invoice && (
                <div className="modal-overlay active">
                    <div className="modal-container invoice-modal-container">
                        <div className="modal-header">
                            <h3>송장 발급 관리 ({viewMode === 'manifest' ? '매니페스트 연동' : '개별박스 선택'})</h3>
                            <button type="button" className="modal-close-btn" onClick={() => setModalOpen({ ...modalOpen, invoice: false })}>&times;</button>
                        </div>
                        <div className="modal-body">
                            <div className="invoice-manager-layout">
                                <div className="invoice-box-list">
                                    <div className="invoice-list-title">대상 식별 ID 목록 ({activeInvoiceBoxes.length}건)</div>
                                    <ul id="invoiceBoxList">
                                        {activeInvoiceBoxes.map((packingId, index) => (
                                            <li
                                                key={packingId}
                                                className={`invoice-list-item ${selectedInvoiceBox === packingId ? 'active' : ''}`}
                                                onClick={() => handleListBoxClick(packingId, index)}
                                            >
                                                ID: {packingId}
                                            </li>
                                        ))}
                                    </ul>
                                </div>

                                <div className="invoice-preview-panel slider-mode">
                                    <div className="slider-controls">
                                        <button type="button" className="btn-slide-nav" onClick={handlePrevSlide} disabled={currentSlide === 0}>이전</button>
                                        <span className="slide-indicator"><strong>{currentSlide + 1}</strong> / {activeInvoiceBoxes.length}</span>
                                        <button type="button" className="btn-slide-nav" onClick={handleNextSlide} disabled={currentSlide === activeInvoiceBoxes.length - 1}>다음</button>
                                    </div>

                                    <div className="slider-viewport slider-viewport-window">
                                        <div className="slider-track slider-track-animate" style={{ transform: `translateX(-${currentSlide * 100}%)` }}>
                                            {activeInvoiceBoxes.map((packingId) => (
                                                <div className="slide-item slide-item-card" key={`slide-${packingId}`}>
                                                    <div className="invoice-preview-card invoice-card-centered">
                                                        <div className="invoice-box-inner">
                                                            <div className="invoice-header-title">택배 운송장 (공정 규격)</div>
                                                            <table className="invoice-mini-table">
                                                                <tbody>
                                                                    <tr><th>패킹식별ID</th><td>{packingId}</td></tr>
                                                                    <tr><th>도착지코드</th><td>SEOUL-MAIN</td></tr>
                                                                    <tr><th>발송인</th><td>통합 물류 인프라 센터</td></tr>
                                                                    <tr><th>특이사항</th><td>정상 출고 공정 승인 완료건</td></tr>
                                                                </tbody>
                                                            </table>
                                                            <div className="barcode-wrapper">
                                                                <div className="barcode-lines">
                                                                    <span className="b-w-1"></span><span className="b-w-2"></span><span className="b-w-3"></span>
                                                                    <span className="b-w-4"></span><span className="b-w-1"></span>
                                                                </div>
                                                                <div className="barcode-text">*PK-{packingId}*</div>
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
                            <button type="button" className="btn-main-action" onClick={handleInvoiceSubmit}>출력 마감 승인</button>
                            <button type="button" className="btn-pop-cancel" onClick={() => setModalOpen({ ...modalOpen, invoice: false })}>취소</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Outbound;