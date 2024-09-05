function login() {
    const data = {
        username: this.username,
        password: this.password
    };

    fetch('http://localhost:8080/api/authenticate', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(response => response.json())
    .then(data => {
        console.log(data); // Imprimir la respuesta del servidor para depurar
        if (data.token) {
            // Obtener los roles del usuario
            fetch('http://localhost:8080/api/account', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${data.token}`,
                    'Content-Type': 'application/json'
                }
            })
            .then(accountResponse => accountResponse.json())
            .then(accountData => {
                console.log(accountData); // Imprimir la respuesta del servidor para depurar
                // Verificar si los roles están presentes en la respuesta
                if (accountData.authorities && Array.isArray(accountData.authorities)) {
                    const roles = accountData.authorities.map(authority => authority.name);
                    if (roles.includes('ROLE_USER')) {
                        this.token = data.token;
                        // Guardar el token en el local storage
                        localStorage.setItem('token', this.token);
                        window.location.href = "productos.html";
                    } else {
                        alert('No tienes permisos para iniciar sesión como usuario.');
                    }
                } else {
                    alert('No se pudo determinar el rol del usuario. Por favor, contacta al soporte.');
                }
            })
            .catch(error => {
                console.error('Error al obtener los roles del usuario:', error);
                alert('Ocurrió un error al obtener los roles del usuario. Por favor, intenta de nuevo.');
            });
        } else {
            alert('Usuario o contraseña incorrectos.');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Ocurrió un error. Por favor, intenta de nuevo.');
    });
}


function signup() {
    window.location.href = "/Html/signup.html"; 
}

function goBack() {
    window.location.href = "/Html/index.html";
}
