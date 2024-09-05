document.getElementById('addProductForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const productName = document.getElementById('productName').value;
    const productDescription = document.getElementById('productDescription').value;
    const productStock = document.getElementById('productStock').value;
    const productPrice = document.getElementById('productPrice').value;
    const productCategoryId = document.getElementById('productCategoryId').value;
    const productImage = document.getElementById('productImage').value;

    const newProduct = {
        name: productName,
        description: productDescription,
        stock: productStock,
        price: productPrice,
        categoryId: productCategoryId,
        image: productImage
    };

    fetch('http://localhost:8080/api/item', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify(newProduct)
    })
    .then(response => response.json())
    .then(data => {
        if (data.id) {
            alert('Producto agregado exitosamente.');
            // Limpiar el formulario
            document.getElementById('addProductForm').reset();
        } else {
            alert('Error al agregar el producto.');
        }
    })
    .catch(error => console.error('Error:', error));
    document.getElementById('logoutButton').addEventListener('click', logout);
});

function logout() {
    localStorage.removeItem('token');
    window.location.href = "/Html/loginadmin.html";
}