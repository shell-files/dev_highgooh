import { useState, useEffect, useRef } from 'react';
import { POST } from "@utils/Network";
import '@styles/inbound.css';

const InboundModal = () => {

    return (
        <div class="modal-overlay" id="asnDetailModal">
            <div class="modal-container modal-window" style="max-width: 1000px; width: 90%;">
                <div class="modal-header">
                    <h3>ASN 상세 명세 조회</h3>
                    <button class="modal-close-btn" onclick="closeInboundDetailModal()">&times;</button>
                </div>

                <div class="modal-body" style="padding: 1.5rem;">
                    <div class="modal-form-inline-grid"
                        style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 1.25rem; margin-bottom: 1.5rem; background: #f8fafc; padding: 1.25rem; border: 1px solid var(--border-color); border-radius: 6px;">
                        <div class="form-group-item">
                            <label
                                style="display: block; font-size: 0.85rem; font-weight: 700; color: var(--text-dark); margin-bottom: 0.5rem; text-align: left;">ASN
                                번호</label>
                            <input type="text" id="detail_asn_number" class="table-inner-input" readonly
                                style="background-color: #e2e8f0; color: #4a5568; cursor: not-allowed; width: 100%; height: 38px; padding: 0.5rem; border: 1px solid var(--border-color); border-radius: 4px; box-sizing: border-box;" />
                        </div>
                        <div class="form-group-item">
                            <label
                                style="display: block; font-size: 0.85rem; font-weight: 700; color: var(--text-dark); margin-bottom: 0.5rem; text-align: left;">공급사명</label>
                            <input type="text" id="detail_supplier" class="table-inner-input" readonly
                                style="background-color: #e2e8f0; color: #4a5568; cursor: not-allowed; width: 100%; height: 38px; padding: 0.5rem; border: 1px solid var(--border-color); border-radius: 4px; box-sizing: border-box;" />
                        </div>
                        <div class="form-group-item">
                            <label
                                style="display: block; font-size: 0.85rem; font-weight: 700; color: var(--text-dark); margin-bottom: 0.5rem; text-align: left;">진행상태</label>
                            <select id="detail_asn_status" class="table-inner-input" disabled
                                style="width: 100%; height: 38px; padding: 0.5rem; border: 1px solid var(--border-color); border-radius: 4px; box-sizing: border-box; background-color: #e2e8f0; color: #4a5568; cursor: not-allowed; -webkit-appearance: none; -moz-appearance: none; appearance: none;">
                                <option value="출고완료">출고완료</option>
                                <option value="입고대기">입고대기</option>
                                <option value="입고중">입고중</option>
                                <option value="입고완료">입고완료</option>
                                <option value="취소">취소</option>
                            </select>
                        </div>
                    </div>

                    <div class="sheet-tab-content active" style="border-top: none;">
                        <div class="excel-table-wrapper"
                            style="max-height: 300px; overflow-y: auto; border: 1px solid var(--border-color); border-top: none;">
                            <table class="excel-styled-table" style="width: 100%; border-collapse: collapse;">
                                <thead>
                                    <tr>
                                        <th
                                            style="width: 80px; background-color: #f1f5f9; border: 1px solid var(--border-color); padding: 0.5rem; font-size: 0.85rem; text-align: center;">
                                            No</th>
                                        <th
                                            style="width: 25%; background-color: #f1f5f9; border: 1px solid var(--border-color); padding: 0.5rem; font-size: 0.85rem; text-align: left;">
                                            품목코드</th>
                                        <th
                                            style="background-color: #f1f5f9; border: 1px solid var(--border-color); padding: 0.5rem; font-size: 0.85rem; text-align: left;">
                                            품목명</th>
                                        <th
                                            style="width: 25%; background-color: #f1f5f9; border: 1px solid var(--border-color); padding: 0.5rem; font-size: 0.85rem; text-align: right;">
                                            입고수량 (kg)</th>

                                    </tr>
                                </thead>
                                <tbody id="detailAsnTableBody">
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <div class="modal-footer"
                    style="padding: 1rem 1.5rem; background-color: #f8fafc; border-top: 1px solid var(--border-color); display: flex; justify-content: flex-end;">
                    <button type="button" class="btn-pop-cancel" onclick="closeInboundDetailModal()"
                        style="min-width: 120px; background-color: #64748b; color: white; border: none; padding: 0.6rem; border-radius: 4px; font-weight: 600; cursor: pointer;">닫기</button>
                </div>
            </div>
        </div>
    )
};

