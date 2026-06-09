import { useState, useEffect, useRef } from 'react';
import { GET, POST, PUT, PATCH, DELETE } from "@utils/Network";
import '@styles/asn.css';

const AsnModal = ({isModal, setModal}) => {
  const [partnerCompany, setPartnerCompany] = useState();
  const [warehouses, setWarehouses] = useState();
  const [materials, setMaterials] = useState();
  const [asn, setAsn] = useState({
    partnerCompany: 0,
    eta: '',
    warehouse: 0,
    vehicleNumber: ''
  });
  const [asnMaterials, setAsnMaterials] = useState([]);
  const onChangeEvent = (e) => {
    const {name, value} = e.target;
    setAsn({...asn, [name]:value});
  }
  const onChangeTableEvent = (e, index) => {
    const { name, value } = e.target;
    
    const updatedMaterials = asnMaterials.map((item, i) => {
      if (i === index) {
        return { ...item, [name]: Number(value) };
      }
      return item;
    });

    setAsnMaterials(updatedMaterials);
  };
  const onDeleteRow = (index) => {
    const filteredMaterials = asnMaterials.filter((_, i) => i !== index);
    setAsnMaterials(filteredMaterials);
  };
  const addAsn = () => {
    if(asn.partnerCompany === 0) {
      alert("공급사명 선택 하세요.");
      return;
    }
    if(asn.eta === '') {
      alert("입고 예정날짜 선택 하세요.");
      return;
    }
    if(asn.warehouse === 0) {
      alert("입고 창고를 선택 하세요.");
      return;
    }
    console.log(asn, asnMaterials);

    let check윤우 = false;
    let msg윤우 = "";
    for(const material of asnMaterials) {
      if(material.itemNo === 0) {
        console.log(material);
        check윤우 = true;
        msg윤우 = "품목 등록이 되어 있지 앖습니다.";
        break;
      }
      if(material.weight === 0) {
        console.log(material);
        check윤우 = true;
        msg윤우 = "품목 무게가 등록 되어 있지 앖습니다.";
        break;
      }
      if(material.diameter === 0) {
        console.log(material);
        check윤우 = true;
        msg윤우 = "지름이 등록 되어 있지 앖습니다.";
        break;
      }
    }

    if(check윤우) {
      alert(msg윤우);
      return;
    }

    const params = {
      partnerCompanyId: asn.partnerCompany,
      warehouseId: asn.warehouse,
      eta: asn.eta,
      items: asnMaterials
    }
    PUT("/asn", params).then(res => {
      console.log(res);
    });
  }
  const addAsnMaterial = () => {
    const material = {
      itemNo: 0,
      weight: 0,
      diameter: 0
    }
    setAsnMaterials([...asnMaterials, material]);
  }
  useEffect(()=> {
    GET("/asn").then(res => {
      if(res.status === true) {
        setPartnerCompany(res.data.suppliers);
        setWarehouses(res.data.warehouses);
        setMaterials(res.data.materials);
      }
    })
  }, []);
  return (
    <>
    <div id="asn-register-modal" className={isModal ? 'modal-overlay active' : 'modal-overlay' } onClick={()=>setModal(false)}></div>
    <div id="asn-register-modal" className={isModal ? 'modal-overlay2 active' : 'modal-overlay' } >
      <div className="modal-window">
          <div className="modal-header">
              <h3>사전입고 통지(ASN) 등록</h3>
              <button className="modal-close-btn" onClick={()=>setModal(false)}>&times;</button>
          </div>
          <div className="modal-body">
              <div className="popup-section">
                  <h4 className="sub-title">기본 정보</h4>
                  <div className="form-grid-4">
                      <div className="input-box">
                          <label>공급사명</label>
                          <select type="text" name="partnerCompany" value={asn.partnerCompany} onChange={onChangeEvent}>
                              <option value>선택</option>
                              {
                                partnerCompany?.map((v,i) => <option key={i} value={v.id}>{v.name}</option> )
                              }
                          </select>
                      </div>
                      <div className="input-box">
                          <label>입고 예정일시</label>
                          <input type="date" required className="filter-date-input" name="eta" value={asn.eta} onChange={onChangeEvent}/>
                      </div>
                  </div>
                  <div className="input-box">
                      <label>입고 창고</label>
                      <select required name="warehouse" value={asn.warehouse} onChange={onChangeEvent}>
                          <option value>선택하세요</option>
                          { warehouses?.map((v,i) => <option key={i} value={v.id}>{v.name}</option>)}
                      </select>
                  </div>
                  <div className="input-box">
                      <label>차량번호</label>
                      <input type="text" className="table-inner-input highlight-field" name="vehicleNumber" value={asn.vehicleNumber} onChange={onChangeEvent}/>
                  </div>
              </div>

              <div className="popup-section" style={{marginTop: '1.5rem'}}>
                  <div className="section-header-flex">
                      <h4 className="sub-title">품목 정보</h4>
                      <button type="button" className="btn-secondary-sm" onClick={addAsnMaterial}>+ 품목 추가</button>
                  </div>
                  <div className="table-responsive" style={{maxHeight: '250px', overflowY: 'auto'}}>
                      <table className="popup-grid-table">
                          <thead>
                              <tr>
                                  <th>순번</th>
                                  <th>품목</th>
                                  <th>무게(kg)</th>
                                  <th>지름(inch)</th>
                                  <th>삭제</th>
                              </tr>
                          </thead>
                          <tbody>
                            {asnMaterials?.map((v,i) => {
                              return (
                                <tr key={i}>
                                    <td className="text-center">{i+1}</td>
                                    <td>
                                        <select type="text" className="table-inner-input highlight-field" name="itemNo" value={v.itemNo} onChange={e=>onChangeTableEvent(e,i)}>
                                            <option value="0">선택하세요</option>
                                            {
                                              materials?.map((vv,ii) => <option key={ii} value={vv.id}>{vv.alloyType}</option> )
                                            }
                                        </select>
                                    </td>
                                    <td><input type="number" className="table-inner-input text-right highlight-field" name="weight" value={v.weight} onChange={e=>onChangeTableEvent(e,i)}/>
                                    </td>
                                    <td><input type="number" className="table-inner-input highlight-field" name="diameter" value={v.diameter} onChange={e=>onChangeTableEvent(e,i)}/>
                                    </td>
                                    <td className="text-center">
                                        <button type="button" className="btn-delete-row" onClick={()=>onDeleteRow(i)}>&times;</button>
                                    </td>
                                </tr>
                              )
                            })}
                          </tbody>
                      </table>
                  </div>
              </div>
          </div>
          <div className="modal-footer">
              <button className="btn-pop-cancel" onClick={()=>setModal(false)}>취소</button>
              <button id="btnAsnSave" className="btn-pop-submit" onClick={addAsn}>ASN 등록</button>
          </div>
        </div>
      </div>
    </>
  )
}

