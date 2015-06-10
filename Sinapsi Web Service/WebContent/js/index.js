$('form').submit(function(event) {
    event.preventDefault();
    $('.wrapper').addClass('form-success');
    $('form').fadeOut(700, function() {
        this.submit();
    });
       
});
    