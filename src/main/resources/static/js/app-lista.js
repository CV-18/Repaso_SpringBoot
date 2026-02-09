document.addEventListener('DOMContentLoaded', () =>{
    const table = document.querySelector('#listaAlbumes');
    if (!table) return;

    table.addEventListener('click', async (event) =>{
        const link = event.target.closest('a');

        const isDeleted = link.classList.contains('borrarAlbumLink');
        if (!isDeleted) return;

        event.preventDefault();

        const tr = link.closest('tr');
        const idEl = tr && tr.querySelector('.albumId');
        const id = idEl ? idEl.textContent.trim() : null;

        const url = "/admin/Albumes/" + id + "/delete/confirm";
        try {
            const response = await fetch(url);
            if (!response.ok) throw new Error(`Response status: ${response.status}`);
            const html = await response.text();
            document.querySelector('#placeholder-modal').innerHTML = html;

            const modalEl = document.querySelector('#delete-modal');
            if (modalEl) {
                const modal = new bootstrap.Modal(modalEl);
                modal.show();
            } else {
                console.error('Modal no encontrado en el HTML recibido');
            }
        } catch (error) {
            console.error(error.message);
        }

    })

    const buscador = document.querySelector('#buscador');
    buscador.addEventListener('keyup', async () => {
        const url = "/admin/albumes/filter?";
        const queryParams = new URLSearchParams({numero: buscador.value}).toString();
        try {
            const response = await fetch(url + queryParams);
            if (!response.ok) throw new Error(`Response status: ${response.status}`);

            const html = await response.text();
            document.querySelector('#listaAlbumes').innerHTML = html;
        } catch (error) {
            console.error(error.message);
        }


    })
})
