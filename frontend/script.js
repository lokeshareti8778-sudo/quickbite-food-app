const API_URL = window.QUICKBITE_API_URL || 'http://localhost:8080/api';
const state = { foods: [], cart: new Map(), category: 'ALL' };
const money = value => `$${Number(value).toFixed(2)}`;

async function loadMenu() {
  const response = await fetch(`${API_URL}/foods`);
  if (!response.ok) throw new Error('Menu could not be loaded');
  state.foods = await response.json();
  renderFilters();
  renderMenu();
}

function renderFilters() {
  const categories = ['ALL', ...new Set(state.foods.map(food => food.category))];
  document.querySelector('#filters').innerHTML = categories.map(category => `<button class="filter ${category === state.category ? 'active' : ''}" data-category="${category}">${category === 'ALL' ? 'All dishes' : category}</button>`).join('');
  document.querySelectorAll('.filter').forEach(button => button.addEventListener('click', () => {
    state.category = button.dataset.category;
    renderFilters();
    renderMenu();
  }));
}

function renderMenu() {
  const foods = state.foods.filter(food => state.category === 'ALL' || food.category === state.category);
  document.querySelector('#menu-grid').innerHTML = foods.map(food => `<article class="food-card"><div class="food-image" style="background-image:url('${food.imageUrl}')"></div><div class="food-content"><h3>${food.name}</h3><p>${food.description}</p><div class="food-meta"><span>${money(food.price)}</span><button class="add-button" aria-label="Add ${food.name} to cart" data-add="${food.id}">+</button></div></div></article>`).join('');
  document.querySelectorAll('[data-add]').forEach(button => button.addEventListener('click', () => addToCart(Number(button.dataset.add))));
}

function addToCart(foodId) { state.cart.set(foodId, (state.cart.get(foodId) || 0) + 1); renderCart(); document.querySelector('#checkout').scrollIntoView({ behavior: 'smooth' }); }
function updateQuantity(foodId, change) { const next = (state.cart.get(foodId) || 0) + change; next > 0 ? state.cart.set(foodId, next) : state.cart.delete(foodId); renderCart(); }
function renderCart() {
  const entries = [...state.cart.entries()];
  document.querySelector('#cart-count').textContent = entries.reduce((sum, [, quantity]) => sum + quantity, 0);
  if (!entries.length) { document.querySelector('#cart-items').innerHTML = '<p class="empty-cart">Your cart is waiting for something tasty.</p>'; document.querySelector('#cart-total').textContent = '$0.00'; return; }
  let total = 0;
  document.querySelector('#cart-items').innerHTML = entries.map(([foodId, quantity]) => { const food = state.foods.find(item => item.id === foodId); const lineTotal = food.price * quantity; total += lineTotal; return `<div class="cart-item"><div><strong>${food.name}</strong><small>${money(food.price)} each</small></div><div class="quantity"><button data-change="-1" data-id="${foodId}">-</button><span>${quantity}</span><button data-change="1" data-id="${foodId}">+</button></div><button class="remove-button" data-remove="${foodId}">Remove</button></div>`; }).join('');
  document.querySelector('#cart-total').textContent = money(total);
  document.querySelectorAll('[data-change]').forEach(button => button.addEventListener('click', () => updateQuantity(Number(button.dataset.id), Number(button.dataset.change))));
  document.querySelectorAll('[data-remove]').forEach(button => button.addEventListener('click', () => { state.cart.delete(Number(button.dataset.remove)); renderCart(); }));
}

document.querySelector('#order-form').addEventListener('submit', async event => {
  event.preventDefault();
  const message = document.querySelector('#form-message');
  if (!state.cart.size) { message.textContent = 'Add at least one dish before placing your order.'; return; }
  const formData = new FormData(event.target);
  const payload = { customerName: formData.get('customerName'), email: formData.get('email'), phone: formData.get('phone'), address: formData.get('address'), items: [...state.cart].map(([foodId, quantity]) => ({ foodId, quantity })) };
  message.textContent = 'Sending your order...';
  try {
    const response = await fetch(`${API_URL}/orders`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
    const result = await response.json();
    if (!response.ok) throw new Error(result.error || 'Order could not be placed');
    document.querySelector('#confirmation-id').textContent = result.orderId;
    document.querySelector('#confirmation-status').textContent = result.status;
    document.querySelector('#confirmation-message').textContent = result.message;
    document.querySelector('#confirmation').hidden = false;
    state.cart.clear(); renderCart(); event.target.reset();
    document.querySelector('#confirmation').scrollIntoView({ behavior: 'smooth' });
  } catch (error) { message.textContent = error.message; }
});

document.querySelector('#cart-link').addEventListener('click', () => document.querySelector('#checkout').scrollIntoView({ behavior: 'smooth' }));
loadMenu().catch(error => { document.querySelector('#menu-grid').innerHTML = `<p class="loading">${error.message}. Start the Spring Boot backend and refresh.</p>`; });