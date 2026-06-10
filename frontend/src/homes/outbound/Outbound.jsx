import '@styles/outbound.css';
import { useState } from 'react';


const Outbound = () => {

    return (
        <>
            <div className="page-header-flex">
                <h2 className="page-title">출고/송장</h2>
                <div className="header-action-group">
                    {/* <button className="btn-secondary-action">엑셀 내보내기</button>
                    <button className="btn-main-action" onclick="openOrderModal()">+ 신규 주문 등록</button> */}
                </div>
            </div>

            <div className="order-summary-grid">
                <div className="summary-card-item">
                    <div className="card-info-left">
                        <span className="summary-label">출고 예정</span>
                        <span className="summary-value">42<small>건</small></span>
                    </div>
                    <div className="card-trend-right">
                        <span className="status-badge bg-all-light text-muted">당일</span>
                    </div>
                </div>

                <div className="summary-card-item">
                    <div className="card-info-left">
                        <span className="summary-label">출고 확정</span>
                        <span className="summary-value text-green">34<small>건</small></span>
                    </div>
                    <div className="card-trend-right">
                        <span className="status-badge bg-green-light text-green">당일</span>
                    </div>
                </div>

                <div className="summary-card-item">
                    <div className="card-info-left">
                        <span className="summary-label">기한 임박</span>
                        <span className="summary-value text-orange">5<small>건</small></span>
                    </div>
                    <div className="card-trend-right">
                        <span className="status-badge bg-orange-light text-orange">당일</span>
                    </div>
                </div>

                <div className="summary-card-item">
                    <div className="card-info-left">
                        <span className="summary-label">기한 초과</span>
                        <span className="summary-value text-red">3<small>건</small></span>
                    </div>
                    <div className="card-trend-right">
                        <span className="status-badge bg-red-light text-red">당일</span>
                    </div>
                </div>

                <div className="summary-card-item">
                    <div className="card-info-left">
                        <span className="summary-label">미배정</span>
                        <span className="summary-value text-dark">3<small>건</small></span>
                    </div>
                    <div className="card-trend-right">
                        <span className="status-badge table-badge badge-dark">당일</span>
                    </div>
                </div>

            </div>

            <div className="filter-wrapper-card">
                <form className="search-filter-grid">
                    <div className="filter-group group-date-range">
                        <label>주문 기간</label>
                        <div className="date-range-container">
                            <input type="date" id="search_start_date" className="filter-control" />
                            <span className="date-separator">~</span>
                            <input type="date" id="search_end_date" className="filter-control" />
                        </div>
                    </div>
                    <div className="filter-group">
                        <label>주문번호 검색</label>
                        <div className="date-range-container">
                            <input type="text" id="search_order_number" className="filter-control" />
                        </div>
                    </div>

                    <div className="filter-group">
                        <label>고객사 검색</label>
                        <div className="date-range-container">
                            <input type="text" id="search_order_number" className="filter-control" />
                        </div>
                    </div>

                    <div className="filter-group">
                        <label htmlFor="search_status">진행 상태</label>
                        <select id="search_status" className="filter-control">
                            <option value="">전체 상태</option>
                            <option value="approved">신규</option>
                            <option value="pending">처리중</option>
                            <option value="rejected">완료</option>
                        </select>
                    </div>

                    <div className="filter-btn-group">
                        <button type="reset" className="btn-filter-reset">초기화</button>
                        <button type="submit" className="btn-filter-search">조회하기</button>
                    </div>
                </form>
            </div>
                    
        </>
    )
}

export default Outbound;