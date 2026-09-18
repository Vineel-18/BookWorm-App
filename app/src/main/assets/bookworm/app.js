// ────────────────────────────────────────────────
//  UTILITIES
// ────────────────────────────────────────────────
function getUser()    { return JSON.parse(localStorage.getItem('bw_user')    || 'null'); }
function getCart()    { return JSON.parse(localStorage.getItem('bw_cart')    || '[]');  }
function getWL()      { return JSON.parse(localStorage.getItem('bw_wl')      || '[]');  }
function getOrders()  { return JSON.parse(localStorage.getItem('bw_orders')  || '[]');  }
function isPremium()  {
  const p = JSON.parse(localStorage.getItem('bw_premium') || 'null');
  return p && p.expiry > Date.now();
}
function isAdmin() { const u = getUser(); return u && u.role === 'admin'; }
function requireAdmin() {
  const u = getUser();
  if (!u) { window.location.href = 'login.html'; return; }
  if (u.role !== 'admin') { window.location.href = 'index.html'; }
}

// Merge base BOOKS with admin-added books from localStorage
function getAllBooks() {
  const custom = JSON.parse(localStorage.getItem('bw_custom_books') || '[]');
  // custom books override base books with same id; new ones are appended
  const base = BOOKS.filter(b => !custom.find(c => c.id === b.id && c._deleted));
  const adds = custom.filter(c => !c._deleted);
  // replace edited base books
  return [...base.map(b => { const ov = adds.find(c => c.id === b.id); return ov || b; }),
          ...adds.filter(c => !BOOKS.find(b => b.id === c.id))];
}
function saveCustomBooks(list) { localStorage.setItem('bw_custom_books', JSON.stringify(list)); }
function getCustomBooks()      { return JSON.parse(localStorage.getItem('bw_custom_books') || '[]'); }

function saveCart(c) {
  localStorage.setItem('bw_cart', JSON.stringify(c));
  refreshBadge();
}

function addToCart(bookId) {
  const c = getCart();
  const x = c.find(i => i.id === bookId);
  if (x) x.qty++;
  else c.push({ id: bookId, qty: 1 });
  saveCart(c);
  showToast('Added to cart ✓');
}

function refreshBadge() {
  const b = document.getElementById('cartBadge');
  if (!b) return;
  const n = getCart().reduce((s, i) => s + i.qty, 0);
  b.textContent = n;
  b.style.display = n > 0 ? 'flex' : 'none';
}

function showToast(msg) {
  const t = document.getElementById('toast');
  if (!t) return;
  t.textContent = msg;
  t.classList.add('show');
  clearTimeout(t._t);
  t._t = setTimeout(() => t.classList.remove('show'), 2300);
}

function requireAuth() {
  if (!getUser()) window.location.href = 'login.html';
}

function doLogout() {
  localStorage.removeItem('bw_user');
  window.location.href = 'login.html';
}

// User dropdown
function toggleDD() {
  const menu = document.getElementById('userMenu');
  if (!menu) return;
  menu.classList.toggle('open');
  // Refresh gift points value each time dropdown opens
  if (menu.classList.contains('open')) {
    const pts   = parseInt(localStorage.getItem('bw_gift_points') || '0');
    const strip = menu.querySelector('.udp-gift-strip');
    if (strip) {
      strip.querySelector('.udp-gift-val').textContent = pts + ' pts';
      strip.querySelector('.udp-gift-sub').textContent = '≈ ₹' + pts + ' value';
    }
  }
}
document.addEventListener('click', e => {
  if (!e.target.closest('.user-dd-wrap'))
    document.getElementById('userMenu')?.classList.remove('open');
});

