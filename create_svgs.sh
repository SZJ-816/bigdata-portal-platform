#!/bin/bash
mkdir -p /usr/share/nginx/html/images

cat > /usr/share/nginx/html/images/channel-ai.svg << 'SVGEOF'
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 400">
  <defs>
    <linearGradient id="bg" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" style="stop-color:#0f0c29"/>
      <stop offset="50%" style="stop-color:#302b63"/>
      <stop offset="100%" style="stop-color:#24243e"/>
    </linearGradient>
    <linearGradient id="glow" x1="0%" y1="0%" x2="100%" y2="0%">
      <stop offset="0%" style="stop-color:#00d2ff"/>
      <stop offset="100%" style="stop-color:#7b2ff7"/>
    </linearGradient>
  </defs>
  <rect width="800" height="400" fill="url(#bg)"/>
  <circle cx="400" cy="180" r="60" fill="none" stroke="url(#glow)" stroke-width="3"/>
  <circle cx="400" cy="180" r="40" fill="none" stroke="url(#glow)" stroke-width="2" opacity="0.6"/>
  <circle cx="400" cy="180" r="8" fill="#00d2ff"/>
  <line x1="400" y1="120" x2="400" y2="80" stroke="#00d2ff" stroke-width="2" opacity="0.5"/>
  <line x1="400" y1="240" x2="400" y2="280" stroke="#00d2ff" stroke-width="2" opacity="0.5"/>
  <line x1="340" y1="180" x2="300" y2="180" stroke="#00d2ff" stroke-width="2" opacity="0.5"/>
  <line x1="460" y1="180" x2="500" y2="180" stroke="#00d2ff" stroke-width="2" opacity="0.5"/>
  <line x1="358" y1="138" x2="330" y2="110" stroke="#7b2ff7" stroke-width="2" opacity="0.5"/>
  <line x1="442" y1="138" x2="470" y2="110" stroke="#7b2ff7" stroke-width="2" opacity="0.5"/>
  <line x1="358" y1="222" x2="330" y2="250" stroke="#7b2ff7" stroke-width="2" opacity="0.5"/>
  <line x1="442" y1="222" x2="470" y2="250" stroke="#7b2ff7" stroke-width="2" opacity="0.5"/>
  <circle cx="400" cy="80" r="4" fill="#00d2ff" opacity="0.7"/>
  <circle cx="400" cy="280" r="4" fill="#00d2ff" opacity="0.7"/>
  <circle cx="300" cy="180" r="4" fill="#00d2ff" opacity="0.7"/>
  <circle cx="500" cy="180" r="4" fill="#00d2ff" opacity="0.7"/>
  <circle cx="330" cy="110" r="4" fill="#7b2ff7" opacity="0.7"/>
  <circle cx="470" cy="110" r="4" fill="#7b2ff7" opacity="0.7"/>
  <circle cx="330" cy="250" r="4" fill="#7b2ff7" opacity="0.7"/>
  <circle cx="470" cy="250" r="4" fill="#7b2ff7" opacity="0.7"/>
  <text x="400" y="340" text-anchor="middle" fill="white" font-family="Arial,sans-serif" font-size="28" font-weight="bold" opacity="0.9">AI · 人工智能</text>
</svg>
SVGEOF

cat > /usr/share/nginx/html/images/channel-bigdata.svg << 'SVGEOF'
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 400">
  <defs>
    <linearGradient id="bg" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" style="stop-color:#0a192f"/>
      <stop offset="100%" style="stop-color:#112240"/>
    </linearGradient>
    <linearGradient id="bar" x1="0%" y1="100%" x2="0%" y2="0%">
      <stop offset="0%" style="stop-color:#64ffda"/>
      <stop offset="100%" style="stop-color:#00b4d8"/>
    </linearGradient>
  </defs>
  <rect width="800" height="400" fill="url(#bg)"/>
  <rect x="220" y="220" width="40" height="80" rx="4" fill="url(#bar)" opacity="0.9"/>
  <rect x="280" y="160" width="40" height="140" rx="4" fill="url(#bar)" opacity="0.9"/>
  <rect x="340" y="120" width="40" height="180" rx="4" fill="url(#bar)" opacity="0.9"/>
  <rect x="400" y="80" width="40" height="220" rx="4" fill="url(#bar)" opacity="0.9"/>
  <rect x="460" y="140" width="40" height="160" rx="4" fill="url(#bar)" opacity="0.9"/>
  <rect x="520" y="180" width="40" height="120" rx="4" fill="url(#bar)" opacity="0.9"/>
  <line x1="200" y1="300" x2="580" y2="300" stroke="#64ffda" stroke-width="2" opacity="0.3"/>
  <circle cx="240" cy="220" r="3" fill="#64ffda" opacity="0.6"/>
  <circle cx="300" cy="160" r="3" fill="#64ffda" opacity="0.6"/>
  <circle cx="360" cy="120" r="3" fill="#64ffda" opacity="0.6"/>
  <circle cx="420" cy="80" r="3" fill="#64ffda" opacity="0.6"/>
  <circle cx="480" cy="140" r="3" fill="#64ffda" opacity="0.6"/>
  <circle cx="540" cy="180" r="3" fill="#64ffda" opacity="0.6"/>
  <polyline points="240,220 300,160 360,120 420,80 480,140 540,180" fill="none" stroke="#64ffda" stroke-width="2" opacity="0.4"/>
  <text x="400" y="350" text-anchor="middle" fill="white" font-family="Arial,sans-serif" font-size="28" font-weight="bold" opacity="0.9">大数据 · Big Data</text>
