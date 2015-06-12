var toggleError = true;

function loginError() {    
     $('h1').toggleClass('error');
}

$('#err_email').click(function() {
     $('h1').toggleClass('success');
});

$('#err_password').click(function() {
    $('h1').toggleClass('success');
});


$('form').submit(function(event) {
    event.preventDefault();
    $('.wrapper').addClass('form-success');
    
    $('form').fadeOut(700, function() {
        this.submit();
    });
       
});
    