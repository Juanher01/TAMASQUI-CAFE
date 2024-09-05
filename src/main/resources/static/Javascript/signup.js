function registrationForm() {
    return {
        login: '',
        name: '',
        password: '',
        email: '',
        address: '',
        phoneNumber: '',

        register() {
            const data = {
                login: this.login,
                name: this.name,
                password: this.password,
                email: this.email,
                address: this.address,
                phoneNumber: this.phoneNumber
            };

            fetch('http://localhost:8080/api/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error en la solicitud.');
                }
                return response.json();
            })
            .then(data => {
                if (data.id) {
                    alert('Usuario registrado exitosamente.');
                    // Limpiar el formulario después del registro exitoso
                    this.login = '';
                    this.name = '';
                    this.password = '';
                    this.email = '';
                    this.address = '';
                    this.phoneNumber = '';
                    // Redirigir al usuario a la página de login
                    window.location.href = "login.html";
                } else {
                    throw new Error('Error al registrar usuario.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error al registrar usuario. Por favor, verifica los datos e intenta nuevamente.');
            });
        },

        cancelar() {
            window.location.href = "login.html";
        }
    };
}

document.addEventListener('alpine:init', () => {
    Alpine.data('registrationForm', registrationForm);
});
