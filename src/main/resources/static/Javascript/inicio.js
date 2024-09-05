$(document).ready(function() {

    let currentPagePath = window.location.pathname;
    let currentPage = currentPagePath.split('/').pop();

    $('.navbar ul li a').each(function() {
        let linkPath = $(this).attr('href');
        let linkPage = linkPath.split('/').pop();
        if (linkPage === currentPage) {
            $(this).addClass('active');
        }
    });
});


 