import '@styles/outhistory.css';


const OutHistory = () => {

    
    const openDetailModal = (id) => {

    }

    return (
        <div id="outHistory-page">
            <div className="page-header-flex">
                <h2 className="page-title">출고이력</h2>
            </div>

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
                        <span className="summary-value text-blue">5<small>%</small></span>
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

            <div className="filter-wrapper-card">
                <form className="search-filter-grid">
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
                                <th>출고일자</th>
                                <th>매니페스트 번호</th>
                                <th>운송사</th>
                                <th>출고 제품 수량(세트)</th>
                                <th>요청 수량</th>
                            </tr>
                        </thead>
                        <tbody id="historyTableBody">
                            <tr>
                                <td className="text-center">2026-05-28</td>
                                <td className="font-bold text-link" onClick={openDetailModal(1)}>
                                    ASN-20260602-001</td>
                                <td className="text-center">대한택배</td>
                                <td>15</td>
                                <td className="text-center">5,000</td>
                            </tr>
                            <tr>
                                <td className="text-center">2026-05-26</td>
                                <td className="font-bold text-link" onClick={openDetailModal(2)}>
                                    ASN-20260602-002</td>
                                <td className="text-center">대한택배</td>
                                <td>15</td>
                                <td className="text-center">2,500</td>
                            </tr>
                            <tr>
                                <td className="text-center">2026-05-26</td>
                                <td className="font-bold text-link" onClick={openDetailModal(3)}>
                                    ASN-20260602-002</td>
                                <td className="text-center">대한택배</td>
                                <td>15</td>
                                <td className="text-center">2,500</td>
                            </tr>
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

        </div>
    )
}

export default OutHistory;