function initNavbar() {
  const u = getUser();
  if (u) {
    const initial = (u.name || 'U').charAt(0).toUpperCase();
    // Small avatar in navbar button
    const navAv = document.getElementById('navAvatar');
    if (navAv) navAv.textContent = initial;
    // Large avatar in dropdown
    const udpAv = document.getElementById('udpAvatar');
    if (udpAv) udpAv.textContent = initial;
    // Name & email
    const n = document.getElementById('uName');
    const e = document.getElementById('uEmail');
    if (n) n.textContent = u.name || 'User';
    if (e) e.textContent = u.email || '';
    // Premium badge
    const badge = document.getElementById('udpPremBadge');
    if (badge) {
      if (isPremium()) {
        badge.style.display = 'inline-block';
        badge.innerHTML = '👑 Premium';
        badge.className = 'udp-prem-badge';
      } else {
        badge.style.display = 'none';
      }
    }
    // Gift points strip — inject between profile header and links
    const menu = document.getElementById('userMenu');
    if (menu) {
      const pts   = parseInt(localStorage.getItem('bw_gift_points') || '0');
      let strip   = menu.querySelector('.udp-gift-strip');
      if (!strip) {
        strip = document.createElement('div');
        strip.className = 'udp-gift-strip';
        const links = menu.querySelector('.user-dd-links');
        if (links) menu.insertBefore(strip, links);
      }
      // Always refresh the value so it reflects latest credits
      strip.innerHTML = `
        <div class="udp-gift-label"><span>🎁</span> Gift Points</div>
        <div>
          <div class="udp-gift-val">${pts} pts</div>
          <div class="udp-gift-sub">≈ ₹${pts} value</div>
        </div>`;
    }
  }
  refreshBadge();
}

// ────────────────────────────────────────────────
//  HOME PAGE
// ────────────────────────────────────────────────
let activeCat = 'All';

function initHome() {
  requireAuth();
  initNavbar();
  buildSidebar();
  renderHome();
}

function buildSidebar() {
  const el = document.getElementById('sidebar');
  if (!el) return;
  el.innerHTML = CATEGORIES.map(c =>
    `<div class="cat-item${c === activeCat ? ' active' : ''}" onclick="selectCat('${c.replace(/'/g,"\\'")}')">` +
    c + '</div>'
  ).join('');
}

function selectCat(cat) {
  activeCat = cat;
  document.querySelectorAll('.cat-item').forEach(el => {
    el.classList.toggle('active', el.textContent.trim() === cat);
  });
  renderHome();
}

function onSearch() { renderHome(); }
function onFilter() { renderHome(); }

function getFiltered() {
  const search = (document.getElementById('searchInput')?.value || '').toLowerCase().trim();
  // Support both custom-dropdown values (index.html) and native <select> fallback
  const fv = (typeof getFilterValues === 'function') ? getFilterValues() : {};
  const lang   = fv.lang   ?? (document.getElementById('fLang')?.value   || '');
  const format = fv.format ?? (document.getElementById('fFormat')?.value || '');
  const price  = fv.price  ?? (document.getElementById('fPrice')?.value  || '');
  const sort   = fv.sort   ?? (document.getElementById('fSort')?.value   || '');

  let books = getAllBooks().filter(b => {
    if (activeCat !== 'All' && b.category !== activeCat) return false;
    if (lang   && b.language !== lang)   return false;
    if (format && b.format   !== format) return false;
    if (price) {
      const [lo, hi] = price.split('-').map(Number);
      if (b.price < lo || b.price > hi) return false;
    }
    if (search && !b.title.toLowerCase().includes(search) &&
        !b.author.toLowerCase().includes(search)) return false;
    return true;
  });

  if (sort === 'price-asc')  books.sort((a, b) => a.price - b.price);
  if (sort === 'price-desc') books.sort((a, b) => b.price - a.price);
  if (sort === 'rating')     books.sort((a, b) => b.rating - a.rating);
  return books;
}