const Asn = () => { 
  const orderStartRef = useRef(null);
  const orderEndRef = useRef(null);
  const asnRef = useRef(null);

  const [isModal, setModal] = useState(false);
  const [summary, setSummary] = useState({completed: 0, expected: 0, total: 0});
  const [list, setList] = useState([]);
  const [page, setPage] = useState(1);
  const [totalCount, setTotalCount] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [size, setSize] = useState(20);

  const openAsnModal = () => setModal(true);

  const openAsnEditModal = (id) => {}

  const searchEvent = (e) => {
    e.preventDefault();
    getData();
  }

  const getData = () => {
    const params = { 
      page, size,
    }
    
    if(asnRef.current !== null) {
      params.asnId = asnRef.current.value;
    }

    if(orderStartRef.current !== null) {
      params.orderStart = orderStartRef.current.value;
    }
    
    if(orderEndRef.current !== null) {
      params.orderEnd = orderEndRef.current.value;
    }
    
    if(params?.orderStart !== "" && params?.orderEnd === "") {
      alert("주문 기간이 필요 합니다.");
      return;
    }

    POST("/asn", params).then(res => {
      if(res.status == true) {
        setSummary(res.data.summary);
        setList(res.data.list);
        setPage(res.data.pagination.page);
        setTotalCount(res.data.pagination.totalCount);
        setTotalPages(res.data.pagination.totalPages);
      }
    });
  }

  useEffect(() => {
    getData()
  }, [page]);
  
  return (
    <>
    <div id="asn-management-page" className="page-content active">
      <div className="page-header-flex">
          <h2 className="page-title">사전입고 통지(ASN) 관리</h2>
          <button className="btn-main-action" onClick={openAsnModal}>+ ASN 등록 추가</button>
      </div>
      <div className="order-summary-grid">
          <div className="summary-card-item">
              <div className="card-info-left">
                  <span className="summary-label">총 입고건수</span>
                  <span className="summary-value">{summary.total}<small>건</small></span>
              </div>
              <div className="card-trend-right">
                  <span className="status-badge bg-all-light text-muted">당월</span>
              </div>
          </div>

          <div className="summary-card-item">
              <div className="card-info-left">
                  <span className="summary-label">입고예정</span>
                  <span className="summary-value text-green">{summary.expected}<small>건</small></span>
              </div>
              <div className="card-trend-right">
                  <span className="status-badge bg-green-light text-green">당월</span>
              </div>
          </div>

          <div className="summary-card-item">
              <div className="card-info-left">
                  <span className="summary-label">입고완료</span>
                  <span className="summary-value text-blue">{summary.completed}<small>건</small></span>
              </div>
              <div className="card-trend-right">
                  <span className="status-badge bg-blue-light text-blue">당월</span>
              </div>
          </div>
      </div>

      <div className="filter-wrapper-card">
          <form className="search-filter-grid" onSubmit={searchEvent}>
              <div className="filter-group group-date-range">
                  <label>주문 기간</label>
                  <div className="date-range-container">
                      <input type="date" id="search_start_date" className="filter-control" ref={orderStartRef}/>
                      <span className="date-separator">~</span>
                      <input type="date" id="search_end_date" className="filter-control" ref={orderEndRef}/>
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
                  <button type="reset" className="btn-filter-reset">초기화</button>
                  <button type="submit" className="btn-filter-search">조회하기</button>
              </div>
          </form>
      </div>

      <div className="content-card" style={{marginTop: '1rem'}}>
          <div className="table-responsive">
              <button type="submit" className="btn-filter-search refresh" onClick={getData}>새로고침</button>
              <table className="main-list-table">
                  <thead>
                      <tr>
                          <th>ASN 번호</th>
                          <th>공급사명</th>
                          <th>입고 창고</th>
                          <th>차량번호</th>
                          <th>입고 예정일</th>
                          <th>품목 건수</th>
                          <th>진행 상태</th>
                      </tr>
                  </thead>
                  <tbody>
                    {
                      list?.map((v,i) =>
                        <tr key={v.asnId}>
                            <td>
                              <a href="#" className="text-link" onClick={()=>openAsnEditModal('ASN-20260602-001')}>{v.asnId}</a>
                            </td>
                            <td>{v.partnerName}</td>
                            <td>{v.warehouseName}</td>
                            <td>{v.vehicleNumber}</td>
                            <td>{v.eta}</td>
                            <td>{v.itemCount}건</td>
                            <td><span className="status-badge ready">{v.step}</span></td>
                        </tr>
                      )
                    }
                </tbody>
            </table>
        </div>

        <div className="pagination-container">
            <div className="pagination-info">
                전체 <span>{totalCount}</span>건
            </div>
            <div className="pagination-buttons">
                <button type="button" className="btn-page first" title="첫 페이지" onClick={()=> setPage(1)} disabled={page <= 1}>&laquo;</button>
                <button type="button" className="btn-page prev" title="이전 페이지" onClick={()=> setPage(page - 1)} disabled={page <= 1}>&lsaquo;</button>
                {
                  Array.from({length: totalPages}).map((v,i) => {
                    const index = i + 1;
                    return (
                      <button key={i} type="button" className={page == index ? 'btn-page-num active' : 'btn-page-num' } onClick={()=>setPage(index)}>{index}</button>
                    )
                  }
                  )
                }

                <button type="button" className="btn-page next" title="다음 페이지" onClick={()=> setPage(page + 1)} disabled={page == totalPages}>&rsaquo;</button>
                <button type="button" className="btn-page last" title="마지막 페이지" onClick={()=> setPage(totalPages)} disabled={page == totalPages}>&raquo;</button>
            </div>
            <div className="pagination-size-selector">

            </div>
        </div>
    </div>
  </div>
    { isModal && <AsnModal isModal={isModal} setModal={setModal} /> }
  </>
  )
}

export default Asn;