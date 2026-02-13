document.addEventListener('DOMContentLoaded', () =>{

    const lnkSalir = document.querySelector('#logoutLink');

    if (lnkSalir) {
        lnkSalir.addEventListener('click',  (event) => {
            event.preventDefault();
            const form = document.querySelector('#logoutForm');
            if (form) form.submit();
        });
    }

});
