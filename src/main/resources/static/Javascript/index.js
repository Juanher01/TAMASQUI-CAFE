function navigate(role) {
    if (role === 'usuario') {
        window.location.href = '/Html/login.html';
    } else if (role === 'administrador') {
        window.location.href = '/Html/loginadmin.html';
    }

}