</svg>
SVGEOF

cat > /usr/share/nginx/html/images/channel-cloud.svg << 'SVGEOF'
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 400">
  <defs>
    <linearGradient id="bg" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" style="stop-color:#1a1a2e"/>
      <stop offset="100%" style="stop-color:#16213e"/>
    </linearGradient>
    <linearGradient id="cloud" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" style="stop-color:#e94560"/>
      <stop offset="100%" style="stop-color:#0f3460"/>
    </linearGradient>
  </defs>
  <rect width="800" height="400" fill="url(#bg)"/>
  <ellipse cx="400" cy="160" rx="120" ry="60" fill="none" stroke="#e94560" stroke-width="3" opacity="0.8"/>
  <ellipse cx="400" cy="160" rx="80" ry="40" fill="none" stroke="#e94560" stroke-width="2" opacity="0.5"/>
  <rect x="360" y="160" width="80" height="60" rx="4" fill="none" stroke="#0f3460" stroke-width="2" opacity="0.6"/>
  <line x1="380" y1="170" x2="380" y2="210" stroke="#e94560" stroke-width="1.5" opacity="0.4"/>
  <line x1="400" y1="170" x2="400" y2="210" stroke="#e94560" stroke-width="1.5" opacity="0.4"/>
  <line x1="420" y1="170" x2="420" y2="210" stroke="#e94560" stroke-width="1.5" opacity="0.4"/>
  <circle cx="300" cy="100" r="6" fill="#e94560" opacity="0.3"/>
  <circle cx="500" cy="100" r="6" fill="#e94560" opacity="0.3"/>
  <circle cx="280" cy="200" r="6" fill="#0f3460" opacity="0.3"/>
  <circle cx="520" cy="200" r="6" fill="#0f3460" opacity="0.3"/>
  <line x1="306" y1="100" x2="340" y2="140" stroke="#e94560" stroke-width="1" opacity="0.3"/>
  <line x1="494" y1="100" x2="460" y2="140" stroke="#e94560" stroke-width="1" opacity="0.3"/>
  <line x1="286" y1="200" x2="340" y2="180" stroke="#0f3460" stroke-width="1" opacity="0.3"/>
  <line x1="514" y1="200" x2="460" y2="180" stroke="#0f3460" stroke-width="1" opacity="0.3"/>
  <text x="400" y="310" text-anchor="middle" fill="white" font-family="Arial,sans-serif" font-size="28" font-weight="bold" opacity="0.9">云计算 · Cloud</text>
</svg>
SVGEOF

cat > /usr/share/nginx/html/images/channel-internet.svg << 'SVGEOF'
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 400">
  <defs>
    <linearGradient id="bg" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" style="stop-color:#0d1b2a"/>
      <stop offset="100%" style="stop-color:#1b263b"/>
    </linearGradient>
  </defs>
  <rect width="800" height="400" fill="url(#bg)"/>
  <circle cx="400" cy="160" r="80" fill="none" stroke="#48cae4" stroke-width="2" opacity="0.6"/>
  <ellipse cx="400" cy="160" rx="80" ry="30" fill="none" stroke="#48cae4" stroke-width="1.5" opacity="0.5"/>
  <line x1="400" y1="80" x2="400" y2="240" stroke="#48cae4" stroke-width="1.5" opacity="0.4"/>
  <ellipse cx="400" cy="160" rx="40" ry="15" fill="none" stroke="#90e0ef" stroke-width="1" opacity="0.4"/>
  <circle cx="400" cy="80" r="5" fill="#48cae4" opacity="0.8"/>
  <circle cx="400" cy="240" r="5" fill="#48cae4" opacity="0.8"/>
  <circle cx="320" cy="160" r="5" fill="#48cae4" opacity="0.8"/>
  <circle cx="480" cy="160" r="5" fill="#48cae4" opacity="0.8"/>
  <circle cx="340" cy="110" r="4" fill="#90e0ef" opacity="0.6"/>
  <circle cx="460" cy="110" r="4" fill="#90e0ef" opacity="0.6"/>
  <circle cx="340" cy="210" r="4" fill="#90e0ef" opacity="0.6"/>
  <circle cx="460" cy="210" r="4" fill="#90e0ef" opacity="0.6"/>
  <text x="400" y="330" text-anchor="middle" fill="white" font-family="Arial,sans-serif" font-size="28" font-weight="bold" opacity="0.9">互联网 · Internet</text>
</svg>
SVGEOF

