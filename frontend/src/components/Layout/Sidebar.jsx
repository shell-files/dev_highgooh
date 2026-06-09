import { useNavigate } from 'react-router';

const Sidebar = () => {
  const navigate = useNavigate()
  return (
    <div className="sidebar">
      <ul>
          <h4 className="factory_icon">입고</h4>
          <li><a onClick={()=>navigate("/home/asn")}>ASN</a></li>
          <li><a onClick={()=>navigate("/home/inbound")}>입고이력</a></li>
      </ul>
      <ul>
          <h4 className="released_icon">출고</h4>
          <li><a href="./order.html">주문</a></li>
          <li><a href="./packing.html">패킹</a></li>
          <li><a href="./outbound.html">출고/송장</a></li>
          <li><a href="./outhistory.html">출고이력</a></li>
      </ul>
      <ul>
          <h4 className="carbon_icon">탄소배출량</h4>
          <li><a href="./pcf.html">대시보드</a></li>
          <li><a href="./anomaly.html">이상치탐지</a></li>
      </ul>
    </div>
  )
}

export default Sidebar;