(function () {
    function getCookie(name) {
        const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
        if (match) {
            return decodeURIComponent(match[2]);
        }
        return null;
    }

    document.addEventListener('DOMContentLoaded', function () {
        const cookieName = 'visitasApp';
        const displayElement = document.getElementById('visitas-count');

        if (!displayElement) return;

        const cookieValue = getCookie(cookieName);

        let visitas = 0;

        if (cookieValue !== null && !isNaN(cookieValue)) {
            visitas = parseInt(cookieValue, 10);
        }

        displayElement.textContent = visitas.toString();

        console.log(`Cookie '${cookieName}' leída:`, visitas);
    });
})();