cat > /usr/share/nginx/html/images/channel-hardware.svg << 'SVGEOF'
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 400">
  <defs>
    <linearGradient id="bg" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" style="stop-color:#1a1a2e"/>
      <stop offset="100%" style="stop-color:#2d2d44"/>
    </linearGradient>
    <linearGradient id="chip" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" style="stop-color:#f77f00"/>
      <stop offset="100%" style="stop-color:#fcbf49"/>
    </linearGradient>
  </defs>
  <rect width="800" height="400" fill="url(#bg)"/>
  <rect x="330" y="100" width="140" height="140" rx="8" fill="none" stroke="url(#chip)" stroke-width="3"/>
  <rect x="355" y="125" width="90" height="90" rx="4" fill="none" stroke="#f77f00" stroke-width="1.5" opacity="0.5"/>
  <rect x="375" y="145" width="50" height="50" rx="2" fill="#f77f00" opacity="0.2"/>
  <line x1="360" y1="100" x2="360" y2="70" stroke="#fcbf49" stroke-width="2" opacity="0.6"/>
  <line x1="390" y1="100" x2="390" y2="70" stroke="#fcbf49" stroke-width="2" opacity="0.6"/>
  <line x1="420" y1="100" x2="420" y2="70" stroke="#fcbf49" stroke-width="2" opacity="0.6"/>
  <line x1="450" y1="100" x2="450" y2="70" stroke="#fcbf49" stroke-width="2" opacity="0.6"/>
  <line x1="360" y1="240" x2="360" y2="270" stroke="#fcbf49" stroke-width="2" opacity="0.6"/>
  <line x1="390" y1="240" x2="390" y2="270" stroke="#fcbf49" stroke-width="2" opacity="0.6"/>
  <line x1="420" y1="240" x2="420" y2="270" stroke="#fcbf49" stroke-width="2" opacity="0.6"/>
  <line x1="450" y1="240" x2="450" y2="270" stroke="#fcbf49" stroke-width="2" opacity="0.6"/>
  <line x1="330" y1="130" x2="300" y2="130" stroke="#fcbf49" stroke-width="2" opacity="0.6"/>
  <line x1="330" y1="160" x2="300" y2="160" stroke="#fcbf49" stroke-width="2" opacity="0.6"/>
  <line x1="330" y1="190" x2="300" y2="190" stroke="#fcbf49" stroke-width="2" opacity="0.6"/>
  <line x1="330" y1="220" x2="300" y2="220" stroke="#fcbf49" stroke-width="2" opacity="0.6"/>
  <line x1="470" y1="130" x2="500" y2="130" stroke="#fcbf49" stroke-width="2" opacity="0.6"/>
  <line x1="470" y1="160" x2="500" y2="160" stroke="#fcbf49" stroke-width="2" opacity="0.6"/>
  <line x1="470" y1="190" x2="500" y2="190" stroke="#fcbf49" stroke-width="2" opacity="0.6"/>
  <line x1="470" y1="220" x2="500" y2="220" stroke="#fcbf49" stroke-width="2" opacity="0.6"/>
  <text x="400" y="330" text-anchor="middle" fill="white" font-family="Arial,sans-serif" font-size="28" font-weight="bold" opacity="0.9">硬件 · Hardware</text>
</svg>
SVGEOF

cat > /usr/share/nginx/html/images/channel-startup.svg << 'SVGEOF'
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 400">
  <defs>
    <linearGradient id="bg" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" style="stop-color:#1b4332"/>
      <stop offset="100%" style="stop-color:#2d6a4f"/>
    </linearGradient>
    <linearGradient id="rocket" x1="0%" y1="100%" x2="0%" y2="0%">
      <stop offset="0%" style="stop-color:#ff6b6b"/>
      <stop offset="100%" style="stop-color:#ffd93d"/>
    </linearGradient>
  </defs>
  <rect width="800" height="400" fill="url(#bg)"/>
  <polygon points="400,80 420,180 400,160 380,180" fill="url(#rocket)" opacity="0.9"/>
  <polygon points="380,180 370,210 390,190" fill="#ff6b6b" opacity="0.6"/>
  <polygon points="420,180 430,210 410,190" fill="#ff6b6b" opacity="0.6"/>
  <ellipse cx="400" cy="220" rx="20" ry="8" fill="#ffd93d" opacity="0.4"/>
  <ellipse cx="400" cy="240" rx="30" ry="10" fill="#ff6b6b" opacity="0.3"/>
  <ellipse cx="400" cy="260" rx="40" ry="12" fill="#ff6b6b" opacity="0.2"/>
  <circle cx="400" cy="130" r="8" fill="white" opacity="0.7"/>
  <line x1="340" y1="300" x2="460" y2="300" stroke="#95d5b2" stroke-width="3" opacity="0.5"/>
  <polygon points="340,300 350,290 360,300" fill="#95d5b2" opacity="0.4"/>
  <polygon points="440,300 450,290 460,300" fill="#95d5b2" opacity="0.4"/>
  <text x="400" y="350" text-anchor="middle" fill="white" font-family="Arial,sans-serif" font-size="28" font-weight="bold" opacity="0.9">创业 · Startup</text>
</svg>
SVGEOF

echo "SVG images created:"
ls -la /usr/share/nginx/html/images/channel-*.svg
