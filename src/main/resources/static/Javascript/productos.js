const data = () => {
    return {
        countPage: 0,
        cartItems: [],
        productos: [],
        showCart: false,

        logout() {
            localStorage.removeItem('token');
            window.location.href = "login.html";
        },

        fetchItems() {
            fetch(`http://localhost:8080/api/items?page=${this.countPage}&size=10`)
            .then(response => response.json())
            .then(data => {
                if(this.countPage > 0){
                    this.productos.push(...data)
                }else{
                    this.productos = data;
                }
                this.countPage++
                console.log(this.productos);
            })
        },
        fecthCart() {
            fetch('http://localhost:8080/api/cart', {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            })
            .then(response => response.json())
            .then(data => {
                this.cartItems = data.carts || [];
                console.log(this.cartItems)
            }).catch(e => console.log('Error:', e))
        },
        async addToCart(productoId, cartItems){
            fetch('http://localhost:8080/api/cart', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${localStorage.getItem('token')}`
                    },
                    body: JSON.stringify({
                        productId: productoId,
                        amount: 1
                    })
                })
                .then(response => {
                    if(response.status !== 201){
                        alert('Error al agregar el producto al carrito.');
                    }
                    return response.json();
                })
                .then(data => {
                    this.cartItems.push(data);
                    this.showCart = true;
                    console.log(data)
                })
                .catch(e => console.error('Error:', e))
        },
        async emptyCart(cartItems){
            fetch('http://localhost:8080/api/cart', {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            })
            .then(response => {
                if(response.status === 204){
                    this.cartItems = [];
                }
            })
            .catch(e => console.error('Error:', e))
        },
        async removeFromCart(productId){
            fetch(`http://localhost:8080/api/cart/item/${productId}`, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${localStorage.getItem('token')}`
                    }
                })
                .then(response => {
                if(response.status === 204){
                    this.cartItems = this.cartItems.filter(cart => cart.id !== productId);
                }
            })
            .catch(e => console.error('Error:', e));
        },
        async checkout(cart){
            fetch('http://localhost:8080/api/checkout', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${localStorage.getItem('token')}`
                    },
                    body: JSON.stringify({
                        shopId: this.cartItems[0].shopping.id,
                        status: "PAID"
                    })
                })
                .then(response => {
                    if(response.status !== 201){
                        alert('Error al agregar al pagar');
                    }
                    return response.json();
                })
                .then(data => {
                    window.open(data.initPoint, '_blank')
                    console.log(data)
                })
                .catch(e => console.error('Error:', e))

        }
    };
}
