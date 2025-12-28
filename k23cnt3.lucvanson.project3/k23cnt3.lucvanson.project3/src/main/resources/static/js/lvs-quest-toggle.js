// Quest Dashboard - Show More/Less Toggle
function toggleShowMore(button) {
    var type = button.getAttribute('data-type');
    var grid = document.querySelector('[data-type="' + type + '"]');
    var hiddenCards = grid.querySelectorAll('.hidden-quest');
    var btnText = button.querySelector('.btn-text');
    var icon = button.querySelector('i');

    var isExpanded = grid.classList.contains('expanded');

    if (isExpanded) {
        // Collapse
        hiddenCards.forEach(function (card) {
            card.style.display = 'none';
        });
        grid.classList.remove('expanded');
        icon.className = 'fas fa-chevron-down mr-2';
        btnText.textContent = 'Show More (' + hiddenCards.length + ' hidden)';
    } else {
        // Expand - remove inline style to let grid handle it
        hiddenCards.forEach(function (card) {
            card.style.display = '';
        });
        grid.classList.add('expanded');
        icon.className = 'fas fa-chevron-up mr-2';
        btnText.textContent = 'Show Less';
    }
}
