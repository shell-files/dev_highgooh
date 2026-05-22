import Image from 'next/image';
import { BannerImages, ServiceIntro } from '@/components';

import '@styles/Gate.css'

import logo from '@assets/images/logos/Logo.png';
import gateBg1 from '@assets/images/backgrounds/GateBg1.png';
import gateBg2 from '@assets/images/backgrounds/GateBg2.png';
import gateBg3 from '@assets/images/backgrounds/GateBg3.png';

const Home = () => {
  return (
    <div className="gate-body">
      <div className="bg-elements">
        <Image src={gateBg1} alt="background shape 1" className="bg-img bg-1" />
        <Image src={gateBg2} alt="background shape 2" className="bg-img bg-2" />
        <Image src={gateBg3} alt="background shape 3" className="bg-img bg-3" />
      </div>

      <div className="container">
        <header className="header">
          <Image src={logo} alt="We are IT Hero Logo" className="logo" style={{width: 'auto', display: 'inline-block'}} />
        </header>
        <div className='content_whole_wrap'>
        <main className="content-wrapper">
          <BannerImages />          
          <ServiceIntro />
        </main>
        </div>
      </div>
    </div>
  );
}

export default Home;