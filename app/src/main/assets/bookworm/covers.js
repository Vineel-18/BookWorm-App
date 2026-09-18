// ── Book Cover SVGs — one per book id ──────────────────────────────
// Each value is a complete inline SVG string used as the cover image.

const BOOK_COVERS = {

  // 1 — The Art of Focus
  1: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 170">
    <defs><linearGradient id="g1" x1="0" y1="0" x2="1" y2="1"><stop offset="0%" stop-color="#c0392b"/><stop offset="100%" stop-color="#7b241c"/></linearGradient></defs>
    <rect width="120" height="170" fill="url(#g1)"/>
    <circle cx="60" cy="72" r="38" fill="none" stroke="rgba(255,255,255,0.15)" stroke-width="2"/>
    <circle cx="60" cy="72" r="26" fill="none" stroke="rgba(255,255,255,0.25)" stroke-width="2"/>
    <circle cx="60" cy="72" r="12" fill="rgba(255,255,255,0.9)"/>
    <circle cx="60" cy="72" r="4" fill="#c0392b"/>
    <line x1="60" y1="34" x2="60" y2="44" stroke="rgba(255,255,255,0.5)" stroke-width="1.5"/>
    <line x1="60" y1="100" x2="60" y2="110" stroke="rgba(255,255,255,0.5)" stroke-width="1.5"/>
    <line x1="22" y1="72" x2="32" y2="72" stroke="rgba(255,255,255,0.5)" stroke-width="1.5"/>
    <line x1="88" y1="72" x2="98" y2="72" stroke="rgba(255,255,255,0.5)" stroke-width="1.5"/>
    <rect x="8" y="128" width="104" height="1" fill="rgba(255,255,255,0.3)"/>
    <text x="60" y="143" font-family="Georgia,serif" font-size="9" font-weight="bold" fill="white" text-anchor="middle" letter-spacing="2">THE ART OF</text>
    <text x="60" y="156" font-family="Georgia,serif" font-size="11" font-weight="bold" fill="white" text-anchor="middle" letter-spacing="3">FOCUS</text>
    <text x="60" y="166" font-family="Arial,sans-serif" font-size="5.5" fill="rgba(255,255,255,0.6)" text-anchor="middle" letter-spacing="1.5">ARJUN PATEL</text>
  </svg>`,

  // 2 — The Art of Learning
  2: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 170">
    <defs><linearGradient id="g2" x1="0" y1="0" x2="1" y2="1"><stop offset="0%" stop-color="#c0392b"/><stop offset="100%" stop-color="#922b21"/></linearGradient></defs>
    <rect width="120" height="170" fill="url(#g2)"/>
    <rect x="20" y="18" width="80" height="6" rx="3" fill="rgba(255,255,255,0.15)"/>
    <rect x="28" y="30" width="64" height="5" rx="2.5" fill="rgba(255,255,255,0.12)"/>
    <rect x="36" y="42" width="48" height="5" rx="2.5" fill="rgba(255,255,255,0.09)"/>
    <circle cx="60" cy="88" r="28" fill="rgba(255,255,255,0.1)" stroke="rgba(255,255,255,0.3)" stroke-width="1.5"/>
    <text x="60" y="84" font-family="Georgia,serif" font-size="22" fill="rgba(255,255,255,0.9)" text-anchor="middle">💡</text>
    <path d="M42 108 Q60 96 78 108" fill="none" stroke="rgba(255,255,255,0.4)" stroke-width="1.5"/>
    <rect x="8" y="122" width="104" height="1" fill="rgba(255,255,255,0.3)"/>
    <text x="60" y="136" font-family="Georgia,serif" font-size="9" font-weight="bold" fill="white" text-anchor="middle" letter-spacing="2">THE ART OF</text>
    <text x="60" y="150" font-family="Georgia,serif" font-size="10" font-weight="bold" fill="white" text-anchor="middle" letter-spacing="2">LEARNING</text>
    <text x="60" y="163" font-family="Arial,sans-serif" font-size="5.5" fill="rgba(255,255,255,0.6)" text-anchor="middle" letter-spacing="1.5">RAJ PATEL</text>
  </svg>`,

  // 3 — The Path to Success
  3: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 170">
    <defs><linearGradient id="g3" x1="0" y1="1" x2="1" y2="0"><stop offset="0%" stop-color="#1a6b2e"/><stop offset="100%" stop-color="#27ae60"/></linearGradient></defs>
    <rect width="120" height="170" fill="url(#g3)"/>
    <polyline points="10,130 35,95 55,108 80,65 110,30" fill="none" stroke="rgba(255,255,255,0.35)" stroke-width="2" stroke-dasharray="4,3"/>
    <circle cx="110" cy="30" r="6" fill="rgba(255,255,255,0.9)"/>
    <polygon points="108,26 114,30 108,34" fill="#27ae60"/>
    <circle cx="80" cy="65" r="4" fill="rgba(255,255,255,0.7)"/>
    <circle cx="55" cy="108" r="4" fill="rgba(255,255,255,0.7)"/>
    <circle cx="35" cy="95" r="4" fill="rgba(255,255,255,0.7)"/>
    <circle cx="10" cy="130" r="4" fill="rgba(255,255,255,0.5)"/>
    <rect x="8" y="138" width="104" height="1" fill="rgba(255,255,255,0.3)"/>
    <text x="60" y="151" font-family="Georgia,serif" font-size="8.5" font-weight="bold" fill="white" text-anchor="middle" letter-spacing="1.5">THE PATH TO</text>
    <text x="60" y="163" font-family="Georgia,serif" font-size="10" font-weight="bold" fill="white" text-anchor="middle" letter-spacing="2.5">SUCCESS</text>
  </svg>`,

  // 4 — The Midnight Hour
  4: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 170">
    <defs><linearGradient id="g4" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#0a0a1a"/><stop offset="100%" stop-color="#1a1a3e"/></linearGradient></defs>
    <rect width="120" height="170" fill="url(#g4)"/>
    <circle cx="60" cy="55" r="28" fill="none" stroke="rgba(255,255,200,0.15)" stroke-width="1"/>
    <path d="M52 35 A22 22 0 1 0 78 55 A16 16 0 1 1 52 35 Z" fill="rgba(255,255,200,0.85)"/>
    <circle cx="30" cy="25" r="1.5" fill="white" opacity="0.8"/>
    <circle cx="90" cy="18" r="1" fill="white" opacity="0.7"/>
    <circle cx="15" cy="60" r="1" fill="white" opacity="0.6"/>
    <circle cx="105" cy="45" r="1.5" fill="white" opacity="0.8"/>
    <circle cx="45" cy="12" r="1" fill="white" opacity="0.5"/>
    <rect x="30" y="90" width="60" height="40" rx="2" fill="rgba(255,255,255,0.05)" stroke="rgba(255,255,255,0.1)" stroke-width="1"/>
    <rect x="36" y="96" width="20" height="14" rx="1" fill="rgba(255,180,0,0.2)"/>
    <rect x="64" y="96" width="20" height="14" rx="1" fill="rgba(255,180,0,0.1)"/>
    <text x="60" y="148" font-family="Georgia,serif" font-size="8.5" font-weight="bold" fill="rgba(255,255,200,0.9)" text-anchor="middle" letter-spacing="1">THE MIDNIGHT</text>
    <text x="60" y="161" font-family="Georgia,serif" font-size="10" font-weight="bold" fill="rgba(255,255,200,0.9)" text-anchor="middle" letter-spacing="2">HOUR</text>
  </svg>`,

  // 5 — Beneath the Stars
  5: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 170">
    <defs><linearGradient id="g5" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#2c0a4a"/><stop offset="100%" stop-color="#8e44ad"/></linearGradient></defs>
    <rect width="120" height="170" fill="url(#g5)"/>
    <circle cx="18" cy="20" r="1.5" fill="white" opacity="0.9"/>
    <circle cx="45" cy="12" r="1" fill="white" opacity="0.7"/>
    <circle cx="70" cy="8" r="2" fill="white" opacity="0.8"/>
    <circle cx="95" cy="22" r="1.5" fill="white" opacity="0.9"/>
    <circle cx="108" cy="10" r="1" fill="white" opacity="0.6"/>
    <circle cx="30" cy="35" r="1" fill="white" opacity="0.5"/>
    <circle cx="85" cy="38" r="1" fill="white" opacity="0.7"/>
    <path d="M70 8 L72 14 L78 14 L73 18 L75 24 L70 20 L65 24 L67 18 L62 14 L68 14 Z" fill="rgba(255,255,180,0.9)" transform="scale(0.7) translate(30,4)"/>
    <ellipse cx="60" cy="105" rx="45" ry="30" fill="rgba(255,255,255,0.05)" stroke="rgba(255,255,255,0.1)" stroke-width="1"/>
    <path d="M25 115 Q45 85 60 90 Q75 95 95 80" fill="none" stroke="rgba(255,150,200,0.6)" stroke-width="1.5"/>
    <circle cx="60" cy="90" r="3" fill="rgba(255,150,200,0.8)"/>
    <text x="60" y="146" font-family="Georgia,serif" font-size="8.5" font-weight="bold" fill="rgba(255,220,255,0.95)" text-anchor="middle" letter-spacing="1.5">BENEATH</text>
    <text x="60" y="158" font-family="Georgia,serif" font-size="9.5" font-weight="bold" fill="rgba(255,220,255,0.95)" text-anchor="middle" letter-spacing="2">THE STARS</text>
    <text x="60" y="168" font-family="Arial,sans-serif" font-size="5.5" fill="rgba(255,255,255,0.5)" text-anchor="middle" letter-spacing="1.5">JESSICA MARTIN</text>
  </svg>`,

  // 6 — The Final Frontier
  6: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 170">
    <defs><radialGradient id="g6" cx="50%" cy="40%"><stop offset="0%" stop-color="#1a4a7a"/><stop offset="100%" stop-color="#040d1a"/></radialGradient></defs>
    <rect width="120" height="170" fill="url(#g6)"/>
    <circle cx="60" cy="60" r="22" fill="rgba(100,180,255,0.15)" stroke="rgba(100,180,255,0.3)" stroke-width="1"/>
    <ellipse cx="60" cy="60" rx="36" ry="12" fill="none" stroke="rgba(100,180,255,0.25)" stroke-width="1" transform="rotate(-20,60,60)"/>
    <path d="M50 55 L60 35 L70 55 L80 50 L60 75 L40 50 Z" fill="rgba(200,230,255,0.85)" stroke="rgba(100,180,255,0.5)" stroke-width="0.5"/>
    <circle cx="20" cy="20" r="1" fill="white" opacity="0.7"/>
    <circle cx="95" cy="15" r="1.5" fill="white" opacity="0.8"/>
    <circle cx="105" cy="50" r="1" fill="white" opacity="0.6"/>
    <circle cx="12" cy="80" r="1" fill="white" opacity="0.5"/>
    <text x="60" y="108" font-family="Arial,sans-serif" font-size="7" fill="rgba(150,200,255,0.7)" text-anchor="middle" letter-spacing="1">★ ★ ★ ★ ★</text>
    <text x="60" y="128" font-family="Georgia,serif" font-size="8" font-weight="bold" fill="rgba(180,220,255,0.95)" text-anchor="middle" letter-spacing="1.5">THE FINAL</text>
    <text x="60" y="142" font-family="Georgia,serif" font-size="10" font-weight="bold" fill="rgba(180,220,255,0.95)" text-anchor="middle" letter-spacing="2">FRONTIER</text>
    <text x="60" y="155" font-family="Arial,sans-serif" font-size="5.5" fill="rgba(255,255,255,0.5)" text-anchor="middle" letter-spacing="1.5">LAURA MITCHELL</text>
  </svg>`,

  // 7 — Joy of Minimalism
  7: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 170">
    <rect width="120" height="170" fill="#f5f0e8"/>
    <rect x="0" y="0" width="120" height="170" fill="#f5f0e8"/>
    <rect x="30" y="22" width="60" height="1.5" fill="#333"/>
    <rect x="42" y="50" width="36" height="36" rx="2" fill="none" stroke="#555" stroke-width="1.5"/>
    <rect x="50" y="58" width="20" height="20" rx="1" fill="#e8943a" opacity="0.8"/>
    <rect x="56" y="64" width="8" height="8" rx="1" fill="#333"/>
    <rect x="30" y="105" width="60" height="1" fill="#bbb"/>
    <text x="60" y="122" font-family="Georgia,serif" font-size="9" fill="#333" text-anchor="middle" letter-spacing="3" font-weight="bold">JOY OF</text>
    <text x="60" y="136" font-family="Georgia,serif" font-size="8" fill="#555" text-anchor="middle" letter-spacing="2">MINIMALISM</text>
    <text x="60" y="152" font-family="Arial,sans-serif" font-size="5.5" fill="#888" text-anchor="middle" letter-spacing="2">DANIEL REED</text>
  </svg>`,

  // 8 — The Vanishing House
  8: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 170">
    <defs><linearGradient id="g8" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#0d1117"/><stop offset="100%" stop-color="#2c3e50"/></linearGradient></defs>
    <rect width="120" height="170" fill="url(#g8)"/>
    <polygon points="60,30 90,60 30,60" fill="rgba(255,255,255,0.08)" stroke="rgba(255,255,255,0.2)" stroke-width="1"/>
    <rect x="38" y="60" width="44" height="38" fill="rgba(255,255,255,0.05)" stroke="rgba(255,255,255,0.15)" stroke-width="1"/>
    <rect x="50" y="75" width="12" height="18" rx="1" fill="rgba(255,180,0,0.15)"/>
    <rect x="68" y="75" width="10" height="10" rx="1" fill="rgba(255,180,0,0.1)"/>
    <path d="M0 98 Q30 90 60 98 Q90 106 120 98 L120 170 L0 170 Z" fill="rgba(255,255,255,0.03)"/>
    <circle cx="60" cy="45" r="8" fill="rgba(255,255,255,0)" stroke="rgba(255,200,100,0.4)" stroke-width="1" stroke-dasharray="3,2"/>
    <text x="60" y="120" font-family="Georgia,serif" font-size="7.5" font-weight="bold" fill="rgba(200,210,230,0.9)" text-anchor="middle" letter-spacing="1">THE VANISHING</text>
    <text x="60" y="134" font-family="Georgia,serif" font-size="10" font-weight="bold" fill="rgba(200,210,230,0.9)" text-anchor="middle" letter-spacing="2">HOUSE</text>
    <text x="60" y="148" font-family="Arial,sans-serif" font-size="5.5" fill="rgba(255,255,255,0.4)" text-anchor="middle" letter-spacing="1.5">CLARA NELSON</text>
  </svg>`,

  // 9 — The Lost Kitten
  9: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 170">
    <defs><linearGradient id="g9" x1="0" y1="0" x2="1" y2="1"><stop offset="0%" stop-color="#e67e22"/><stop offset="100%" stop-color="#f39c12"/></linearGradient></defs>
    <rect width="120" height="170" fill="url(#g9)"/>
    <circle cx="60" cy="68" r="30" fill="rgba(255,255,255,0.12)"/>
    <ellipse cx="60" cy="68" rx="16" ry="14" fill="rgba(255,255,255,0.9)"/>
    <polygon points="44,54 48,42 54,54" fill="rgba(255,255,255,0.9)"/>
    <polygon points="66,54 72,42 76,54" fill="rgba(255,255,255,0.9)"/>
    <ellipse cx="52" cy="62" rx="4" ry="5" fill="#333"/>
    <ellipse cx="68" cy="62" rx="4" ry="5" fill="#333"/>
    <circle cx="53" cy="61" r="2" fill="white"/>
    <circle cx="69" cy="61" r="2" fill="white"/>
    <ellipse cx="60" cy="71" rx="3" ry="2" fill="rgba(255,150,150,0.8)"/>
    <path d="M48 74 Q55 78 60 76 Q65 78 72 74" fill="none" stroke="rgba(100,100,100,0.5)" stroke-width="1"/>
    <rect x="8" y="118" width="104" height="1" fill="rgba(255,255,255,0.3)"/>
    <text x="60" y="132" font-family="Arial,sans-serif" font-size="8.5" font-weight="bold" fill="white" text-anchor="middle" letter-spacing="1">THE LOST</text>
    <text x="60" y="146" font-family="Arial,sans-serif" font-size="10" font-weight="bold" fill="white" text-anchor="middle" letter-spacing="2">KITTEN</text>
    <text x="60" y="160" font-family="Arial,sans-serif" font-size="5.5" fill="rgba(255,255,255,0.7)" text-anchor="middle" letter-spacing="1.5">EMILY PARKER</text>
  </svg>`,

  // 10 — The Silent Witness
  10: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 170">
    <defs><linearGradient id="g10" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#1a2332"/><stop offset="100%" stop-color="#2c3e50"/></linearGradient></defs>
    <rect width="120" height="170" fill="url(#g10)"/>
    <ellipse cx="60" cy="60" rx="20" ry="26" fill="rgba(255,255,255,0.08)" stroke="rgba(255,255,255,0.15)" stroke-width="1"/>
    <circle cx="53" cy="55" r="4" fill="rgba(255,255,255,0.6)"/>
    <circle cx="67" cy="55" r="4" fill="rgba(255,255,255,0.6)"/>
    <path d="M50 68 Q60 74 70 68" fill="none" stroke="rgba(255,255,255,0.3)" stroke-width="1.5"/>
    <line x1="60" y1="86" x2="60" y2="100" stroke="rgba(255,255,255,0.2)" stroke-width="1"/>
    <path d="M40 15 L45 22 M50 12 L50 20 M60 10 L60 18 M70 12 L70 20 M80 15 L75 22" stroke="rgba(255,220,100,0.5)" stroke-width="1" fill="none"/>
    <line x1="0" y1="105" x2="120" y2="105" stroke="rgba(255,255,255,0.08)" stroke-width="1"/>
    <text x="60" y="122" font-family="Georgia,serif" font-size="7.5" font-weight="bold" fill="rgba(180,200,220,0.9)" text-anchor="middle" letter-spacing="1.5">THE SILENT</text>
    <text x="60" y="136" font-family="Georgia,serif" font-size="10" font-weight="bold" fill="rgba(180,200,220,0.9)" text-anchor="middle" letter-spacing="2">WITNESS</text>
    <text x="60" y="150" font-family="Arial,sans-serif" font-size="5.5" fill="rgba(255,255,255,0.4)" text-anchor="middle" letter-spacing="1.5">PRIYA SHARMA</text>
  </svg>`,

};
