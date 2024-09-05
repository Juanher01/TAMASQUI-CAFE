document.addEventListener('DOMContentLoaded', function () {
    const token = localStorage.getItem('token');

    if (!token) {
        window.location.href = "login.html"; // Redirigir a la página de login si no hay token
        return;
    }

    fetch('http://localhost:8080/api/user/orders', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Error en la respuesta de la red');
        }
        return response.json();
    })
    .then(orders => {
        const tableBody = document.getElementById('userOrdersTableBody');
        tableBody.innerHTML = ''; // Limpiar el contenido existente
        orders.forEach(order => {
            const row = document.createElement('tr');

            // Crear una lista de items
            let items = '';
            if (order.carts && order.carts.length > 0) {
                items = '<ul>';
                order.carts.forEach(cart => {
                    items += `<li>${cart.item.name} - ${cart.amount} x ${cart.unitePrice.toFixed(2)}</li>`;
                });
                items += '</ul>';
            } else {
                items = 'N/A';
            }

            row.innerHTML = `
                <td>${order.id}</td>
                <td>${order.date ? new Date(order.date).toLocaleString() : 'N/A'}</td>
                <td>${order.state}</td>
                <td>${order.totalPrice ? order.totalPrice.toFixed(2) : 'N/A'}</td>
                <td>${items}</td>
            `;

            tableBody.appendChild(row);
        });
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Ocurrió un error al recuperar tus pedidos.');
    });
});