function renderHome() {
  const el = document.getElementById('mainContent');
  if (!el) return;

  const all    = getFiltered();
  const orders = getOrders();

  // Category selected → show all matching
  if (activeCat !== 'All') {
    el.innerHTML = all.length
      ? section(activeCat, all)
      : '<div class="empty">No books in this category.</div>';
    return;
  }

  // "All" view — curated sections (capped) + per-category previews
  let reco = all.filter(b => b.tags.includes('recommended'));
  // Personalise if order history exists
  if (orders.length > 0) {
    const bIds  = new Set(orders.flatMap(o => o.items.map(i => i.id)));
    const bCats = new Set(orders.flatMap(o => o.items.map(i => i.category)));
    const priority = [
      ...BOOKS.filter(b => bIds.has(b.id)),
      ...BOOKS.filter(b => !bIds.has(b.id) && bCats.has(b.category))
    ];
    reco = [...new Map([...priority, ...reco].map(b => [b.id, b])).values()];
  }

  const best = all.filter(b => b.tags.includes('bestseller'));
  const newL = all.filter(b => b.tags.includes('new'));

  let html = '';

  // Buy-again banner
  if (orders.length > 0) {
    const last = orders[0];
    html += `<div class="buy-again-bar">
      <span>🔄 Buy Again:</span>
      ${last.items.slice(0, 3).map(it =>
        `<span class="buy-again-chip" onclick="addToCart(${it.id})">${it.title}</span>`
      ).join('')}
      <a href="orders.html" style="margin-left:auto;font-size:11px;color:#e8943a">View All Orders →</a>
    </div>`;
  }

  // Curated sections only — no per-category previews
  if (reco.length) html += section('Recommended for You', reco.slice(0, 8));
  if (best.length) html += section('Bestsellers this Month', best.slice(0, 8));
  if (newL.length) html += section('New Launches', newL.slice(0, 8));

  if (!html) {
    html = '<div class="empty">No books found.</div>';
  }

  el.innerHTML = html;
}

function section(title, books) {
  return `<div class="section">
    <div class="section-title">${title}</div>
    <div class="book-row">${books.map(bookCard).join('')}</div>
  </div>`;
}

function sectionPreview(title, books, total) {
  const seeAll = total > 4
    ? `<span class="see-all-link" onclick="selectCat('${title.replace(/'/g,"\\'")}')">See all ${total} →</span>`
    : '';
  return `<div class="section">
    <div class="section-title-row">
      <span class="section-title">${title}</span>${seeAll}
    </div>
    <div class="book-row">${books.map(bookCard).join('')}</div>
  </div>`;
}

// Cover icon mapping per book
const COVER_ICONS = {
  // Self-help
  1: '🎯', 2: '💡', 3: '🌟', 7: '🏠',
  // Mystery
  4: '🌙', 10: '🔍', 11: '🌫️',
  // Romance
  5: '⭐', 12: '💐', 13: '💌',
  // Science Fiction
  6: '🚀', 14: '🤖', 15: '🛸',
  // Fantasy
  16: '👑', 17: '⚡',
  // Historical
  18: '🏛️', 19: '🗺️',
  // Biography
  20: '✈️', 21: '🕊️',
  // Memoir
  22: '📓', 23: '🏚️',
  // Travel
  24: '🌲', 25: '🎒',
  // Cooking
  26: '🌶️', 27: '🍰',
  // Children's
  9: '🐱', 28: '🦋', 29: '🎒',
  // Young Adult
  30: '☀️', 31: '💻',
  // Comics & Graphic Novels
  32: '🦸', 33: '🥭',
  // Poetry
  34: '🌊', 35: '✒️',
  // Drama
  36: '🌧️', 37: '🎭',
  // Science
  38: '🔭', 39: '🧬',
  // Philosophy
  40: '🪷', 41: '🕯️',
  // Religion
  42: '🧘', 43: '🌅',
  // Language Learning
  44: '🗣️', 45: '📝',
  // Fiction
  8: '👁️'
};

