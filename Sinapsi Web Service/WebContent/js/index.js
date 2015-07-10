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
    $('#cog').css("-webkit-animation", "rotation 1s infinite linear");
    $('form').fadeOut(700, function() {
        this.submit();
    });
    
       
});

function register() {
    var email = document.getElementById('email').value;
    var password =document.getElementById('password').value
                     
    if(email.length == 0 || passowrd.length == 0)
       $('h1').toggleClass('error');
    else {
         var xmlHttp = new XMLHttpRequest();
        xmlHttp.open( "GET", "register?email=${email}&passoword=${password}", false);
        xmlHttp.send( null );
        return xmlHttp.responseText;
    }
}
    