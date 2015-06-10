<%@ page 
    language="java" 
    contentType="text/html; charset=utf-8"
    pageEncoding="utf-8" 
%>
<!doctype html>
<html>
    <head>
        <meta charset="utf-8"/>
        <title>Sinapsi</title>
        <meta name="vieport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="css/style.css">
        <link href='http://fonts.googleapis.com/css?family=Montserrat' rel='stylesheet' type='text/css'>
        <script type="application/javascript" src="js/particleground.js"></script>
        <script type="application/javascript" src="js/demo.js"></script>
        <script src='http://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.3/jquery.min.js'></script>
    </head>
    
    <body>
        <%
            String email = null;
            Cookie[] cookies = request.getCookies();
            if(cookies != null) {
                for(Cookie cookie : cookies) {
                    if(cookie.getName().equals("user")) 
                        email = cookie.getValue();
                }
            }
            if(email == null) 
                response.sendRedirect("login.html");
        %>
        
        <div id="particles">
            <div id="intro">
                <div class="wrapper">
                <div class="container">
                    <h1>Sinapsi</h1>   
                    <h3>Welcome <%=email %></h3>
                    
                </div>
                </div>   
            </div>
        </div>
        <footer>
            © 2015 Sinapsi
        </footer>
        <script type="application/javascript" src="js/index.js"></script>  
    </body>
</html>