document.addEventListener('DOMContentLoaded', function () {
    const token = localStorage.getItem('token');

    if (!token) {
        window.location.href = "/Html/login.html"; // Redirigir a la página de login si no hay token
        return;
    }

    fetch('http://localhost:8080/api/admin/orders', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(orders => {
        const tableBody = document.getElementById('ordersTableBody');
        orders.forEach(order => {
            const row = document.createElement('tr');

            row.innerHTML = `
                <td>${order.id}</td>
                <td>${order.date ? new Date(order.date).toLocaleString() : 'N/A'}</td>
                <td>${order.state}</td>
                <td>${order.totalPrice ? order.totalPrice.toFixed(2) : 'N/A'}</td>
                <td>${order.user.login}</td>
                <td>${order.user.address}</td> <!-- Nueva celda para la dirección -->
            `;

            tableBody.appendChild(row);
        });
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Ocurrió un error al recuperar los pedidos.');
    });

    document.getElementById('logoutButton').addEventListener('click', logout);
});

function logout() {
    localStorage.removeItem('token');
    window.location.href = "/Html/loginadmin.html";
}