function bookCard(b) {
  const genre = b.category;
  return `<div class="book-card" onclick="goBook(${b.id})">
    <div class="bk-cover" style="background:${b.coverBg}">
      <div class="bk-cover-genre">${genre}</div>
      <div class="bk-cover-main-title">${b.title}</div>
      <div class="bk-cover-author-name">${b.author}</div>
    </div>
    <div class="bk-meta">
      <div class="bk-title">${b.title}</div>
      <div class="bk-author">by <a href="#" onclick="event.stopPropagation();filterAuthor('${b.author}')">${b.author}</a></div>
      <div class="bk-desc">${b.desc}</div>
      <div class="bk-format">${b.format}</div>
      <div class="bk-genres">${b.genres.map(g =>
        `<a href="#" onclick="event.stopPropagation();selectCat('${g}')">${g}</a>`
      ).join('')}</div>
      <div class="bk-price">₹${b.price}</div>
      <div class="bk-delivery">Delivery by ${b.delivery}</div>
    </div>
  </div>`;
}

function goBook(id) {
  window.location.href = `book.html?id=${id}`;
}
function filterAuthor(name) {
  const inp = document.getElementById('searchInput');
  if (inp) { inp.value = name; renderHome(); }
}

// ────────────────────────────────────────────────
//  WISHLIST PAGE
// ────────────────────────────────────────────────
function toggleWL(bookId) {
  let wl = getWL();
  const idx = wl.indexOf(bookId);
  if (idx === -1) {
    wl.push(bookId);
    showToast('Added to wishlist ♥');
  } else {
    wl.splice(idx, 1);
    showToast('Removed from wishlist');
  }
  localStorage.setItem('bw_wl', JSON.stringify(wl));
  // re-render if on wishlist page
  const wc = document.getElementById('wishlistContainer');
  if (wc) renderWishlist();
}

function renderWishlist() {
  const el = document.getElementById('wishlistContainer');
  if (!el) return;
  const wl = getWL();
  const books = BOOKS.filter(b => wl.includes(b.id));

  if (!books.length) {
    el.innerHTML = `<div class="empty-wishlist">
      <div class="empty-wl-icon">🤍</div>
      <div class="empty-wl-title">Your wishlist is empty</div>
      <div class="empty-wl-sub">Browse books and add your favourites here.</div>
      <a href="index.html" class="btn-go-home">Browse Books</a>
    </div>`;
    return;
  }

  el.innerHTML = `<div class="wl-grid">${books.map(b => wlCard(b)).join('')}</div>`;
}

function wlCard(b) {
  const icon = COVER_ICONS[b.id] || '📖';
  return `<div class="wl-card">
    <div class="wl-cover" style="background:${b.coverBg}" onclick="goBook(${b.id})">
      <div class="bk-cover-title">${b.coverText}</div>
      <div class="bk-cover-icon">${icon}</div>
      <div class="bk-cover-author">${b.coverAuthorDisplay}</div>
    </div>
    <div class="wl-info">
      <div class="wl-book-title" onclick="goBook(${b.id})">${b.title}</div>
      <div class="wl-book-author">by <span style="color:var(--orange)">${b.author}</span></div>
      <div class="wl-book-format">${b.format}</div>
      <div class="wl-book-price">₹${b.price}</div>
      <div class="wl-book-delivery">Delivery by ${b.delivery}</div>
      <div class="wl-actions">
        <button class="wl-btn-cart" onclick="addToCart(${b.id});showToast('Added to cart ✓')">Add to Cart</button>
        <button class="wl-btn-remove" onclick="toggleWL(${b.id})" title="Remove from wishlist">🗑</button>
      </div>
    </div>
  </div>`;
}

function initWishlist() {
  requireAuth();
  initNavbar();
  refreshBadge();
  renderWishlist();
}

// ────────────────────────────────────────────────
//  LOGOUT (alias used by wishlist.html)
// ────────────────────────────────────────────────
function logout() { doLogout(); }

// ────────────────────────────────────────────────
//  USER MENU (alias used by wishlist.html)
// ────────────────────────────────────────────────
function toggleUserMenu() { toggleDD(); }

// ────────────────────────────────────────────────
//  PAYMENT PAGE
// ────────────────────────────────────────────────
function initPayment() {
  requireAuth();
  initNavbar();
  refreshBadge();
  const total = localStorage.getItem('bw_checkout_total') || '₹0';
  const els = document.querySelectorAll('#payableAmount, #payableAmount2');
  els.forEach(el => { if (el) el.textContent = total; });
}

