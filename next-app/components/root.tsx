"use client";

import { useState, useEffect } from 'react';
import Image from 'next/image';
import { useRouter } from 'next/navigation';
import { showServiceIntro } from '@/components';

import gateMain1 from '@assets/images/banners/GateMain1.jpg';
import gateMain2 from '@assets/images/banners/GateMain2.jpg';
import gateMain3 from '@assets/images/banners/GateMain3.jpg';

import gateReport from '@assets/icons/base/GateReport.png';
import gateCarbon from '@assets/icons/base/GateCarbon.png';
import gateSupply from '@assets/icons/base/GateSupply.png';
import gateLogin from '@assets/icons/base/GateLogin.png';
import gateReportHover from '@assets/icons/hover/GateReportHover.png';
import gateCarbonHover from '@assets/icons/hover/GateCarbonHover.png';
import gateSupplyHover from '@assets/icons/hover/GateSupplyHover.png';
import gateLoginHover from '@assets/icons/hover/GateLoginHover.png';

export const BannerImages = () => {
  const bannerImages = [gateMain1, gateMain2, gateMain3];
  const [currentIndex, setCurrentIndex] = useState(0);
  useEffect(() => {
		const timer = setInterval(() => {
			setCurrentIndex((prevIndex) => {
				if (prevIndex === bannerImages.length - 1) return 0;
				else return prevIndex + 1;
      });
		}, 3000);
		return () => clearInterval(timer)
	}, [bannerImages.length]);
  
  return (
    <section className="main-banner">
      {bannerImages.map((image, index) => (
        <div
          key={index}
          className={`banner-bg ${index === currentIndex ? 'active' : ''}`}
          style={{ backgroundImage: `url(${image.src})` }}
        />
      ))}
      <div className="banner-overlay"></div>
      <div className="banner-text">
        <h2>지속 가능한 미래를 위한 최적의 파트너</h2>
        <h1>ESG 통합 솔루션 "WITH"</h1>
        <p> 
            보고서 자동 요약, 탄소배출량 추적, 공급망 관리까지! <br />
            복잡한 ESG 경영을 단 하나의 플랫폼으로 스마트하게 완성하고 <br />
            기업의 가치를 높이세요.
        </p>
      </div>
    </section>
  );
}

export const ServiceIntro = () => {
  const router = useRouter();
  const services = [
    {
      title: "SKM",
      description: ["ESG 보고서 요약", "자동화 시스템"],
      className: "service-card top-left",
      images: [
        {"src": gateReport, "alt": "icon", "className": "default-icon"},
        {"src": gateReportHover, "alt": "icon hover", "className": "hover-icon"},
      ],
      onClick: () => showServiceIntro(
        "ESG 보고서 요약 자동화",
        "AI를 활용하여 복잡한 ESG 보고서의<br/>핵심 지표와 인사이트를 즉시 추출합니다.",
        gateReport.src
      )
    },
    {
      title: "HighGo!",
      description: ["ESG 탄소배출량 관리", "데이터 분석"],
      className: "service-card top-right",
      images: [
        {"src": gateCarbon, "alt": "icon", "className": "default-icon"},
        {"src": gateCarbonHover, "alt": "icon hover", "className": "hover-icon"},
      ],
      onClick: () => showServiceIntro(
        "ESG 탄소배출량 관리 시스템",
        "사업장별 탄소 배출 데이터를 실시간으로 수집하고<br/>글로벌 표준에 맞춘 대시보드를 제공합니다.",
        gateCarbon.src
      )
    },
    {
      title: "TripleValues",
      description: ["ESG 공급망 관리", "지속 가능성 평가"],
      className: "service-card bottom-left",
      images: [
        {"src": gateSupply, "alt": "icon", "className": "default-icon"},
        {"src": gateSupplyHover, "alt": "icon hover", "className": "hover-icon"},
      ],
      onClick: () => showServiceIntro(
        "ESG 공급망 관리 시스템",
        "협력사의 ESG 리스크를 진단하고 공급망 전체의<br/>지속가능성을 투명하게 모니터링합니다.",
        gateSupply.src
      )
    },
    {
      title: "Platform",
      description: ["로그인"],
      className: "service-card bottom-right login-card",
      images: [
        {"src": gateLogin, "alt": "icon", "className": "default-icon"},
        {"src": gateLoginHover, "alt": "icon hover", "className": "hover-icon"},
      ],
      onClick: () => router.push('/login')
    },
  ]
  return (
    <section className="service-grid">
      {
        services.map((service, index) => (
          <div className={service.className} key={index} onClick={service.onClick}>
            <span className="badge">{service.title}</span>
            <div className="icon-box">
              {service.images.map((img, idx) => (
                  <Image key={idx} src={img.src} alt={img.alt} className={img.className} />
              ))}
            </div>
            <h2>
              {service.description.map((line, idx) => (
                <p key={idx}>{line}</p>
              ))}
            </h2>
          </div>
        ))
      }
    </section>
  )
}