const Inbound = () => {
    const orderStartRef = useRef(null);
    const orderEndRef = useRef(null);
    const asnRef = useRef(null);

    const [isModal, setModal] = useState(false);
    const [list, setList] = useState([]);
    const [page, setPage] = useState(1);
    const [totalCount, setTotalCount] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [size, setSize] = useState(20);

    const openInboundDetailModal = (id) => {
    };

    const getData = () => {
        const params = { page, size };
        POST("/inbound", params).then(res => {
            console.log(res);
            setList(res.data.list);
            setPage(res.data.pagination.page);
            setTotalCount(res.data.pagination.totalCount);
            setTotalPages(res.data.pagination.totalPages);
        });
    }

    useEffect(() => {
        getData()
    }, [page]);


    return (
        <>
            <div className="page-header-flex">
                <h2 className="page-title">자재 입고이력</h2>
            </div>

            <div className="filter-wrapper-card">
                <form className="search-filter-grid">
                    <div className="filter-group group-date-range">
                        <label>입고 완료 일자</label>
                        <div className="date-range-container">
                            <input type="date" id="search_start_date" className="filter-control" />
                            <span className="date-separator">~</span>
                            <input type="date" id="search_end_date" className="filter-control" />
                        </div>
                    </div>
                    <div className="filter-group">
                        <label>ASN 번호 검색</label>
                        <div className="date-range-container">
                            <input type="text" id="search_order_number" className="filter-control" />
                        </div>
                    </div>
                    <div></div>
                    <div className="filter-btn-group">
                        <button type="reset" className="btn-filter-reset">초기화</button>
                        <button type="submit" className="btn-filter-search">조회하기</button>
                    </div>
                </form>
            </div>

            <div className="content-card">
                <div className="table-responsive">
                    <div className="inhistory-btn-group">
                        <button type="button" className="btn-filter-reset">엑셀 다운로드</button>
                        <button type="submit" className="btn-filter-search">새로고침</button>
                    </div>
                    <table className="history-data-table">
                        <thead>
                            <tr>
                                <th>입고일자</th>
                                <th>ASN 번호</th>
                                <th>공급사</th>
                                <th>입고 창고</th>
                            </tr>
                        </thead>
                        <tbody id="historyTableBody">
                            {
                                list?.map((v, i) =>
                                    <tr key={i}>
                                        <td className="text-center">{v.eta}</td>
                                        <td className="font-bold text-link" onClick={openInboundDetailModal('1')}>{v.asnId}</td>
                                        <td>{v.partnerName}</td>
                                        <td className="text-center">{v.warehouseName}</td>
                                    </tr>
                                )
                            }
                        </tbody>
                    </table>
                </div>

                <div className="pagination-container">
                    <div className="pagination-info">
                        전체 <span>3</span>건
                    </div>

                    <div className="pagination-buttons">
                        <button type="button" className="btn-page" title="처음 페이지" disabled>&lt;&lt;</button>
                        <button type="button" className="btn-page" title="이전 블록" disabled>&lt;</button>

                        <button type="button" className="btn-page-num active">1</button>
                        <button type="button" className="btn-page-num">2</button>
                        <button type="button" className="btn-page-num">3</button>
                        <button type="button" className="btn-page-num">4</button>
                        <button type="button" className="btn-page-num">5</button>

                        <button type="button" className="btn-page" title="다음 블록">&gt;</button>
                        <button type="button" className="btn-page" title="끝 페이지">&gt;&gt;</button>
                    </div>
                </div>

            </div>


        </>
    )
}

export default Inbound;