function completePayment() {
  const cart  = getCart();
  const items = cart.map(c => {
    const b = getAllBooks().find(b => b.id === c.id);
    return b ? { ...b, qty: c.qty } : null;
  }).filter(Boolean);

  const total    = localStorage.getItem('bw_checkout_total') || '₹0';
  const prem     = isPremium();
  const now      = Date.now();
  const DAY      = 24 * 60 * 60 * 1000;
  const HOUR     = 60 * 60 * 1000;

  // Read the delivery type chosen in cart
  const delivType = localStorage.getItem('bw_checkout_delivery') || (prem ? 'prem_same' : 'standard');

  // Map delivery type → total delivery window in ms
  const MIN      = 60 * 1000;

  const windowMs = {
    standard:  6 * DAY,
    fast:      3 * DAY,
    sameday:   2 * MIN,     // demo: 2 minutes
    prem_same: 2 * MIN,     // demo: 2 minutes
    prem_next: 1 * DAY,
  }[delivType] || 6 * DAY;

  const deliveryDays = {
    standard: 6, fast: 3, sameday: 0, prem_same: 0, prem_next: 1
  }[delivType] || 6;

  // Stages: Confirmed(0%) → Packed(10%) → Shipped(40%) → Out(80%) → Delivered(100%)
  const packedAt    = now + Math.round(windowMs * 0.10);
  const shippedAt   = now + Math.round(windowMs * 0.40);
  const outAt       = now + Math.round(windowMs * 0.80);
  const deliveredAt = now + windowMs;

  const fmtDate = ms => new Date(ms).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' });

  // Human-readable delivery label for the order card
  const delivLabel = {
    standard:  'Standard (4–6 days)',
    fast:      'Fast (2–3 days)',
    sameday:   'Same-Day',
    prem_same: '👑 Same-Day (Premium)',
    prem_next: '👑 Next-Day (Premium)',
  }[delivType] || 'Standard';

  const orders = getOrders();
  const order  = {
    id:           Date.now().toString(36).toUpperCase(),
    date:         fmtDate(now),
    placedAt:     now,
    packedAt,
    shippedAt,
    outAt,
    deliveredAt,
    deliveryDays,
    delivType,
    delivLabel,
    isPremOrder:  prem,
    status:       'Confirmed',
    items,
    total
  };
  orders.unshift(order);
  localStorage.setItem('bw_orders', JSON.stringify(orders));
  saveCart([]);
  localStorage.removeItem('bw_checkout_delivery');

  // Store confirmed items for display
  localStorage.setItem('bw_confirmed', JSON.stringify(items));
  window.location.href = 'confirmation.html';
}

// ────────────────────────────────────────────────
//  CONFIRMATION PAGE
// ────────────────────────────────────────────────
function initConfirmation() {
  requireAuth();
  initNavbar();
  refreshBadge();
  const el    = document.getElementById('confirmedItems');
  const items = JSON.parse(localStorage.getItem('bw_confirmed') || '[]');
  if (!el) return;
  if (!items.length) {
    el.innerHTML = '<div style="color:#888;font-size:13px">No items found.</div>';
    return;
  }
  el.innerHTML = items.map(it =>
    `<div class="confirmed-item">${it.title}</div>`
  ).join('');
  localStorage.removeItem('bw_confirmed');
}

// ────────────────────────────────────────────────
//  DELIVERY STATUS HELPERS
// ────────────────────────────────────────────────
const ORDER_STAGES = ['Confirmed', 'Packed', 'Shipped', 'Out for Delivery', 'Delivered'];

/**
 * Returns the live computed status for an order.
 * For orders that have timeline timestamps we auto-advance through stages.
 * For old orders without timestamps we return the stored status as-is.
 */
