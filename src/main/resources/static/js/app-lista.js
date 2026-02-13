document.addEventListener('DOMContentLoaded', () =>{

    const table = document.querySelector('#listaAlbumes');

    if (table) {
        table.addEventListener('click', async (event) => {
            const link = event.target.closest('a');

            if (!link || !link.classList.contains('borrarAlbumLink')) return;

            event.preventDefault();

            const tr = link.closest('tr');
            const idEl = tr && tr.querySelector('.albumId');
            const id = idEl ? idEl.textContent.trim().replace('#', '') : null;

            if (!id) {
                console.error('No se pudo encontrar el ID del álbum');
                return;
            }

            const url = "/admin/albumes/" + id + "/delete/confirm";
            try {
                const response = await fetch(url);
                if (!response.ok) throw new Error(`Response status: ${response.status}`);
                const html = await response.text();

                const modalContainer = document.querySelector('#placeholder-modal');
                if (modalContainer) {
                    modalContainer.innerHTML = html;
                    const modalEl = document.querySelector('#delete-modal');
                    if (modalEl) {
                        const modal = new bootstrap.Modal(modalEl);
                        modal.show();
                    }
                }
            } catch (error) {
                console.error(error.message);
            }
        });
    }

    const buscador = document.querySelector('#buscador');

    if (buscador) {
        const debounce = (func, wait) => {
            let timeout;
            return function(...args) {
                clearTimeout(timeout);
                timeout = setTimeout(() => func.apply(this, args), wait);
            };
        };

        const realizarBusqueda = async () => {
            const url = "/admin/albumes/filter?";
            const queryParams = new URLSearchParams({nombre: buscador.value}).toString();
            try {
                const response = await fetch(url + queryParams);
                if (!response.ok) throw new Error(`Response status: ${response.status}`);

                const html = await response.text();
                const lista = document.querySelector('#listaAlbumes');
                if (lista) lista.innerHTML = html;

            } catch (error) {
                console.error(error.message);
            }
        };

        buscador.addEventListener('keyup', debounce(realizarBusqueda, 300));
    }

});
