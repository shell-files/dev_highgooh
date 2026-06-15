import '@styles/outbound.css';
import { useState, useEffect, useRef } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
    getOutboundList,
    getOutboundManifestList,
    getOutboundDetail,
    getOutboundFormData,
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
    const { loading, view = {}, detailData, carriers = [], vehicles = [] }
        = useSelector(state => state.outbound);

    // 💡 크리티컬 포인트 해결: list를 쪼개지 말고, 슬라이스가 저장해 둔 전용 배열 상태를 직접 구독합니다.
    const {
        summary = {},
        boxList = [],
        manifestList = [],
        page = 1,
        totalCount = 0,
        totalPages = 1,
        size = 20
    } = useSelector((state) => state.outbound.view || {});

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
        console.log(filters)
        if (viewMode === 'box') {
            dispatch(getOutboundList(filters));
        } else {
            dispatch(getOutboundManifestList(filters));
        }
    };

    useEffect(() => {
        getData();
    }, [page, viewMode]);

    useEffect(() => {
        dispatch(getOutboundFormData());
    }, [dispatch]);

    // useEffect(() => {
    //     console.log("boxList", boxList);
    // }, [boxList]);

    // useEffect(() => {
    //     console.log("manifestList", manifestList);
    // }, [manifestList]);

    // 탭 전환 핸들러
    const handleViewModeChange = (mode) => {
        setViewMode(mode);
        setCheckedBoxes([]);
        setCheckedManifests([]);
        setSelectedCarrier(null);
        dispatch(setOutboundPage(1)); // 페이지 번호 초기화 및 데이터 리로드 유도
    };

    // --- 4. 변환 및 안전한 헬퍼 유틸 함수 ---
    // Outbound.jsx 내부의 formatDeadline 함수를 이렇게 직관적으로 수정해 보세요!
    const formatDeadline = (deadlineStr) => {
        if (!deadlineStr) return '-';

        // 백엔드에서 온 날짜 문자열에서 날짜 파트(YYYY-MM-DD)만 깔끔하게 분리
        const datePart = deadlineStr.substring(0, 10);

        const today = new Date();
        today.setHours(0, 0, 0, 0);
        const deadlineDate = new Date(datePart);
        deadlineDate.setHours(0, 0, 0, 0);

        // 날짜 차이 계산
        const diffTime = deadlineDate - today;
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

        if (diffDays < 0) {
            return <span className="text-red">D+{Math.abs(diffDays)} (지연)</span>;
        } else if (diffDays === 0) {
            return <span className="text-orange">D-{diffDays} (금일)</span>;
        } else {
            return <span>D-{diffDays}</span>;
        }
    };

    const getStatusStyle = (stateName) => {
        if (!stateName) return 'badge-dark'; // 💡 방어 코드 삽입
        switch (stateName) {
            case '출고완료': return 'text-green bg-green-light';
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

    // 💡 1. 차량 배정 모달 오픈 핸들러 (에러 가드 완벽 적용)
    const openVehicleModal = () => {
        if (viewMode !== 'box') return alert('박스 탭에서만 차량 배정이 가능합니다.');
        if (checkedBoxes.length === 0) return alert('미배정 박스를 선택해주세요.');

        // 리덕스 구조인 boxList를 안전하게 가져옵니다. 
        // 만약 상단에 boxList가 없다면 view.boxList가 되도록 이중 방어합니다.
        const currentBoxes = boxList || [];
        const targetBox = currentBoxes.find(b => b.packingId === checkedBoxes[0]);

        // DB 외래키(FK) NOT NULL 제약조건 위반을 막기 위한 마스터 데이터용 기본 ID 지정
        const resolvedCarrierId = targetBox?.carrierId || 1;
        const resolvedVehicleId = targetBox?.vehicleId || 1;

        // 크래시의 원인이었던 'item'을 완전히 제거하고 오늘 날짜 혹은 마감일을 안전하게 문자열로 변환
        let defaultEtd = new Date().toISOString().substring(0, 10);
        if (targetBox && targetBox.deadline) {
            defaultEtd = String(targetBox.deadline).substring(0, 10);
        } else if (targetBox && targetBox.etd) {
            defaultEtd = String(targetBox.etd).substring(0, 10);
        }

        setVehicleForm({
            carrierId: resolvedCarrierId,
            vehicleId: resolvedVehicleId,
            lpn: '',
            driver: '',
            etd: defaultEtd
        });

        setModalOpen(prev => ({ ...prev, vehicle: true }));
    };

    console.log(modalOpen.invoice)
    // 💡 2. 차량 배정 서브밋 핸들러 (중복 선언 원천 차단)
    const handleVehicleSubmit = async () => {
        if (!vehicleForm.lpn || !vehicleForm.lpn.trim()) return alert('차량 번호를 입력해주세요.');
        if (!vehicleForm.driver || !vehicleForm.driver.trim()) return alert('운전자 명을 입력해주세요.');

        const payload = {
            packingIds: checkedBoxes,
            carrierId: vehicleForm.carrierId,
            vehicleId: vehicleForm.vehicleId,
            lpn: vehicleForm.lpn,
            driver: vehicleForm.driver,
            etd: vehicleForm.etd
        };

        try {
            const result = await dispatch(assignOutboundVehicle(payload));
            if (result.meta.requestStatus === 'fulfilled') {
                alert('차량 배정이 완료되었습니다.');
                setModalOpen(prev => ({ ...prev, vehicle: false }));
                setCheckedBoxes([]);
                setSelectedCarrier(null);
                if (typeof getData === 'function') getData(); // 목록 갱신 안전장치
            } else {
                alert('차량 배정에 실패했습니다. 입력 값을 확인하세요.');
            }
        } catch (error) {
            console.error("차량 배정 처리 중 크래시 발생:", error);
            alert('서버 통신 중 에러가 발생했습니다.');
        }
    };

    // 3. 모달 JSX - 운송사/차량 드롭다운으로 교체
    {
        modalOpen.vehicle && (
            <div className="modal-overlay active">
                <div className="modal-container vehicle-modal-container">
                    <div className="modal-header">
                        <h3>차량 배정 신청</h3>
                        <button type="button" className="modal-close-btn"
                            onClick={() => setModalOpen({ ...modalOpen, vehicle: false })}>
                            &times;
                        </button>
                    </div>
                    <div className="modal-body">
                        <div className="modal-form-grid">
                            <div className="form-group">
                                <label>선택된 박스 수</label>
                                <input type="text" className="modal-input"
                                    value={`${checkedBoxes.length} 건`} disabled />
                            </div>
                            {/* ✅ 운송사 드롭다운으로 변경 */}
                            <div className="form-group">
                                <label>운송사 선택 <span className="text-red">*</span></label>
                                <select
                                    className="modal-input"
                                    value={vehicleForm.carrierId}
                                    onChange={(e) => setVehicleForm({
                                        ...vehicleForm,
                                        carrierId: Number(e.target.value)
                                    })}
                                >
                                    <option value={0}>-- 운송사를 선택하세요 --</option>
                                    {(carriers ?? []).map(c => (
                                        <option key={c.id} value={c.id}>
                                            {c.name}
                                        </option>
                                    ))}
                                </select>
                            </div>
                            {/* ✅ 차량 드롭다운으로 변경 */}
                            <div className="form-group">
                                <label>차량 선택 <span className="text-red">*</span></label>
                                <select
                                    className="modal-input"
                                    value={vehicleForm.vehicleId}
                                    onChange={(e) => setVehicleForm({
                                        ...vehicleForm,
                                        vehicleId: Number(e.target.value)
                                    })}
                                >
                                    <option value={0}>-- 차량을 선택하세요 --</option>
                                    {vehicles.map(v => (
                                        <option key={v.id} value={v.id}>
                                            {v.vehicleType} ({v.maxPayloadTon}톤)
                                        </option>
                                    ))}
                                </select>
                            </div>
                            <div className="form-group">
                                <label>차량 번호</label>
                                <input type="text" className="modal-input"
                                    value={vehicleForm.lpn}
                                    onChange={(e) => setVehicleForm({ ...vehicleForm, lpn: e.target.value })}
                                    placeholder="예: 11가 1234" />
                            </div>
                            <div className="form-group">
                                <label>운전자 명</label>
                                <input type="text" className="modal-input"
                                    value={vehicleForm.driver}
                                    onChange={(e) => setVehicleForm({ ...vehicleForm, driver: e.target.value })}
                                    placeholder="홍길동 기사님" />
                            </div>
                        </div>
                    </div>
                    <div className="modal-footer">
                        <button type="button" className="btn-pop-cancel"
                            onClick={() => setModalOpen({ ...modalOpen, vehicle: false })}>
                            취소
                        </button>
                        <button type="button" className="btn-pop-submit"
                            onClick={handleVehicleSubmit}>
                            배정 확정
                        </button>
                    </div>
                </div>
            </div>
        )
    }

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

            // ✅ manifestList에서 packingIds를 직접 꺼내도록 수정
            targetManifests.forEach(transId => {
                const manifest = manifestList.find(m => m.transportationId === transId);
                if (manifest?.packingIds) {
                    linkedBoxes.push(...manifest.packingIds);
                } else {
                    // packingIds가 없으면 transportationId 자체를 임시 식별자로 사용
                    linkedBoxes.push(transId);
                }
            });

            if (linkedBoxes.length === 0) {
                return alert('선택한 매니페스트에 연결된 박스 정보를 찾을 수 없습니다.');
            }
        }

        setActiveInvoiceBoxes(linkedBoxes);
        setSelectedInvoiceBox(linkedBoxes[0]);
        setCurrentSlide(0);
        setModalOpen(prev => ({ ...prev, invoice: true })); // ✅ 버그 2도 같이 수정
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
                            <option value="20">미배정</option>
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
                                    <th>주문번호</th>
                                    <th>박스번호</th>
                                    <th>고객사</th>
                                    <th>운송사</th>
                                    <th>상태</th>
                                    {/* <th>송장번호</th> */}
                                </tr>
                            </thead>
                            <tbody>
                                {boxList.map((item) => {
                                    const isCarrierDisabled = selectedCarrier !== null && selectedCarrier !== item.carrierName;

                                    const isAlreadyAssigned = item.transportationId !== null;

                                    // 다른 운송사가 선택된 상태인지 확인
                                    const isCarrierMismatch = selectedCarrier !== null && selectedCarrier !== item.carrierName;

                                    // 최종 활성화/비활성화 결정
                                    const isDisabled = isAlreadyAssigned || isCarrierMismatch;

                                    // 💡 1. 기한 날짜를 베이스로 20260614 형태 완성
                                    const baseDateStr = item.deadline ? item.deadline.substring(0, 10).replace(/-/g, "") : "20260614";

                                    // 💡 2. 송장번호용 YYMMDD 형태 완성 (20260614 -> 260614)
                                    const shortDateStr = baseDateStr.substring(2);
                                    const padId = (id) => String(id || 0).padStart(3, '0');

                                    // 💡 3. 만약 DB에 저장된 송장번호가 있으면 그대로 쓰고, 없거나 '1111' 같은 더미면 INV-규격 적용
                                    const displayInvoice = item.invoiceNumber && !['1111', '1324'].includes(item.invoiceNumber)
                                        ? item.invoiceNumber
                                        : `INV-${shortDateStr}-${padId(item.packingId)}`;

                                    return (
                                        <tr key={item.packingId}>
                                            {/* 0. 체크박스 */}
                                            <td className="text-center">
                                                <input
                                                    type="checkbox"
                                                    className="box-check"
                                                    disabled={isDisabled}
                                                    onChange={() => handleBoxCheck(item.packingId, item.carrierName)}
                                                    checked={checkedBoxes.includes(item.packingId)}
                                                />
                                            </td>

                                            {/* 1. 기한 */}
                                            <td className="text-center">{formatDeadline(item.deadline)}</td>

                                            {/* 2. 주문번호 (이제 기한 날짜를 베이스로 에러 없이 실시간 렌더링) */}
                                            <td className="text-center text-green font-bold"
                                            // onClick={() => handleOrderDetailView(item.outboundId)}
                                            >
                                                {item.outboundId}
                                            </td>

                                            {/* 3. 박스번호 (더미 데이터 무시하고 무조건 BOX-YYYYMMDD-XXX 강제 적용) */}
                                            <td className="text-center font-bold text-green">
                                                {item.packingId}
                                            </td>

                                            {/* 4. 고객사 */}
                                            <td>{item.customerName}</td>

                                            {/* 5. 운송사 */}
                                            <td>{item.carrierName || '-'}</td>

                                            {/* 6. 상태 */}
                                            <td className="text-center">
                                                <span className={`table-badge ${getStatusStyle(item.stateName)}`}>
                                                    {item.stateName}
                                                </span>
                                            </td>

                                            {/* 7. 송장번호 (발행 전 '차량배정' 상태면 발행대기, 그 외엔 INV-YYMMDD-XXX 강제 적용) */}
                                            {/* <td className="text-center font-bold text-blue">
                                                {item.stateName === '차량배정' ? (
                                                    <span className="text-gray-light">발행대기</span>
                                                ) : (
                                                    displayInvoice
                                                )}
                                            </td> */}
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
                                {manifestList && manifestList.length > 0 ? (
                                    manifestList.map(item => (
                                        <tr key={item.transportationId}>
                                            <td className="text-center">
                                                <input
                                                    type="checkbox"
                                                    className="manifest-check"
                                                    // 💡 '차량배정' 완벽 일치가 아니라 '배정'이라는 글자가 포함되어 있으면 체크박스 활성화
                                                    disabled={
                                                        item.stateCode !== 21 &&
                                                        item.stateCode !== 22
                                                    }
                                                    onChange={() => handleManifestCheck(item.transportationId)}
                                                    checked={checkedManifests.includes(item.transportationId)}
                                                />
                                            </td>
                                            <td className="text-center font-bold text-link manifest-no-link" onClick={() => openInvoiceModal(item.transportationId)}>
                                                {item.transportationId}
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
                                    ))
                                ) : (
                                    /* 💡 데이터가 아예 없을 때 휑하게 비어있는 것보다 '데이터가 없다'는 안내 행을 띄워주는 안전장치 */
                                    <tr>
                                        <td colSpan="6" className="text-center" style={{ padding: '20px', color: '#999' }}>
                                            배정된 매니페스트 내역이 존재하지 않습니다.
                                        </td>
                                    </tr>
                                )}
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

                                {/* 💡 지정 운송사 칸: 선택한 박스의 운송사 이름이 없으면 기본 협력사명('지정 운송사A')이 뜨도록 안전 처리 */}
                                <div className="form-group">
                                    <label>지정 운송사</label>
                                    <input type="text" className="modal-input" value={selectedCarrier || '기본 지정 운송사'} disabled />
                                </div>

                                <div className="form-group">
                                    <label>차량 번호 <span className="text-red">*</span></label>
                                    <input type="text" className="modal-input" value={vehicleForm.lpn} onChange={(e) => setVehicleForm({ ...vehicleForm, lpn: e.target.value })} placeholder="예: 11가 1234" />
                                </div>

                                <div className="form-group">
                                    <label>운전자 명 <span className="text-red">*</span></label>
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