function computeOrderStatus(order) {
  const s   = (order.status || '').toLowerCase();
  const now = Date.now();
  const MIN = 60 * 1000;

  // Return Requested → Returned after 2 minutes
  if (s === 'return requested') {
    const returnedAt = (order.returnedAt) ||
      (order.returnRequestedAt ? order.returnRequestedAt + 2 * MIN : null);
    if (returnedAt && now >= returnedAt) return 'Returned';
    return order.status;
  }

  // Already terminal
  if (['cancelled', 'returned'].includes(s)) return order.status;

  // Auto-advance delivery stages via timestamps
  if (order.placedAt && order.deliveredAt) {
    if (now >= order.deliveredAt)  return 'Delivered';
    if (now >= order.outAt)        return 'Out for Delivery';
    if (now >= order.shippedAt)    return 'Shipped';
    if (now >= order.packedAt)     return 'Packed';
    return 'Confirmed';
  }
  // Fallback to stored status
  return order.status || 'Confirmed';
}

/**
 * Returns the index (0-4) of the current stage in ORDER_STAGES.
 */
function stageIndex(status) {
  const i = ORDER_STAGES.indexOf(status);
  return i === -1 ? 0 : i;
}

/**
 * Format a timestamp as a short date string.
 */
function fmtTs(ms) {
  if (!ms) return '';
  return new Date(ms).toLocaleDateString('en-IN', { day: 'numeric', month: 'short' });
}

// ────────────────────────────────────────────────
//  RETURN ORDER
// ────────────────────────────────────────────────
const RETURN_REASONS = [
  'Wrong book delivered',
  'Damaged / torn pages',
  'Duplicate order placed',
  'Book quality not as expected',
  'Changed my mind',
  'Other'
];

// Returns true if order is eligible for return
function isReturnEligible(order) {
  if (!order) return false;
  const s = (order.status || '').toLowerCase();
  // Only delivered orders, not already returned/requested
  if (s !== 'delivered') return false;
  // 7-day return window from deliveredAt or order date
  const base = order.deliveredAt || order.dateMs;
  if (base) {
    const diff = Date.now() - base;
    if (diff > 7 * 24 * 60 * 60 * 1000) return false;
  }
  return true;
}

let _returnOrderId = null;

function openReturnSheet(orderId) {
  _returnOrderId = orderId;
  const overlay = document.getElementById('returnSheetOverlay');
  const sheet   = document.getElementById('returnSheet');
  // reset state
  document.querySelectorAll('.return-reason-opt').forEach(el => el.classList.remove('selected'));
  const radios = document.querySelectorAll('input[name="returnReason"]');
  radios.forEach(r => r.checked = false);
  const noteEl = document.getElementById('returnNote');
  if (noteEl) noteEl.value = '';
  document.getElementById('returnSubmitBtn').disabled = true;
  overlay.classList.add('open');
  sheet.classList.add('open');
}

function closeReturnSheet() {
  document.getElementById('returnSheetOverlay').classList.remove('open');
  document.getElementById('returnSheet').classList.remove('open');
  _returnOrderId = null;
}

function selectReturnReason(el, val) {
  document.querySelectorAll('.return-reason-opt').forEach(o => o.classList.remove('selected'));
  el.classList.add('selected');
  el.querySelector('input').checked = true;
  document.getElementById('returnSubmitBtn').disabled = false;
}

function submitReturn() {
  if (!_returnOrderId) return;
  const radio  = document.querySelector('input[name="returnReason"]:checked');
  const reason = radio ? radio.value : '';
  const note   = (document.getElementById('returnNote')?.value || '').trim();
  if (!reason) { showToast('Please select a reason'); return; }

  const orders = getOrders();
  const idx    = orders.findIndex(o => o.id === _returnOrderId);
  if (idx === -1) return;

  orders[idx].status             = 'Return Requested';
  orders[idx].returnReason       = reason;
  orders[idx].returnNote         = note;
  orders[idx].returnRequestedAt  = Date.now();
  orders[idx].returnDate         = new Date().toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' });
  localStorage.setItem('bw_orders', JSON.stringify(orders));

  closeReturnSheet();
  showToast('Return request submitted ✓');
  // re-render orders list
  if (typeof renderOrdersList === 'function') renderOrdersList();
}

