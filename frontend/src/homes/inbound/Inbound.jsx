import { useState, useEffect, useRef } from 'react';
import { POST } from "@utils/Network";
import '@styles/inbound.css';
import { useDispatch, useSelector } from 'react-redux';
import { getInbound, getInboundDetail, setPage, closeInboundModal } from '@stores/inboundSlice';
import { getFirstDay, getLastDayOfMonth, addOneDay } from '@stores/date';
import { showDefaultAlert } from "@components/UI/ServiceAlert";
import * as XLSX from 'xlsx';

const InboundModal = ({ detailData, isModal }) => {
    const dispatch = useDispatch();
    const inbound = detailData?.inbound;
    const items = detailData?.items || [];


    return (
        <>
            <div className={isModal ? "modal-overlay active" : "modal-overlay"} id="asnDetailModal" onClick={(e) => { if (e.target.id === 'asnDetailModal') dispatch(closeInboundModal()); }}>
                <div className="modal-container modal-window" style={{ maxWidth: '1000px', width: '90%' }}>
                    <div className="modal-header">
                        <h3>ASN 상세 명세 조회</h3>
                        <button className="modal-close-btn" onClick={() => dispatch(closeInboundModal())}>&times;</button>
                    </div>

                    <div className="modal-body" style={{ padding: '1.5rem' }}>
                        <div className="modal-form-inline-grid"
                            style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '1.25rem', marginBottom: '1.5rem', background: '#f8fafc', padding: '1.25rem', border: '1px solid var(--border-color)', borderRadius: '6px' }}>
                            <div className="form-group-item">
                                <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 700, color: 'var(--text-dark)', marginBottom: '0.5rem', textAlign: 'left' }}>ASN 번호</label>
                                <input type="text" id="detail_asn_number" className="table-inner-input" readOnly
                                    value={inbound?.asnId || ''}
                                    style={{ backgroundColor: '#e2e8f0', color: '#4a5568', cursor: 'not-allowed', width: '100%', height: '38px', padding: '0.5rem', border: '1px solid var(--border-color)', borderRadius: '4px', boxSizing: 'border-box' }} />
                            </div>
                            <div className="form-group-item">
                                <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 700, color: 'var(--text-dark)', marginBottom: '0.5rem', textAlign: 'left' }}>공급사명</label>
                                <input type="text" id="detail_supplier" className="table-inner-input" readOnly
                                    value={inbound?.partnerName || ''}
                                    style={{ backgroundColor: '#e2e8f0', color: '#4a5568', cursor: 'not-allowed', width: '100%', height: '38px', padding: '0.5rem', border: '1px solid var(--border-color)', borderRadius: '4px', boxSizing: 'border-box' }} />
                            </div>
                            <div className="form-group-item">
                                <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 700, color: 'var(--text-dark)', marginBottom: '0.5rem', textAlign: 'left' }}>진행상태</label>
                                <select id="detail_asn_status" className="table-inner-input" disabled value="입고완료"
                                    style={{ width: '100%', height: '38px', padding: '0.5rem', border: '1px solid var(--border-color)', borderRadius: '4px', boxSizing: 'border-box', backgroundColor: '#e2e8f0', color: '#4a5568', cursor: 'not-allowed', WebkitAppearance: 'none', MozAppearance: 'none', appearance: 'none' }}>
                                    <option value="출고완료">출고완료</option>
                                    <option value="입고대기">입고대기</option>
                                    <option value="입고중">입고중</option>
                                    <option value="입고완료">입고완료</option>
                                    <option value="취소">취소</option>
                                </select>
                            </div>
                        </div>

                        <div className="sheet-tab-content active" style={{ borderTop: 'none' }}>
                            <div className="excel-table-wrapper" style={{ maxHeight: '300px', overflowY: 'auto', border: '1px solid var(--border-color)', borderTop: 'none' }}>
                                <table className="excel-styled-table" style={{ width: '100%', borderCollapse: 'collapse' }}>
                                    <thead>
                                        <tr>
                                            <th style={{ width: '80px', backgroundColor: '#f1f5f9', border: '1px solid var(--border-color)', padding: '0.5rem', fontSize: '0.85rem', textAlign: 'center' }}>No</th>
                                            <th style={{ width: '25%', backgroundColor: '#f1f5f9', border: '1px solid var(--border-color)', padding: '0.5rem', fontSize: '0.85rem', textAlign: 'left' }}>품목코드</th>
                                            <th style={{ backgroundColor: '#f1f5f9', border: '1px solid var(--border-color)', padding: '0.5rem', fontSize: '0.85rem', textAlign: 'left' }}>품목명</th>
                                            <th style={{ width: '25%', backgroundColor: '#f1f5f9', border: '1px solid var(--border-color)', padding: '0.5rem', fontSize: '0.85rem', textAlign: 'right' }}>입고수량 (kg)</th>
                                        </tr>
                                    </thead>
                                    <tbody id="detailAsnTableBody">
                                        {items.map((item, index) => (
                                            <tr key={index}>
                                                <td style={{ border: '1px solid var(--border-color)', padding: '0.5rem', fontSize: '0.85rem', textAlign: 'center' }}>{index + 1}</td>
                                                <td style={{ border: '1px solid var(--border-color)', padding: '0.5rem', fontSize: '0.85rem', textAlign: 'left' }}>{item.alloyType}</td>
                                                <td style={{ border: '1px solid var(--border-color)', padding: '0.5rem', fontSize: '0.85rem', textAlign: 'left' }}>{item.itemName}</td>
                                                <td style={{ border: '1px solid var(--border-color)', padding: '0.5rem', fontSize: '0.85rem', textAlign: 'right' }}>{item.weight?.toLocaleString()}</td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>

                    <div className="modal-footer" style={{ padding: '1rem 1.5rem', backgroundColor: '#f8fafc', borderTop: '1px solid var(--border-color)', display: 'flex', justifyContent: 'flex-end' }}>
                        <button type="button" className="btn-pop-cancel" onClick={() => dispatch(closeInboundModal())} style={{ minWidth: '120px', backgroundColor: '#64748b', color: 'white', border: 'none', padding: '0.6rem', borderRadius: '4px', fontWeight: 600, cursor: 'pointer' }}>닫기</button>
                    </div>
                </div>
            </div>
        </>
    )
};

const Inbound = () => {
    const dispatch = useDispatch();

    const orderStartRef = useRef(null);
    const orderEndRef = useRef(null);
    const asnRef = useRef(null);

    const isModal = useSelector((state) => state.inbound.isModal);
    const list = useSelector((state) => state.inbound.view.list);
    const page = useSelector((state) => state.inbound.view.page);
    const totalCount = useSelector((state) => state.inbound.view.totalCount);
    const totalPages = useSelector((state) => state.inbound.view.totalPages);
    const size = useSelector((state) => state.inbound.view.size);
    const detailData = useSelector((state) => state.inbound.detailData);

    const [firstDate, setFirstDate] = useState(null);
    const [endDate, setEndDate] = useState(null);

    const tableColList = ["입고일자", "ASN 번호", "공급사","입고 창고"]

    const openDetailModal = (asnId) => {
        const params = { asnId };
        dispatch(getInboundDetail(params));
    };

    const searchEvent = (e) => {
        e.preventDefault();
        if (page === 1) getData(); else dispatch(setPage(1));
    };

    const resetResearch = () => {
        if (orderStartRef.current) orderStartRef.current.value = getFirstDay();
        if (orderEndRef.current) orderEndRef.current.value = getLastDayOfMonth();
        if (asnRef.current) asnRef.current.value = "";
        if (page === 1) getData(); else dispatch(setPage(1));
    };

    const getData = () => {
        const startVal = orderStartRef.current?.value || "";
        const endVal = orderEndRef.current?.value || "";

        if ((startVal !== "" && endVal === "") || (startVal === "" && endVal !== "")) {
            showDefaultAlert("오류", "입고일자 검색을 완성하거나 초기화 후 검색해주세요.", "error");
            return;
        }

        const params = { page, size };

        if (asnRef.current !== null) params.asnId = asnRef.current.value;

        if (startVal !== "") {
            params.orderStart = startVal;
            setFirstDate(startVal);
        }

        if (endVal !== "") {
            params.orderEnd = addOneDay(endVal);
            setEndDate(endVal);
        }

        dispatch(getInbound(params));
    };

    useEffect(() => {
        if (!isModal) getData();
    }, [page, isModal]);


    const handleDownload = (data) => {
        // 1. 데이터를 기반으로 워크시트(Worksheet) 생성

        const replaceData = []
        data.map((v) => { replaceData.push({ "입고일자": v.ata, "ASN 번호": v.asnId, "공급사": v.partnerName, "입고 창고": v.warehouseName}) })
        console.log(list)

        const worksheet = XLSX.utils.json_to_sheet(replaceData);

        // (선택) 엑셀 시트의 헤더(열 이름)를 한글로 예쁘게 변경하고 싶을 때
        XLSX.utils.sheet_add_aoa(worksheet, [tableColList], { origin: "A1" });

        // 2. 새로운 워크북(Workbook)을 생성하고 워크시트 추가
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, `입고이력_${page}페이지_${firstDate}~${endDate}`);

        // 3. 엑셀 파일 작성 및 다운로드 실행
        // 파일명은 원하는 대로 지정할 수 있습니다.
        XLSX.writeFile(workbook, `입고이력_${page}페이지_${firstDate}~${endDate}.xlsx`);
    };


    return (
        <div id="inbound-page">
            <div className="page-header-flex">
                <h2 className="page-title">입고이력 조회</h2>
            </div>

            <div className="filter-wrapper-card">
                <form className="search-filter-grid" onSubmit={searchEvent}>
                    <div className="filter-group group-date-range">
                        <label>입고일자 검색</label>
                        <div className="date-range-container">
                            <input type="date" id="search_start_date" className="filter-control" ref={orderStartRef} defaultValue={getFirstDay()} />
                            <span className="date-separator">~</span>
                            <input type="date" id="search_end_date" className="filter-control" ref={orderEndRef} defaultValue={getLastDayOfMonth()} />
                        </div>
                    </div>
                    <div className="filter-group">
                        <label>ASN 번호 검색</label>
                        <div className="date-range-container">
                            <input type="text" id="search_order_number" className="filter-control" ref={asnRef} />
                        </div>
                    </div>
                    <div></div>
                    <div className="filter-btn-group">
                        <button type="button" onClick={() => resetResearch()} className="btn-filter-reset">초기화</button>
                        <button type="submit" className="btn-filter-search">조회하기</button>
                    </div>
                </form>
            </div>

            <div className="content-card">
                <div className="table-responsive">
                    <div className="inhistory-btn-group">
                        <button type="button" className="btn-filter-reset" onClick={() => handleDownload(list)}>엑셀 다운로드</button>
                        <button type="button" className="btn-filter-search" onClick={getData}>새로고침</button>
                    </div>
                    <table className="history-data-table">
                        <thead>
                            
                            <tr>
                                {
                                    tableColList.map((v, i) => <th key={i}>{v}</th>)
                                }
                            </tr>
                        </thead>
                        <tbody id="historyTableBody">
                            {list.length === 0 ? (
                                <tr>
                                    <td colSpan={4} className="text-center" style={{ padding: '2rem', color: 'gray' }}>조회된 데이터가 없습니다.</td>
                                </tr>
                            ) : (
                                list?.map((v, i) => (
                                    <tr key={i}>
                                        <td className="text-center">{v.ata}</td>
                                        <td className="font-bold text-link" style={{ cursor: 'pointer' }} onClick={() => openDetailModal(v.asnId)}>{v.asnId}</td>
                                        <td>{v.partnerName}</td>
                                        <td className="text-center">{v.warehouseName}</td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>

                <div className="pagination-container">
                    <div className="pagination-info">
                        전체 <span>{totalCount}</span>건
                    </div>

                    <div className="pagination-buttons">
                        <button type="button" className="btn-page" title="처음 페이지" onClick={() => dispatch(setPage(1))} disabled={page <= 1}>&lt;&lt;</button>
                        <button type="button" className="btn-page" title="이전 블록" onClick={() => dispatch(setPage(page - 1))} disabled={page <= 1}>&lt;</button>
                        {Array.from({ length: totalPages }).map((v, i) => {
                            const index = i + 1;
                            return (
                                <button key={i} type="button" className={page == index ? 'btn-page-num active' : 'btn-page-num'} onClick={() => dispatch(setPage(index))}>{index}</button>
                            )
                        })}
                        <button type="button" className="btn-page" title="다음 블록" onClick={() => dispatch(setPage(page + 1))} disabled={page == totalPages}>&gt;</button>
                        <button type="button" className="btn-page" title="끝 페이지" onClick={() => dispatch(setPage(totalPages))} disabled={page == totalPages}>&gt;&gt;</button>
                    </div>
                </div>

            </div>

            {isModal && <InboundModal detailData={detailData} isModal={isModal} />}
        </div>
    )
}

export default Inbound;